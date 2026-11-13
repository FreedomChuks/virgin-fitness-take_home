package com.virginactive.core.ui

import com.virginactive.core.model.FitnessClass
import java.time.Duration
import java.time.format.DateTimeFormatter

private val clock = DateTimeFormatter.ofPattern("HH:mm")
private val shortDate = DateTimeFormatter.ofPattern("EEE, d MMM")
private val acronyms = setOf("hiit", "rpm")

val FitnessClass.startLabel: String
    get() = startsAt.format(clock)

val FitnessClass.endLabel: String
    get() = endsAt.format(clock)

val FitnessClass.dateLabel: String
    get() = startsAt.format(shortDate)

val FitnessClass.timeLabel: String
    get() = "$startLabel – $endLabel"

val FitnessClass.durationLabel: String
    get() {
        val minutes = Duration.between(startsAt, endsAt).toMinutes()
        return if (minutes >= 60 && minutes % 60 == 0L) "${minutes / 60} h"
        else if (minutes > 60) "${minutes / 60} h ${minutes % 60} min" else "$minutes min"
    }

val FitnessClass.categoryLabel: String
    get() {
        if (category.lowercase() in acronyms) return category.uppercase()
        val words = category.split(Regex("(?=[A-Z])|[_\\-\\s]+")).filter { it.isNotBlank() }
        return words
            .joinToString(" ") { it.lowercase() }
            .replaceFirstChar { it.titlecase() }
            .ifBlank { "Class" }
    }
