package com.virginactive.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics

@Composable
fun ActiveHero(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
) {
    val glyph = remember { PathParser().parsePathString(BrandGlyphPath).toPath() }
    Column(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(
                Brush.linearGradient(
                    listOf(ActiveBrandColors.Red, ActiveBrandColors.RedDeep),
                    start = Offset.Zero,
                    end = Offset.Infinite))
            .drawBehind {
                val glyphScale = size.height * 1.6f / 108f
                withTransform({
                    translate(left = size.width - 70f * glyphScale, top = -size.height * 0.25f)
                    scale(glyphScale, glyphScale, pivot = Offset.Zero)
                }) {
                    drawPath(glyph, Color.White.copy(alpha = 0.09f))
                }
            }
            .padding(horizontal = ActiveSpace.large, vertical = ActiveSpace.large),
        verticalArrangement = Arrangement.spacedBy(ActiveSpace.small)) {
            val onBrand = ActiveBrandColors.OnRed
            if (eyebrow != null) {
                Text(
                    eyebrow.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = onBrand.copy(alpha = 0.85f))
            }
            Text(
                title,
                Modifier.semantics { heading() },
                style = MaterialTheme.typography.headlineSmall,
                color = onBrand)
            subtitle?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = onBrand.copy(alpha = 0.92f))
            }
        }
}

@ActivePreviews
@Composable
private fun ActiveHeroPreview() {
    ActiveTheme {
        Surface {
            ActiveHero(
                "Crush your goals this week",
                "You've attended 3 classes — 1 more to hit your weekly target.",
                Modifier.padding(ActiveSpace.medium),
                eyebrow = "This week")
        }
    }
}
