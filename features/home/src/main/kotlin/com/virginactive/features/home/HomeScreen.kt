package com.virginactive.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.virginactive.core.designsystem.*
import com.virginactive.core.model.*
import com.virginactive.core.ui.*
import com.virginactive.features.home.components.*

@Composable
fun HomeScreen(state: ScreenState<List<HomeBlock>>, onAction: (HomeUiAction) -> Unit) {
    RefreshableStateContent(state, { onAction(HomeUiAction.Refresh) }) { blocks ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(top = ActiveSpace.small, bottom = ActiveSpace.extraLarge),
            verticalArrangement = Arrangement.spacedBy(ActiveSpace.large)) {
                if (blocks.isEmpty()) {
                    item {
                        ActiveEmptyState(
                            "Your home is taking shape",
                            "Explore this week's classes from the Classes tab.",
                            Modifier.fillMaxWidth())
                    }
                }
                itemsIndexed(blocks, contentType = { _, block -> block::class }) { _, block ->
                    HomeBlockContent(block, onAction)
                }
            }
    }
}

@Composable
private fun HomeBlockContent(block: HomeBlock, onAction: (HomeUiAction) -> Unit) {
    val gutter = Modifier.padding(horizontal = ActiveSpace.screen)
    when (block) {
        is HomeBlock.Greeting ->
            Text(
                block.title,
                gutter.semantics { heading() },
                style = MaterialTheme.typography.headlineMedium)
        is HomeBlock.Hero -> ActiveHero(block.title, block.subtitle, gutter)
        is HomeBlock.MyClub -> HomeClubCard(block, gutter)
        is HomeBlock.Promotion -> HomePromotionCard(block, gutter)
        is HomeBlock.Tiles ->
            HomeTileRow(
                block,
                onOpen = { onAction(HomeUiAction.Navigate(it)) },
                contentPadding = PaddingValues(horizontal = ActiveSpace.screen))
    }
}
