package com.virginactive.features.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.virginactive.core.designsystem.*
import com.virginactive.core.model.HomeBlock
import com.virginactive.core.ui.SectionTitle

@Composable
internal fun HomeClubCard(block: HomeBlock.MyClub, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(ActiveSpace.xs)) {
        SectionTitle("Your home club")
        ActiveCard(Modifier.fillMaxWidth()) {
            Text(block.name, style = MaterialTheme.typography.titleMedium)
            Column(
                Modifier.padding(top = ActiveSpace.xs),
                verticalArrangement = Arrangement.spacedBy(ActiveSpace.small)) {
                    block.address?.let { ActiveInfoRow(ActiveIcons.Location, it) }
                    block.hours?.let { ActiveInfoRow(ActiveIcons.Schedule, "Open today · $it") }
                    block.phone?.let { ActiveInfoRow(ActiveIcons.Phone, it) }
                }
        }
    }
}
