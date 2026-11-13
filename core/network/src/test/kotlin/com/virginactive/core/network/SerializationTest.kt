package com.virginactive.core.network

import com.virginactive.core.network.dto.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class SerializationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `generated request serializer escapes credentials and round trips`() {
        val request = LoginRequest("member+test@example.com", "quote\" slash\\ newline\n")
        assertEquals(request, json.decodeFromString<LoginRequest>(json.encodeToString(request)))
    }

    @Test
    fun `unknown fields do not break token decoding`() {
        val response =
            json.decodeFromString<TokenResponse>(
                """{"accessToken":"a","refreshToken":"r","expiresIn":300,"future":true}""")
        assertEquals(300L, response.expiresIn)
    }

    @Test
    fun `missing availability never silently becomes zero`() {
        try {
            json.decodeFromString<ClassDto>(
                """{"classId":"id","clubId":"club","title":"Yoga","startsAt":"2026-11-13T07:00:00Z","endsAt":"2026-11-13T08:00:00Z","timezone":"Europe/London","spots":20,"status":"available","userBookingStatus":"none"}""")
            fail("Missing required availability should be rejected")
        } catch (_: SerializationException) {}
    }
}
