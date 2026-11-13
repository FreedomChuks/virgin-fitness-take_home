package com.virginactive.features.booking.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.virginactive.core.designsystem.*
import com.virginactive.core.model.*
import com.virginactive.core.ui.durationLabel
import com.virginactive.core.ui.timeLabel
import java.time.format.DateTimeFormatter

private val longDate = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy")

@Composable
internal fun ClassDetailsCard(fitnessClass: FitnessClass, modifier: Modifier = Modifier) {
    ActiveCard(modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(ActiveSpace.medium)) {
            ActiveInfoRow(ActiveIcons.Event, fitnessClass.startsAt.format(longDate), label = "Date")
            ActiveInfoRow(
                ActiveIcons.Schedule,
                "${fitnessClass.timeLabel} · ${fitnessClass.durationLabel}",
                label = "Time")
            ActiveInfoRow(ActiveIcons.Location, fitnessClass.timezone, label = "Club time zone")
            ActiveInfoRow(ActiveIcons.Person, availability(fitnessClass), label = "Availability")
        }
    }
}

private fun availability(fitnessClass: FitnessClass): String =
    when (fitnessClass.availability) {
        Availability.AVAILABLE -> "${fitnessClass.available} of ${fitnessClass.capacity} spots left"
        Availability.FULL ->
            when (fitnessClass.waitlistCount) {
                0 -> "Full · no one on the waitlist yet"
                1 -> "Full · 1 person on the waitlist"
                else -> "Full · ${fitnessClass.waitlistCount} people on the waitlist"
            }
        Availability.CANCELLED -> "Cancelled"
        Availability.UNKNOWN -> "Unknown"
    }
