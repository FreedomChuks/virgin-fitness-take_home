package com.virginactive.core.ui

import com.virginactive.core.domain.AppFailure
import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenStateTest {
    private val offline = AppFailure(AppFailure.Reason.NETWORK, "Offline", requestId = "req-1")

    @Test
    fun `first load shows loading then a full error`() {
        val loading = ScreenState.Loading.refreshing<String>()
        assertEquals(ScreenState.Loading, loading)
        assertEquals(ScreenState.Failed("Offline\nReference: req-1"), loading.failed(offline))
    }

    @Test
    fun `refresh keeps content and failure shows a notice over it`() {
        val refreshing = ScreenState.Ready("home", refreshError = "old").refreshing()
        assertEquals(ScreenState.Ready("home", refreshing = true), refreshing)
        assertEquals(
            ScreenState.Ready("home", refreshError = "Offline\nReference: req-1"),
            refreshing.failed(offline))
    }

    @Test
    fun `a previous failure retries as a fresh load`() {
        assertEquals(ScreenState.Loading, ScreenState.Failed("Offline").refreshing<String>())
    }
}
