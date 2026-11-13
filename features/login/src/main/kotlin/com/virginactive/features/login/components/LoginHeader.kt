package com.virginactive.features.login.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.virginactive.core.designsystem.*

@Composable
internal fun LoginHeader(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().padding(bottom = ActiveSpace.small),
        verticalArrangement = Arrangement.spacedBy(ActiveSpace.large)) {
            ActiveBrandMark()
            ActiveHero(
                title = "Welcome back",
                subtitle = "Sign in to book classes, check your club and keep your week on track.")
        }
}
