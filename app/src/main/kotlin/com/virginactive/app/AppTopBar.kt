package com.virginactive.app

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.virginactive.app.navigation.Destination
import com.virginactive.core.designsystem.ActiveBrandMark
import com.virginactive.core.designsystem.ActiveIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppTopBar(destination: NavKey, onBack: () -> Unit, onSignOut: () -> Unit) {
    TopAppBar(
        title = {
            when (destination) {
                is Destination.Booking -> Text("Class details")
                is Destination.Timetable -> Text("Classes")
                else -> ActiveBrandMark(size = 28.dp)
            }
        },
        navigationIcon = {
            if (destination is Destination.Booking)
                IconButton(onClick = onBack) { Icon(ActiveIcons.Back, contentDescription = "Back") }
        },
        actions = {
            TextButton(
                onClick = onSignOut,
                modifier = Modifier.heightIn(min = 48.dp),
                colors =
                    ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant)) {
                    Text("Sign out")
                }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface),
    )
}
