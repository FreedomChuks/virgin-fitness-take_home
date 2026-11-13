package com.virginactive.core.domain

import com.virginactive.core.domain.repositories.FitnessRepository
import com.virginactive.core.model.*
import java.time.Clock

class BookClassUseCase(
    private val repository: FitnessRepository,
    private val clock: Clock = Clock.systemUTC()
) {
    suspend operator fun invoke(fitnessClass: FitnessClass): Result<Booking> {
        val invalid =
            when {
                fitnessClass.hasStarted(clock.instant()) -> "This class has already started."
                fitnessClass.bookingStatus != BookingStatus.NONE ->
                    "You already have a booking for this class."
                fitnessClass.availability !in setOf(Availability.AVAILABLE, Availability.FULL) ->
                    "This class cannot be booked."
                else -> null
            }
        if (invalid != null) return Result.Failure(AppFailure(AppFailure.Reason.INVALID, invalid))
        return repository.book(fitnessClass.ref)
    }
}
