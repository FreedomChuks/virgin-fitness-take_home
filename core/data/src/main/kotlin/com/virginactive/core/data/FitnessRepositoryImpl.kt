package com.virginactive.core.data

import com.virginactive.core.domain.Result
import com.virginactive.core.domain.repositories.FitnessRepository
import com.virginactive.core.model.ClassRef
import com.virginactive.core.network.NetworkDataSource
import java.time.LocalDate
import javax.inject.Inject

class FitnessRepositoryImpl
@Inject
constructor(
    private val network: NetworkDataSource,
    private val members: MemberCache,
) : FitnessRepository {
    override suspend fun member() =
        members.member?.let { Result.Success(it) }
            ?: mapped(retry = true) { network.member().toMember().also { members.member = it } }

    override suspend fun home() = mapped(retry = true) { network.home().toHome() }

    override suspend fun timetable(clubId: String, date: LocalDate?) =
        mapped(retry = true) { network.timetable(clubId, date?.toString()).toTimetable() }

    override suspend fun book(ref: ClassRef) = mapped {
        network.book(ref.clubId, ref.classId).toBooking()
    }

    override suspend fun cancel(ref: ClassRef) = mapped { network.cancel(ref.clubId, ref.classId) }
}
