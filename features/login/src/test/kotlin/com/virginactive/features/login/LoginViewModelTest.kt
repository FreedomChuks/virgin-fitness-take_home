package com.virginactive.features.login

import com.virginactive.core.domain.AppFailure
import com.virginactive.core.domain.Result
import com.virginactive.core.domain.repositories.AuthRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    private class Auth : AuthRepository {
        override val signedIn = MutableStateFlow(false)
        val gate = CompletableDeferred<Unit>()
        var calls = 0
        var failure: AppFailure? = null

        override suspend fun login(username: String, password: String): Result<Unit> {
            calls++
            gate.await()
            failure?.let {
                return Result.Failure(it)
            }
            signedIn.value = true
            return Result.Success(Unit)
        }

        override suspend fun logout(): Result<Unit> {
            signedIn.value = false
            return Result.Success(Unit)
        }
    }

    @Test
    fun `blank submit changes state without calling authentication`() =
        runTest(dispatcher) {
            val auth = Auth()
            val vm = LoginViewModel(auth)
            vm.onAction(LoginAction.Submit)
            runCurrent()
            assertEquals(0, auth.calls)
            assertNotNull(vm.state.value.error)
            assertFalse(vm.state.value.busy)
        }

    @Test
    fun `busy state prevents duplicate submits and credential edits then clears password on success`() =
        runTest(dispatcher) {
            val auth = Auth()
            val vm = LoginViewModel(auth)
            vm.onAction(LoginAction.UsernameChanged("member"))
            vm.onAction(LoginAction.PasswordChanged("secret"))
            vm.onAction(LoginAction.Submit)
            vm.onAction(LoginAction.Submit)
            vm.onAction(LoginAction.UsernameChanged("another"))
            runCurrent()
            assertEquals(1, auth.calls)
            assertEquals("member", vm.state.value.username)
            assertTrue(vm.state.value.busy)
            auth.gate.complete(Unit)
            runCurrent()
            assertEquals("", vm.state.value.password)
            assertFalse(vm.state.value.busy)
        }

    @Test
    fun `editing credentials clears the prior validation error`() =
        runTest(dispatcher) {
            val vm = LoginViewModel(Auth())
            vm.onAction(LoginAction.Submit)
            vm.onAction(LoginAction.UsernameChanged("member"))
            assertNull(vm.state.value.error)
            vm.onAction(LoginAction.TogglePasswordVisibility)
            assertTrue(vm.state.value.passwordVisible)
        }

    @Test
    fun `typed login failure displays error and allows retry`() =
        runTest(dispatcher) {
            val auth =
                Auth().apply {
                    failure = AppFailure(AppFailure.Reason.UNAUTHORIZED, "Invalid credentials")
                    gate.complete(Unit)
                }
            val vm = LoginViewModel(auth)
            vm.onAction(LoginAction.UsernameChanged("member"))
            vm.onAction(LoginAction.PasswordChanged("password"))
            vm.onAction(LoginAction.Submit)
            runCurrent()
            assertFalse(vm.state.value.busy)
            assertEquals("Invalid credentials", vm.state.value.error)
            assertFalse(auth.signedIn.value)
            auth.failure = null
            vm.onAction(LoginAction.Submit)
            runCurrent()
            assertTrue(auth.signedIn.value)
            assertEquals("", vm.state.value.password)
        }
}
