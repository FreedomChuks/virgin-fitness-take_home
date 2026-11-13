package com.virginactive.core.designsystem

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Light", showBackground = true, widthDp = 360)
@Preview(
    name = "Dark", showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Large text", showBackground = true, widthDp = 360, fontScale = 1.5f)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class ActivePreviews

@Preview(name = "Light", showBackground = true, widthDp = 360, heightDp = 800)
@Preview(
    name = "Dark",
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(
    name = "Large text", showBackground = true, widthDp = 360, heightDp = 800, fontScale = 1.5f)
@Preview(name = "Small phone", showBackground = true, widthDp = 320, heightDp = 568)
@Preview(name = "Landscape", showBackground = true, widthDp = 800, heightDp = 360)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class ActiveScreenPreviews
