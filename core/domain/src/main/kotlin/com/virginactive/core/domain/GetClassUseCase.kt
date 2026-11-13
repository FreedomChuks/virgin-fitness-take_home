package com.virginactive.core.domain

import com.virginactive.core.domain.repositories.FitnessRepository
import com.virginactive.core.model.*

class GetClassUseCase(private val repository: FitnessRepository) {
    suspend operator fun invoke(ref: ClassRef): Result<FitnessClass> =
        repository.timetable(ref.clubId).flatMap { table ->
            val fitnessClass = table.days.flatMap { it.classes }.firstOrNull { it.ref == ref }
            if (fitnessClass != null) Result.Success(fitnessClass)
            else
                Result.Failure(
                    error =
                        AppFailure(
                            reason = AppFailure.Reason.INVALID,
                            message = "This class is no longer in the current week's timetable."))
        }
}
