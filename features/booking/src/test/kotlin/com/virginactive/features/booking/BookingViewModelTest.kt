package com.virginactive.features.booking

import com.virginactive.core.domain.*
import com.virginactive.core.domain.Result
import com.virginactive.core.domain.repositories.FitnessRepository
import com.virginactive.core.model.*
import com.virginactive.core.ui.*
import java.time.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class BookingViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun before() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun after() {
        Dispatchers.resetMain()
    }

    private class Repository : FitnessRepository {
        var fitnessClass =
            FitnessClass(
                ClassRef("club", "class"),
                "Yoga",
                "Trainer",
                "yoga",
                OffsetDateTime.now().plusDays(2),
                OffsetDateTime.now().plusDays(2).plusHours(1),
                "Europe/London",
                20,
                1,
                Availability.AVAILABLE,
                BookingStatus.NONE,
                0)
        var bookingCalls = 0
        var ambiguousFailure = false
        var readFailure = false

        override suspend fun member(): Result<Member> = error("Unused")

        override suspend fun home(): Result<List<HomeBlock>> = Result.Success(emptyList())

        override suspend fun timetable(clubId: String, date: LocalDate?): Result<Timetable> {
            if (readFailure) return Result.Failure(AppFailure(AppFailure.Reason.NETWORK, "Offline"))
            return Result.Success(
                Timetable(
                    "club",
                    LocalDate.parse("2026-11-09"),
                    LocalDate.parse("2026-11-15"),
                    LocalDate.parse("2026-11-13"),
                    listOf(TimetableDay(LocalDate.parse("2026-11-13"), listOf(fitnessClass)))))
        }

        override suspend fun book(ref: ClassRef): Result<Booking> {
            bookingCalls++
            fitnessClass = fitnessClass.copy(bookingStatus = BookingStatus.BOOKED)
            if (ambiguousFailure)
                return Result.Failure(AppFailure(AppFailure.Reason.NETWORK, "Connection lost"))
            return Result.Success(Booking("receipt", BookingStatus.BOOKED, null, fitnessClass))
        }

        override suspend fun cancel(ref: ClassRef): Result<Unit> {
            fitnessClass = fitnessClass.copy(bookingStatus = BookingStatus.NONE)
            readFailure = true
            return Result.Success(Unit)
        }
    }

    @Test
    fun `uncertain booking requires reconciliation before another mutation`() =
        runTest(dispatcher) {
            val repository = Repository().apply { ambiguousFailure = true }
            val vm =
                BookingViewModel(
                    repository,
                    GetClassUseCase(repository),
                    BookClassUseCase(repository),
                    ClassRef("club", "class"))
            runCurrent()
            vm.onAction(BookingAction.RequestBooking)
            vm.onAction(BookingAction.ConfirmBooking)
            runCurrent()
            assertTrue(vm.state.value.needsRefresh)
            vm.onAction(BookingAction.RequestBooking)
            vm.onAction(BookingAction.ConfirmBooking)
            runCurrent()
            assertEquals(1, repository.bookingCalls)
            vm.onAction(BookingAction.Refresh)
            runCurrent()
            assertFalse(vm.state.value.needsRefresh)
            assertEquals(
                BookingStatus.BOOKED,
                (vm.state.value.content as ScreenState.Ready).value.bookingStatus)
        }

    @Test
    fun `double taps submit only one booking`() =
        runTest(dispatcher) {
            val repository = Repository()
            val vm =
                BookingViewModel(
                    repository,
                    GetClassUseCase(repository),
                    BookClassUseCase(repository),
                    ClassRef("club", "class"))
            runCurrent()
            vm.onAction(BookingAction.RequestBooking)
            vm.onAction(BookingAction.ConfirmBooking)
            vm.onAction(BookingAction.RequestBooking)
            vm.onAction(BookingAction.ConfirmBooking)
            runCurrent()
            assertEquals(1, repository.bookingCalls)
            assertEquals("receipt", vm.state.value.receipt?.id)
        }

    @Test
    fun `successful cancellation followed by failed read never leaves infinite loading or old receipt`() =
        runTest(dispatcher) {
            val repository = Repository()
            val vm =
                BookingViewModel(
                    repository,
                    GetClassUseCase(repository),
                    BookClassUseCase(repository),
                    ClassRef("club", "class"))
            runCurrent()
            vm.onAction(BookingAction.RequestBooking)
            vm.onAction(BookingAction.ConfirmBooking)
            runCurrent()
            vm.onAction(BookingAction.RequestCancellation)
            vm.onAction(BookingAction.ConfirmCancellation)
            runCurrent()
            assertFalse(vm.state.value.busy)
            assertTrue(vm.state.value.needsRefresh)
            assertNull(vm.state.value.receipt)
            assertEquals(
                BookingStatus.NONE,
                (vm.state.value.content as ScreenState.Ready).value.bookingStatus)
        }

    @Test
    fun `booking requires explicit confirmation and dismissing does not mutate the server`() =
        runTest(dispatcher) {
            val repository = Repository()
            val vm =
                BookingViewModel(
                    repository,
                    GetClassUseCase(repository),
                    BookClassUseCase(repository),
                    ClassRef("club", "class"))
            runCurrent()
            vm.onAction(BookingAction.ConfirmBooking)
            runCurrent()
            assertEquals(0, repository.bookingCalls)
            vm.onAction(BookingAction.RequestBooking)
            assertEquals(BookingConfirmation.BOOK, vm.state.value.confirmation)
            vm.onAction(BookingAction.DismissConfirmation)
            vm.onAction(BookingAction.ConfirmBooking)
            runCurrent()
            assertNull(vm.state.value.confirmation)
            assertEquals(0, repository.bookingCalls)
        }
}
