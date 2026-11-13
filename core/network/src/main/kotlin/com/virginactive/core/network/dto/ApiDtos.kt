package com.virginactive.core.network.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable internal data class LoginRequest(val username: String, val password: String)

@Serializable internal data class RefreshRequest(val refreshToken: String)

@Serializable
internal data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: JsonObject? = null,
)

@Serializable
internal data class ApiErrorDto(val message: String? = null, val requestId: String? = null)

@Serializable data class ClubDto(val id: String)

@Serializable data class MemberDto(val homeClub: ClubDto)

@Serializable
data class ClassDto(
    val classId: String,
    val clubId: String,
    val title: String,
    val trainer: String = "",
    val type: String = "",
    val startsAt: String,
    val endsAt: String,
    val timezone: String,
    val spots: Int,
    val available: Int,
    val status: String,
    val userBookingStatus: String,
    val waitlistCount: Int = 0,
)

@Serializable data class TimetableDayDto(val date: String, val classes: List<ClassDto>)

@Serializable
data class TimetableDto(
    val clubId: String,
    val weekStart: String,
    val weekEnd: String,
    val selectedDate: String,
    val days: List<TimetableDayDto>,
)

@Serializable
data class BookingDto(
    val bookingId: String,
    val status: String,
    val waitlistPosition: Int? = null,
    val classInstance: ClassDto,
)
