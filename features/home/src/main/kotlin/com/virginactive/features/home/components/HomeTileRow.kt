package com.virginactive.features.home.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.virginactive.core.designsystem.*
import com.virginactive.core.model.HomeAction
import com.virginactive.core.model.HomeBlock
import com.virginactive.core.ui.SectionTitle

@Composable
internal fun HomeTileRow(
    block: HomeBlock.Tiles,
    onOpen: (HomeAction) -> Unit,
    contentPadding: PaddingValues,
) {
    if (block.items.isEmpty() && block.action == null) return
    Column(verticalArrangement = Arrangement.spacedBy(ActiveSpace.xs)) {
        SectionTitle(
            block.title,
            Modifier.padding(contentPadding),
            actionLabel = block.action?.let { "View all" },
            onAction = { block.action?.let(onOpen) })
        Row(
            Modifier.horizontalScroll(rememberScrollState())
                .padding(contentPadding)
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(ActiveSpace.smallMedium)) {
                block.items.forEach { tile ->
                    HomeTileCard(tile, onOpen = tile.action?.let { action -> { onOpen(action) } })
                }
            }
    }
}
