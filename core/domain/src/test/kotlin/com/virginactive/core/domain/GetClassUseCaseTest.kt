package com.virginactive.core.domain

import com.virginactive.core.domain.repositories.FitnessRepository
import com.virginactive.core.model.*
import java.time.LocalDate
import java.time.OffsetDateTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class GetClassUseCaseTest {
    private val yoga =
        FitnessClass(
            ClassRef("club", "yoga::2026-11-13"),
            "Yoga",
            "Trainer",
            "yoga",
            OffsetDateTime.parse("2026-11-13T07:00:00+02:00"),
            OffsetDateTime.parse("2026-11-13T08:00:00+02:00"),
            "Africa/Johannesburg",
            20,
            5,
            Availability.AVAILABLE,
            BookingStatus.NONE,
            0)

    private class Repository(private val timetable: Result<Timetable>) : FitnessRepository {
        var requestedClub: String? = null

        override suspend fun member(): Result<Member> = error("Unused")

        override suspend fun home(): Result<List<HomeBlock>> = error("Unused")

        override suspend fun timetable(clubId: String, date: LocalDate?): Result<Timetable> {
            requestedClub = clubId
            return timetable
        }

        override suspend fun book(ref: ClassRef): Result<Booking> = error("Unused")

        override suspend fun cancel(ref: ClassRef): Result<Unit> = error("Unused")
    }

    private fun week(vararg classes: FitnessClass) =
        Result.Success(
            Timetable(
                "club",
                LocalDate.parse("2026-11-09"),
                LocalDate.parse("2026-11-15"),
                LocalDate.parse("2026-11-13"),
                listOf(TimetableDay(LocalDate.parse("2026-11-13"), classes.toList()))))

    @Test
    fun `finds the class in its club's timetable`() = runTest {
        val repository = Repository(week(yoga))
        assertEquals(Result.Success(yoga), GetClassUseCase(repository)(yoga.ref))
        assertEquals("club", repository.requestedClub)
    }

    @Test
    fun `class missing from the week is an explicit failure`() = runTest {
        val result = GetClassUseCase(Repository(week()))(yoga.ref) as Result.Failure
        assertEquals(AppFailure.Reason.INVALID, result.error.reason)
    }

    @Test
    fun `timetable failure is passed through`() = runTest {
        val failure = Result.Failure(AppFailure(AppFailure.Reason.NETWORK, "Offline"))
        assertEquals(failure, GetClassUseCase(Repository(failure))(yoga.ref))
    }
}
