package com.virginactive.core.domain

import com.virginactive.core.domain.repositories.FitnessRepository
import com.virginactive.core.model.*
import java.time.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class BookClassUseCaseTest {
    private val now = Instant.parse("2026-11-13T00:00:00Z")
    private val fixture =
        FitnessClass(
            ClassRef("club", "class"),
            "Yoga",
            "Trainer",
            "yoga",
            OffsetDateTime.parse("2026-11-13T07:00:00+02:00"),
            OffsetDateTime.parse("2026-11-13T08:00:00+02:00"),
            "Africa/Johannesburg",
            20,
            0,
            Availability.FULL,
            BookingStatus.NONE,
            0)

    private class Repository : FitnessRepository {
        var bookings = 0

        override suspend fun member(): Result<Member> = error("Unused")

        override suspend fun home(): Result<List<HomeBlock>> = error("Unused")

        override suspend fun timetable(clubId: String, date: LocalDate?): Result<Timetable> =
            error("Unused")

        override suspend fun book(ref: ClassRef): Result<Booking> {
            bookings++
            return Result.Failure(AppFailure(AppFailure.Reason.SERVER, "Reached repository"))
        }

        override suspend fun cancel(ref: ClassRef) = Result.Success(Unit)
    }

    @Test
    fun `past cancelled unknown and already booked classes never reach the API`() = runTest {
        val repository = Repository()
        val book = BookClassUseCase(repository, Clock.fixed(now, ZoneOffset.UTC))
        listOf(
                fixture.copy(startsAt = OffsetDateTime.ofInstant(now, ZoneOffset.UTC)),
                fixture.copy(availability = Availability.CANCELLED),
                fixture.copy(availability = Availability.UNKNOWN),
                fixture.copy(bookingStatus = BookingStatus.BOOKED),
                fixture.copy(bookingStatus = BookingStatus.UNKNOWN))
            .forEach {
                val result = book(it)
                assertTrue(result is Result.Failure)
                assertEquals(AppFailure.Reason.INVALID, (result as Result.Failure).error.reason)
            }
        assertEquals(0, repository.bookings)
    }

    @Test
    fun `full class is allowed to reach waitlist endpoint`() = runTest {
        val repository = Repository()
        val result = BookClassUseCase(repository, Clock.fixed(now, ZoneOffset.UTC))(fixture)
        assertEquals("Reached repository", (result as Result.Failure).error.message)
        assertEquals(1, repository.bookings)
    }
}
