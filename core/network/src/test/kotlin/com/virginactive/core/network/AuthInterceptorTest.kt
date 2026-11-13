package com.virginactive.core.network

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.Assert.*

class AuthInterceptorTest {
    private lateinit var server: MockWebServer

    @Before
    fun setup() {
        server = MockWebServer().apply { start() }
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    private fun response(body: String, code: Int = 200) =
        MockResponse()
            .setResponseCode(code)
            .setBody(body)
            .addHeader("Content-Type", "application/json")

    private fun signedIn() = TokenStore().apply { save(Tokens("access", "refresh")) }

    @Test
    fun `headers are automatic and excluded from login`() = runBlocking {
        server.enqueue(
            response("""{"accessToken":"access","refreshToken":"refresh","expiresIn":300}"""))
        server.enqueue(
            response(
                """{"id":"member","firstName":"Member","homeClub":{"id":"club","name":"Club"}}"""))
        val base = server.url("/").toString()
        val api = NetworkDataSource(base, createHttpClient(base, signedIn(), Json))
        api.login("member", "password")
        api.member()
        assertNull(server.takeRequest().getHeader("Authorization"))
        assertEquals("Bearer access", server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun `credentials never attach to a different origin`() {
        val other = MockWebServer().apply { start() }
        try {
            other.enqueue(MockResponse().setResponseCode(204))
            val client = createHttpClient(server.url("/").toString(), signedIn(), Json)
            client
                .newCall(
                    Request.Builder()
                        .url(other.url("/me"))
                        .header("Authorization", "stale")
                        .build())
                .execute()
                .close()
            assertNull(other.takeRequest().getHeader("Authorization"))
        } finally {
            other.shutdown()
        }
    }

    @Test
    fun `second unauthorized response stops and clears the rejected session`() = runBlocking {
        server.enqueue(response("{}", 401))
        server.enqueue(response("""{"accessToken":"new","refreshToken":"next","expiresIn":300}"""))
        server.enqueue(response("{}", 401))
        val tokens = signedIn()
        val base = server.url("/").toString()
        val api = NetworkDataSource(base, createHttpClient(base, tokens, Json))
        try {
            api.member()
            fail("Expected 401")
        } catch (error: ApiException) {
            assertEquals(401, error.status)
        }
        assertNull(tokens.current)
        assertEquals(3, server.requestCount)
        server.takeRequest()
        val refresh = server.takeRequest()
        assertEquals("/auth/refresh", refresh.path)
        assertNull(refresh.getHeader("Authorization"))
        assertEquals("Bearer new", server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun `expiring token refreshes before the request instead of after a 401`() = runBlocking {
        server.enqueue(response("""{"accessToken":"new","refreshToken":"next","expiresIn":300}"""))
        server.enqueue(
            response(
                """{"id":"member","firstName":"Member","homeClub":{"id":"club","name":"Club"}}"""))
        val tokens = TokenStore().apply { save(Tokens("old", "refresh", Instant.EPOCH)) }
        val base = server.url("/").toString()
        NetworkDataSource(base, createHttpClient(base, tokens, Json)).member()
        assertEquals("/auth/refresh", server.takeRequest().path)
        assertEquals("Bearer new", server.takeRequest().getHeader("Authorization"))
        assertEquals(2, server.requestCount)
        assertEquals("new", tokens.current?.accessToken)
    }

    @Test
    fun `failed proactive refresh falls back to the current token`() = runBlocking {
        server.enqueue(response("{}", 500))
        server.enqueue(
            response(
                """{"id":"member","firstName":"Member","homeClub":{"id":"club","name":"Club"}}"""))
        val expiring = Tokens("old", "refresh", Instant.EPOCH)
        val tokens = TokenStore().apply { save(expiring) }
        val base = server.url("/").toString()
        NetworkDataSource(base, createHttpClient(base, tokens, Json)).member()
        server.takeRequest()
        assertEquals("Bearer old", server.takeRequest().getHeader("Authorization"))
        assertEquals(expiring, tokens.current)
    }

    @Test
    fun `login records expiry from expiresIn`() = runBlocking {
        server.enqueue(
            response("""{"accessToken":"access","refreshToken":"refresh","expiresIn":300}"""))
        val now = Instant.parse("2026-11-13T10:00:00Z")
        val base = server.url("/").toString()
        val tokens = TokenStore()
        val login =
            NetworkDataSource(
                    base,
                    createHttpClient(base, tokens, Json),
                    clock = Clock.fixed(now, ZoneOffset.UTC))
                .login("member", "password")
        assertEquals(now.plusSeconds(300), login.tokens.expiresAt)
    }

    @Test
    fun `login unauthorized never triggers refresh or clears an existing session`() = runBlocking {
        server.enqueue(response("{}", 401))
        val tokens = signedIn()
        val base = server.url("/").toString()
        val api = NetworkDataSource(base, createHttpClient(base, tokens, Json))
        try {
            api.login("invalid", "password")
            fail("Expected 401")
        } catch (error: ApiException) {
            assertEquals(401, error.status)
        }
        assertNotNull(tokens.current)
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `malformed refresh response fails as IO and keeps the session`() = runBlocking {
        server.enqueue(response("{}", 401))
        server.enqueue(response("""{"accessToken":"","refreshToken":"next","expiresIn":300}"""))
        val tokens = signedIn()
        val base = server.url("/").toString()
        val api = NetworkDataSource(base, createHttpClient(base, tokens, Json))
        try {
            api.member()
            fail("Expected IOException")
        } catch (error: java.io.IOException) {
            assertEquals("The server returned an invalid session.", error.message)
        }
        assertEquals(Tokens("access", "refresh"), tokens.current)
    }
}
