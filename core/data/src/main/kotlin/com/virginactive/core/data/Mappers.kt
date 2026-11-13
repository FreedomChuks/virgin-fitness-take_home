package com.virginactive.core.data

import com.virginactive.core.model.*
import com.virginactive.core.network.dto.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

internal fun bookingStatus(value: String?) =
    when (value) {
        "none" -> BookingStatus.NONE
        "booked" -> BookingStatus.BOOKED
        "waitlisted" -> BookingStatus.WAITLISTED
        else -> BookingStatus.UNKNOWN
    }

internal fun ClassDto.toClass() =
    FitnessClass(
        ClassRef(clubId, classId),
        title,
        trainer,
        type,
        OffsetDateTime.parse(startsAt),
        OffsetDateTime.parse(endsAt),
        timezone,
        spots,
        available,
        when (status) {
            "available" -> Availability.AVAILABLE
            "full" -> Availability.FULL
            "cancelled" -> Availability.CANCELLED
            else -> Availability.UNKNOWN
        },
        bookingStatus(userBookingStatus),
        waitlistCount,
    )

internal fun TimetableDto.toTimetable() =
    Timetable(
        clubId,
        LocalDate.parse(weekStart),
        LocalDate.parse(weekEnd),
        LocalDate.parse(selectedDate),
        days.mapNotNull { it.toDayOrNull() }.distinctBy { it.date })

private fun TimetableDayDto.toDayOrNull(): TimetableDay? {
    val date =
        try {
            LocalDate.parse(date)
        } catch (_: DateTimeParseException) {
            return null
        }
    return TimetableDay(
        date,
        classes
            .mapNotNull { it.toClassOrNull() }
            .distinctBy { it.ref.classId }
            .sortedBy { it.startsAt.toInstant() })
}

private fun ClassDto.toClassOrNull(): FitnessClass? =
    try {
        toClass()
    } catch (_: DateTimeParseException) {
        null
    }

private fun tileKind(type: String) =
    when (type) {
        "classCarousel" -> TileKind.CLASSES
        "myRewards" -> TileKind.REWARDS
        "myGoals" -> TileKind.GOALS
        else -> null
    }

internal fun MemberDto.toMember() = Member(homeClub.id)

internal fun BookingDto.toBooking(): Booking {
    val bookingStatus = bookingStatus(status)
    require(bookingStatus == BookingStatus.BOOKED || bookingStatus == BookingStatus.WAITLISTED) {
        "Unrecognised booking outcome"
    }
    return Booking(bookingId, bookingStatus, waitlistPosition, classInstance.toClass())
}

internal fun HomeActionDto.toAction(): HomeAction? =
    when (this) {
        is HomeActionDto.OpenClass -> HomeAction.OpenClass(ClassRef(clubId, classId))
        is HomeActionDto.OpenTimetable -> HomeAction.OpenTimetable(clubId)
        HomeActionDto.Unknown -> null
    }

internal fun ManifestDto.toHome(): List<HomeBlock> =
    blocks.mapNotNull { block ->
        when (block) {
            is HomeBlockDto.Greeting -> HomeBlock.Greeting(block.title)
            is HomeBlockDto.Hero -> HomeBlock.Hero(block.title, block.subtitle)
            is HomeBlockDto.MyClub ->
                HomeBlock.MyClub(
                    block.name, block.addressLine, block.openingHoursToday, block.phoneNumber)
            is HomeBlockDto.Promotion -> HomeBlock.Promotion(block.title, block.subtitle)
            is HomeBlockDto.Tiles ->
                HomeBlock.Tiles(
                    tileKind(block.type) ?: return@mapNotNull null,
                    block.title,
                    block.items.mapNotNull { it.toTile() },
                    block.viewAllAction?.toAction())
            HomeBlockDto.Unknown -> null
        }
    }

private fun HomeTileDto.toTile(): HomeTile? {
    val title = title ?: return null
    return HomeTile(
        id ?: title,
        title,
        subtitle,
        badge,
        startsAt?.let { runCatching { OffsetDateTime.parse(it) }.getOrNull() },
        actionLabel,
        actionRef?.toAction())
}
