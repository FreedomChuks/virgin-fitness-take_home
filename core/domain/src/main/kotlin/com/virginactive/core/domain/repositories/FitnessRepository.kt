package com.virginactive.core.domain.repositories

import com.virginactive.core.domain.Result
import com.virginactive.core.model.*
import java.time.LocalDate

interface FitnessRepository {
    suspend fun member(): Result<Member>

    suspend fun home(): Result<List<HomeBlock>>

    suspend fun timetable(clubId: String, date: LocalDate? = null): Result<Timetable>

    suspend fun book(ref: ClassRef): Result<Booking>

    suspend fun cancel(ref: ClassRef): Result<Unit>
}
