package com.virginactive.core.data

import com.virginactive.core.model.*
import com.virginactive.core.network.dto.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test

class ApiFixtureTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `runner payloads decode through generated serializers and domain mappers`() {
        verify("avid.runner", "club_sea_point", 7)
    }

    @Test
    fun `swimmer payloads decode through generated serializers and domain mappers`() {
        verify("competitive.swimmer", "club_aldersgate", 6)
    }

    private fun verify(name: String, clubId: String, homeCount: Int) {
        val text = checkNotNull(javaClass.getResource("/$name.json")).readText()
        val payloads = json.parseToJsonElement(text).jsonObject
        val member = json.decodeFromJsonElement<MemberDto>(payloads.getValue("/me")).toMember()
        assertEquals(clubId, member.homeClubId)
        val home =
            json.decodeFromJsonElement<ManifestDto>(payloads.getValue("/home/manifest")).toHome()
        assertEquals(homeCount, home.size)
        assertTrue(home.first() is HomeBlock.Greeting)
        val table =
            json
                .decodeFromJsonElement<TimetableDto>(
                    payloads.getValue("/clubs/$clubId/classes/timetable"))
                .toTimetable()
        assertEquals(7, table.days.size)
        assertTrue(table.days.flatMap { it.classes }.all { it.ref.clubId == clubId })
        assertTrue(
            table.days.all { day ->
                day.classes.zipWithNext().all { (a, b) ->
                    a.startsAt.toInstant() <= b.startsAt.toInstant()
                }
            })
    }
}
