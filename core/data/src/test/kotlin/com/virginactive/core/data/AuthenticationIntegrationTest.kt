package com.virginactive.core.data

import com.virginactive.core.domain.Result
import com.virginactive.core.network.*
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.*
import org.junit.*
import org.junit.Assert.*

class AuthenticationIntegrationTest {
    private lateinit var server: MockWebServer

    @Before
    fun before() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun after() {
        server.shutdown()
    }

    private fun tokens(access: String, refresh: String, ttl: Int = 300) =
        """{"accessToken":"$access","refreshToken":"$refresh","expiresIn":$ttl}"""

    private fun response(body: String, code: Int = 200) =
        MockResponse()
            .setResponseCode(code)
            .setBody(body)
            .addHeader("Content-Type", "application/json")

    @Test
    fun `concurrent expired requests rotate tokens once`() = runBlocking {
        val refreshes = AtomicInteger()
        server.dispatcher =
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse =
                    when (request.path) {
                        "/auth/login" -> response(tokens("old", "r1", 0))
                        "/auth/refresh" -> {
                            refreshes.incrementAndGet()
                            assertTrue(request.body.readUtf8().contains("r1"))
                            response(tokens("new", "r2"))
                        }
                        else ->
                            if (request.getHeader("Authorization") == "Bearer new")
                                response(
                                    """{"id":"member","firstName":"Member","homeClub":{"id":"club","name":"Club"}}""")
                            else response("{}", 401)
                    }
            }
        val sessions = TokenStore()
        val api = TestClient(server.url("/").toString(), sessions)
        api.login("member", "password")
        coroutineScope { List(8) { async { api.member() } }.awaitAll() }
        assertEquals(1, refreshes.get())
        assertTrue(sessions.signedIn.value)
    }

    @Test
    fun `rejected refresh clears session`() = runBlocking {
        server.enqueue(response(tokens("old", "r1", 0)))
        server.enqueue(response("{}", 401))
        server.enqueue(response("""{"error":"InvalidRefreshToken","message":"Expired"}""", 401))
        val sessions = TokenStore()
        val api = TestClient(server.url("/").toString(), sessions)
        api.login("member", "password")
        try {
            api.member()
            fail("Expected unauthorized")
        } catch (error: ApiException) {
            assertEquals(401, error.status)
        }
        assertFalse(sessions.signedIn.value)
    }

    @Test
    fun `transient refresh failure preserves session for retry`() = runBlocking {
        server.enqueue(response(tokens("old", "r1")))
        server.enqueue(response("{}", 401))
        server.enqueue(response("{}", 500))
        val sessions = TokenStore()
        val api = TestClient(server.url("/").toString(), sessions)
        api.login("member", "password")
        try {
            api.member()
            fail("Expected transient failure")
        } catch (error: java.io.IOException) {
            assertEquals("Couldn't refresh the session (500).", error.message)
        }
        assertTrue(sessions.signedIn.value)
    }

    @Test
    fun `mutation failure is not retried and exposes request id`() = runBlocking {
        server.enqueue(response(tokens("access", "refresh")))
        server.enqueue(
            response("""{"error":"Internal","message":"Try later","requestId":"req-123"}""", 500))
        val sessions = TokenStore()
        val api = TestClient(server.url("/").toString(), sessions)
        api.login("member", "password")
        try {
            api.book("club", "id::2026-11-13")
            fail("Expected server error")
        } catch (error: ApiException) {
            assertEquals("req-123", error.requestId)
        }
        assertEquals(2, server.requestCount)
    }

    @Test
    fun `safe read retries once after server error`() = runBlocking {
        server.enqueue(response(tokens("access", "refresh")))
        server.enqueue(response("{}", 503))
        server.enqueue(
            response(
                """{"id":"member","firstName":"Member","homeClub":{"id":"club","name":"Club"}}"""))
        val sessions = TokenStore()
        val api = TestClient(server.url("/").toString(), sessions)
        api.login("member", "password")
        assertTrue(api.repository.member() is Result.Success)
        assertEquals(3, server.requestCount)
    }

    @Test
    fun `unauthorized request refreshes and replays with new credentials`() = runBlocking {
        server.enqueue(response(tokens("access", "refresh")))
        server.enqueue(response("{}", 401))
        server.enqueue(response(tokens("new", "next")))
        server.enqueue(
            response(
                """{"id":"member","firstName":"Member","homeClub":{"id":"club","name":"Club"}}"""))
        val sessions = TokenStore()
        val api = TestClient(server.url("/").toString(), sessions)
        api.login("member", "password")
        api.member()
        server.takeRequest()
        server.takeRequest()
        server.takeRequest()
        assertEquals("Bearer new", server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun `logout abandons in-flight requests so they cannot replay under another account`() =
        runBlocking {
            server.enqueue(response(tokens("first", "r1")))
            server.enqueue(
                response("{}", 401)
                    .setHeadersDelay(500, java.util.concurrent.TimeUnit.MILLISECONDS))
            server.enqueue(response(tokens("second", "r2")))
            val sessions = TokenStore()
            val api = TestClient(server.url("/").toString(), sessions)
            api.login("first", "password")
            server.takeRequest()
            val pending =
                async(Dispatchers.IO) {
                    try {
                        api.member()
                        false
                    } catch (error: java.io.IOException) {
                        true
                    }
                }
            server.takeRequest()
            api.logout()
            api.login("second", "password")
            assertTrue(pending.await())
            assertTrue(sessions.signedIn.value)
            assertEquals(3, server.requestCount)
        }

    @Test
    fun `logout does not wait for a slow token refresh`() = runBlocking {
        server.enqueue(response(tokens("old", "r1", 0)))
        server.enqueue(
            response(tokens("new", "r2")).setHeadersDelay(3, java.util.concurrent.TimeUnit.SECONDS))
        val sessions = TokenStore()
        val api = TestClient(server.url("/").toString(), sessions)
        api.login("member", "password")
        server.takeRequest()
        val pending = async(Dispatchers.IO) { runCatching { api.member() } }
        server.takeRequest()
        val started = System.nanoTime()
        api.logout()
        assertTrue(System.nanoTime() - started < 1_500_000_000L)
        assertFalse(sessions.signedIn.value)
        pending.await()
        Unit
    }

    @Test
    fun `login serializes credentials with kotlinx converter`() = runBlocking {
        server.enqueue(response(tokens("access", "refresh")))
        val sessions = TokenStore()
        val api = TestClient(server.url("/").toString(), sessions)
        api.login("member+test@example.com", "quoted\"password")
        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/auth/login", request.path)
        assertEquals("application/json", request.getHeader("Accept"))
        val body = kotlinx.serialization.json.Json.parseToJsonElement(request.body.readUtf8())
        assertEquals(
            kotlinx.serialization.json.Json.parseToJsonElement(
                """{"username":"member+test@example.com","password":"quoted\"password"}"""),
            body)
    }

    @Test
    fun `cancel accepts empty 204 and encodes class id as one path segment`() = runBlocking {
        server.enqueue(response(tokens("access", "refresh")))
        server.enqueue(MockResponse().setResponseCode(204))
        val sessions = TokenStore()
        val api = TestClient(server.url("/").toString(), sessions)
        api.login("member", "password")
        api.cancel("club", "id::2026-11-13/extra")
        server.takeRequest()
        val request = server.takeRequest()
        assertEquals("DELETE", request.method)
        assertEquals(
            listOf("clubs", "club", "classes", "id::2026-11-13/extra", "bookings"),
            request.requestUrl!!.pathSegments)
        assertEquals("Bearer access", request.getHeader("Authorization"))
        assertEquals(0L, request.bodySize)
    }

    @Test
    fun `timetable uses typed response and optional date query`() = runBlocking {
        server.enqueue(response(tokens("access", "refresh")))
        val table =
            """{"clubId":"club","weekStart":"2026-11-09","weekEnd":"2026-11-15","selectedDate":"2026-11-13","days":[]}"""
        server.enqueue(response(table))
        server.enqueue(response(table))
        val sessions = TokenStore()
        val api = TestClient(server.url("/").toString(), sessions)
        api.login("member", "password")
        assertEquals("club", api.timetable("club", "2026-11-13").clubId)
        api.timetable("club", null)
        server.takeRequest()
        assertEquals("2026-11-13", server.takeRequest().requestUrl!!.queryParameter("date"))
        assertNull(server.takeRequest().requestUrl!!.query)
    }

    @Test
    fun `repository owns session and logout never makes an HTTP request`() = runBlocking {
        server.enqueue(response(tokens("access", "refresh")))
        val sessions = TokenStore()
        val api = TestClient(server.url("/").toString(), sessions)
        assertFalse(sessions.signedIn.value)
        api.login("member", "password")
        assertTrue(sessions.signedIn.value)
        api.logout()
        assertFalse(sessions.signedIn.value)
        try {
            api.member()
            fail("Expected unauthenticated request to be blocked")
        } catch (error: ApiException) {
            assertEquals(401, error.status)
        }
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `member from login is reused and cleared on logout`() = runBlocking {
        val user = """{"id":"u1","firstName":"Avid","homeClub":{"id":"club_a","name":"A"}}"""
        server.enqueue(
            response("""{"accessToken":"a","refreshToken":"r","expiresIn":300,"user":$user}"""))
        server.enqueue(response(tokens("b", "r2")))
        server.enqueue(
            response("""{"id":"u2","firstName":"Swim","homeClub":{"id":"club_b","name":"B"}}"""))
        val sessions = TokenStore()
        val api = TestClient(server.url("/").toString(), sessions)
        api.login("first", "password")
        val first = (api.repository.member() as Result.Success).data
        assertEquals("club_a", first.homeClubId)
        assertEquals(1, server.requestCount)
        api.logout()
        api.login("second", "password")
        val second = (api.repository.member() as Result.Success).data
        assertEquals("club_b", second.homeClubId)
        assertEquals(3, server.requestCount)
    }

    @Test
    fun `malformed login user does not fail sign in`() = runBlocking {
        server.enqueue(
            response(
                """{"accessToken":"a","refreshToken":"r","expiresIn":300,"user":{"id":"u1"}}"""))
        val sessions = TokenStore()
        val api = TestClient(server.url("/").toString(), sessions)
        assertTrue(api.login("member", "password") is Result.Success)
        assertTrue(sessions.signedIn.value)
    }

    private class TestClient(baseUrl: String, sessions: TokenStore) {
        private val members = MemberCache()
        private val network =
            NetworkDataSource(
                baseUrl, createHttpClient(baseUrl, sessions, Json { ignoreUnknownKeys = true }))
        private val auth = AuthRepositoryImpl(network, sessions, members, Dispatchers.IO)
        val repository = FitnessRepositoryImpl(network, members)

        suspend fun login(username: String, password: String) = auth.login(username, password)

        suspend fun logout() = auth.logout()

        suspend fun member() = network.member()

        suspend fun book(clubId: String, classId: String) = network.book(clubId, classId)

        suspend fun cancel(clubId: String, classId: String) = network.cancel(clubId, classId)

        suspend fun timetable(clubId: String, date: String?) = network.timetable(clubId, date)
    }
}
