package com.virginactive.core.network

import com.virginactive.core.network.dto.*
import retrofit2.Response
import retrofit2.http.*

internal interface RetrofitService {
    @POST("auth/login") suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @GET("me") suspend fun member(): Response<MemberDto>

    @GET("home/manifest") suspend fun home(): Response<ManifestDto>

    @GET("clubs/{clubId}/classes/timetable")
    suspend fun timetable(
        @Path("clubId") clubId: String,
        @Query("date") date: String?
    ): Response<TimetableDto>

    @POST("clubs/{clubId}/classes/{classId}/bookings")
    suspend fun book(
        @Path("clubId") clubId: String,
        @Path("classId") classId: String
    ): Response<BookingDto>

    @DELETE("clubs/{clubId}/classes/{classId}/bookings")
    suspend fun cancel(
        @Path("clubId") clubId: String,
        @Path("classId") classId: String
    ): Response<Unit>
}
