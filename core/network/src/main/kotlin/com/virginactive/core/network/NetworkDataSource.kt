package com.virginactive.core.network

import com.virginactive.core.network.auth.*
import com.virginactive.core.network.dto.*
import java.time.Clock
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

data class LoginResult(val tokens: Tokens, val member: MemberDto?)

class NetworkDataSource(
    baseUrl: String,
    private val client: OkHttpClient,
    private val json: Json = Json { ignoreUnknownKeys = true },
    private val clock: Clock = Clock.systemUTC(),
) {
    private val service: RetrofitService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RetrofitService::class.java)

    suspend fun login(username: String, password: String): LoginResult {
        val response =
            json.safeApiCall { service.login(LoginRequest(username, password)) }.requiredBody()
        val member =
            try {
                response.user?.let { json.decodeFromJsonElement(MemberDto.serializer(), it) }
            } catch (_: SerializationException) {
                null
            }
        return LoginResult(response.toTokens(clock), member)
    }

    suspend fun member(): MemberDto = json.safeApiCall { service.member() }.requiredBody()

    suspend fun home(): ManifestDto = json.safeApiCall { service.home() }.requiredBody()

    suspend fun timetable(clubId: String, date: String?): TimetableDto =
        json.safeApiCall { service.timetable(clubId, date) }.requiredBody()

    suspend fun book(clubId: String, classId: String): BookingDto =
        json.safeApiCall { service.book(clubId, classId) }.requiredBody()

    suspend fun cancel(clubId: String, classId: String) {
        json.safeApiCall { service.cancel(clubId, classId) }
    }

    fun cancelInFlight() = client.dispatcher.cancelAll()
}
