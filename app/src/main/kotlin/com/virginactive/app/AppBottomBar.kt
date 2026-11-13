package com.virginactive.app

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.virginactive.app.navigation.Destination
import com.virginactive.core.designsystem.ActiveIcons

@Composable
internal fun AppBottomBar(destination: NavKey, onHome: () -> Unit, onClasses: () -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
        val itemColors =
            NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onSurface,
            )
        val home = destination == Destination.Home
        val classes = destination is Destination.Timetable
        NavigationBarItem(
            selected = home,
            onClick = onHome,
            icon = {
                Icon(
                    if (home) ActiveIcons.HomeSelected else ActiveIcons.Home,
                    contentDescription = null)
            },
            label = { Text("Home") },
            colors = itemColors,
        )
        NavigationBarItem(
            selected = classes,
            onClick = onClasses,
            icon = {
                Icon(
                    if (classes) ActiveIcons.TimetableSelected else ActiveIcons.Timetable,
                    contentDescription = null)
            },
            label = { Text("Classes") },
            colors = itemColors,
        )
    }
}
