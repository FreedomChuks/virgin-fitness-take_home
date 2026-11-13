package com.virginactive.core.model

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime

data class Member(val homeClubId: String)

data class ClassRef(val clubId: String, val classId: String)

enum class Availability {
    AVAILABLE,
    FULL,
    CANCELLED,
    UNKNOWN
}

enum class BookingStatus {
    NONE,
    BOOKED,
    WAITLISTED,
    UNKNOWN
}

data class FitnessClass(
    val ref: ClassRef,
    val title: String,
    val trainer: String,
    val category: String,
    val startsAt: OffsetDateTime,
    val endsAt: OffsetDateTime,
    val timezone: String,
    val capacity: Int,
    val available: Int,
    val availability: Availability,
    val bookingStatus: BookingStatus,
    val waitlistCount: Int,
) {
    fun hasStarted(now: Instant) = !startsAt.toInstant().isAfter(now)

    fun cancellationMayForfeitCost(now: Instant): Boolean {
        val remaining = Duration.between(now, startsAt.toInstant())
        return !remaining.isNegative && remaining < Duration.ofHours(12)
    }
}

data class TimetableDay(val date: LocalDate, val classes: List<FitnessClass>)

data class Timetable(
    val clubId: String,
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val selectedDate: LocalDate,
    val days: List<TimetableDay>
)

data class Booking(
    val id: String,
    val status: BookingStatus,
    val waitlistPosition: Int?,
    val fitnessClass: FitnessClass
)

sealed interface HomeAction {
    data class OpenClass(val ref: ClassRef) : HomeAction

    data class OpenTimetable(val clubId: String) : HomeAction
}

data class HomeTile(
    val id: String,
    val title: String,
    val subtitle: String?,
    val badge: String?,
    val startsAt: OffsetDateTime?,
    val actionLabel: String?,
    val action: HomeAction?
)

enum class TileKind {
    CLASSES,
    REWARDS,
    GOALS
}

sealed interface HomeBlock {
    data class Greeting(val title: String) : HomeBlock

    data class Hero(val title: String, val subtitle: String?) : HomeBlock

    data class MyClub(
        val name: String,
        val address: String?,
        val hours: String?,
        val phone: String?
    ) : HomeBlock

    data class Tiles(
        val kind: TileKind,
        val title: String,
        val items: List<HomeTile>,
        val action: HomeAction?,
    ) : HomeBlock

    data class Promotion(val title: String, val subtitle: String?) : HomeBlock
}
