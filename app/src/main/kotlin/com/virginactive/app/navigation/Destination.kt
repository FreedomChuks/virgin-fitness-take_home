package com.virginactive.app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {
    @Serializable @SerialName("login") data object Login : Destination

    @Serializable @SerialName("home") data object Home : Destination

    @Serializable
    @SerialName("timetable")
    data class Timetable(val clubId: String? = null) : Destination

    @Serializable
    @SerialName("booking")
    data class Booking(val clubId: String, val classId: String) : Destination
}
