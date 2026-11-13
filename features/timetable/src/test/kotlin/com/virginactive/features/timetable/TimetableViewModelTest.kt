package com.virginactive.features.timetable

import com.virginactive.core.domain.AppFailure
import com.virginactive.core.domain.Result
import com.virginactive.core.domain.repositories.FitnessRepository
import com.virginactive.core.model.*
import com.virginactive.core.ui.ScreenState
import java.time.LocalDate
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class TimetableViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    private class Repository : FitnessRepository {
        var table =
            Timetable(
                "club",
                LocalDate.parse("2026-11-09"),
                LocalDate.parse("2026-11-15"),
                LocalDate.parse("2026-11-13"),
                listOf(TimetableDay(LocalDate.parse("2026-11-13"), emptyList())))

        override suspend fun member() = Result.Success(Member("club"))

        var response: CompletableDeferred<Result<Timetable>>? = null

        override suspend fun timetable(clubId: String, date: LocalDate?) =
            response?.await() ?: Result.Success(table)

        override suspend fun home(): Result<List<HomeBlock>> = Result.Success(emptyList())

        override suspend fun book(ref: ClassRef): Result<Booking> = error("Unused")

        override suspend fun cancel(ref: ClassRef) = Result.Success(Unit)
    }

    @Test
    fun `refresh preserves selected day and ignores invalid selections`() =
        runTest(dispatcher) {
            val vm = TimetableViewModel(Repository(), "club")
            vm.onAction(TimetableAction.Refresh)
            runCurrent()
            vm.onAction(TimetableAction.SelectDay(null))
            vm.onAction(TimetableAction.Refresh)
            runCurrent()
            assertNull(vm.state.value.selectedDate)
            vm.onAction(TimetableAction.SelectDay(LocalDate.parse("2026-12-25")))
            assertNull(vm.state.value.selectedDate)
        }

    @Test
    fun `new week resets selection to server venue date`() =
        runTest(dispatcher) {
            val repository = Repository()
            val vm = TimetableViewModel(repository, "club")
            vm.onAction(TimetableAction.Refresh)
            runCurrent()
            vm.onAction(TimetableAction.SelectDay(null))
            repository.table =
                Timetable(
                    "club",
                    LocalDate.parse("2026-11-16"),
                    LocalDate.parse("2026-11-22"),
                    LocalDate.parse("2026-11-16"),
                    listOf(TimetableDay(LocalDate.parse("2026-11-16"), emptyList())))
            vm.onAction(TimetableAction.Refresh)
            runCurrent()
            assertEquals(LocalDate.parse("2026-11-16"), vm.state.value.selectedDate)
        }

    @Test
    fun `pull refresh retains content and selection on failure then recovers`() =
        runTest(dispatcher) {
            val repository = Repository()
            val vm = TimetableViewModel(repository, "club")
            vm.onAction(TimetableAction.Refresh)
            runCurrent()
            vm.onAction(TimetableAction.SelectDay(null))
            val response = CompletableDeferred<Result<Timetable>>()
            repository.response = response
            vm.onAction(TimetableAction.Refresh)
            runCurrent()
            val refreshing = vm.state.value.content as ScreenState.Ready
            assertTrue(refreshing.refreshing)
            assertEquals(repository.table, refreshing.value)
            response.complete(Result.Failure(AppFailure(AppFailure.Reason.NETWORK, "Offline")))
            runCurrent()
            val failedRefresh = vm.state.value.content as ScreenState.Ready
            assertFalse(failedRefresh.refreshing)
            assertNotNull(failedRefresh.refreshError)
            assertEquals(repository.table, failedRefresh.value)
            assertNull(vm.state.value.selectedDate)
            repository.response = null
            vm.onAction(TimetableAction.Refresh)
            runCurrent()
            assertNull((vm.state.value.content as ScreenState.Ready).refreshError)
        }
}
