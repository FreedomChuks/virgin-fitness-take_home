package com.virginactive.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.virginactive.app.navigation.AppNavigator
import com.virginactive.app.navigation.Destination
import com.virginactive.core.model.ClassRef
import com.virginactive.core.model.FitnessClass
import com.virginactive.core.model.HomeAction
import com.virginactive.features.booking.BookingRoute
import com.virginactive.features.booking.BookingViewModel
import com.virginactive.features.home.HomeRoute
import com.virginactive.features.home.HomeViewModel
import com.virginactive.features.login.LoginRoute
import com.virginactive.features.login.LoginViewModel
import com.virginactive.features.timetable.TimetableRoute
import com.virginactive.features.timetable.TimetableViewModel

@Composable
fun ActiveApp(addToCalendar: (FitnessClass) -> Unit, viewModel: AppViewModel = hiltViewModel()) {
    val signedIn by viewModel.signedIn.collectAsStateWithLifecycle()
    val signOutError by viewModel.signOutError.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(signOutError) { signOutError?.let { snackbarHostState.showSnackbar(it) } }
    key(signedIn) {
        val backStack = rememberNavBackStack(if (signedIn) Destination.Home else Destination.Login)
        val navigator = remember(backStack) { AppNavigator(backStack) }
        val destination = backStack.last()
        val openClass: (ClassRef) -> Unit = { navigator.booking(it.clubId, it.classId) }
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {
                if (signedIn)
                    AppTopBar(destination, navigator::back) {
                        viewModel.onAction(AppAction.SignOut)
                    }
            },
            bottomBar = {
                if (signedIn && destination !is Destination.Booking)
                    AppBottomBar(destination, navigator::home) { navigator.timetable() }
            },
        ) { padding ->
            NavDisplay(
                backStack = backStack,
                onBack = navigator::back,
                modifier = Modifier.fillMaxSize().padding(padding),
                entryDecorators =
                    listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                    ),
                entryProvider =
                    entryProvider {
                        entry<Destination.Login> { LoginRoute(hiltViewModel<LoginViewModel>()) }
                        entry<Destination.Home> {
                            val vm: HomeViewModel = hiltViewModel()
                            HomeRoute(vm) { action ->
                                when (action) {
                                    is HomeAction.OpenClass -> openClass(action.ref)
                                    is HomeAction.OpenTimetable ->
                                        navigator.timetable(action.clubId)
                                }
                            }
                        }
                        entry<Destination.Timetable> { route ->
                            val vm =
                                hiltViewModel<TimetableViewModel, TimetableViewModel.Factory>(
                                    creationCallback = { it.create(route.clubId) },
                                )
                            TimetableRoute(vm, openClass)
                        }
                        entry<Destination.Booking> { route ->
                            val vm =
                                hiltViewModel<BookingViewModel, BookingViewModel.Factory>(
                                    creationCallback = {
                                        it.create(ClassRef(route.clubId, route.classId))
                                    },
                                )
                            BookingRoute(vm, addToCalendar)
                        }
                    },
            )
        }
    }
}
