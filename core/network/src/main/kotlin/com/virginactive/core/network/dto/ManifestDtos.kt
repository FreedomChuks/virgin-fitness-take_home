package com.virginactive.core.network.dto

import kotlinx.serialization.Serializable

@Serializable data class ManifestDto(val blocks: List<HomeBlockDto>)

@Serializable(with = HomeBlockSerializer::class)
sealed interface HomeBlockDto {
    @Serializable data class Greeting(val title: String) : HomeBlockDto

    @Serializable data class Hero(val title: String, val subtitle: String? = null) : HomeBlockDto

    @Serializable
    data class MyClub(
        val name: String,
        val addressLine: String? = null,
        val openingHoursToday: String? = null,
        val phoneNumber: String? = null,
    ) : HomeBlockDto

    @Serializable
    data class Tiles(
        val type: String,
        val title: String,
        val items: List<HomeTileDto> = emptyList(),
        val viewAllAction: HomeActionDto? = null,
    ) : HomeBlockDto

    @Serializable
    data class Promotion(val title: String, val subtitle: String? = null) : HomeBlockDto

    data object Unknown : HomeBlockDto
}

@Serializable
data class HomeTileDto(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val badge: String? = null,
    val startsAt: String? = null,
    val actionLabel: String? = null,
    val actionRef: HomeActionDto? = null,
)

@Serializable(with = HomeActionSerializer::class)
sealed interface HomeActionDto {
    @Serializable data class OpenClass(val clubId: String, val classId: String) : HomeActionDto

    @Serializable data class OpenTimetable(val clubId: String) : HomeActionDto

    data object Unknown : HomeActionDto
}
