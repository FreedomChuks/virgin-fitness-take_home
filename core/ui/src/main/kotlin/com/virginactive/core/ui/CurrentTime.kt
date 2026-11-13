package com.virginactive.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay

@Composable
fun rememberCurrentInstant(tick: Duration = 30.seconds): Instant {
    val now by
        produceState(Instant.now(), tick) {
            while (true) {
                delay(tick)
                value = Instant.now()
            }
        }
    return now
}
