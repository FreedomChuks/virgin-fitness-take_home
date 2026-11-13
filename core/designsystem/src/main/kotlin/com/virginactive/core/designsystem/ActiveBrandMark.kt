package com.virginactive.core.designsystem

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal const val BrandGlyphPath = "M25,29h13l16,35 16,-35h13L60,80H48z"

private val BrandMarkVector: ImageVector by lazy {
    ImageVector.Builder("ActiveBrandMark", 108.dp, 108.dp, 108f, 108f)
        .addPath(addPathNodes("M0,0h108v108h-108z"), fill = SolidColor(ActiveBrandColors.Red))
        .addPath(addPathNodes(BrandGlyphPath), fill = SolidColor(ActiveBrandColors.OnRed))
        .build()
}

@Composable
fun ActiveBrandMark(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    showWordmark: Boolean = true,
    wordmarkColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier.clearAndSetSemantics {
            contentDescription = "Virgin Active"
            heading()
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ActiveSpace.small)) {
            Image(
                BrandMarkVector,
                contentDescription = null,
                modifier = Modifier.size(size).clip(MaterialTheme.shapes.small))
            if (showWordmark) {
                Text(
                    "Virgin Active",
                    style = MaterialTheme.typography.titleLarge,
                    color = wordmarkColor)
            }
        }
}

@ActivePreviews
@Composable
private fun ActiveBrandMarkPreview() {
    ActiveTheme {
        Surface {
            Column(
                Modifier.padding(ActiveSpace.medium),
                verticalArrangement = Arrangement.spacedBy(ActiveSpace.medium)) {
                    ActiveBrandMark()
                    ActiveBrandMark(size = 48.dp, showWordmark = false)
                }
        }
    }
}
