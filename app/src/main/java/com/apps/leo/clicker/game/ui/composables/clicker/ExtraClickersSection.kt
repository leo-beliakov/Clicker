package com.apps.leo.clicker.game.ui.composables.clicker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import com.apps.leo.clicker.game.ui.model.ExtraClickerInfo

@Composable
fun ExtraClickersSection(
    extraClickers: List<ExtraClickerInfo>,
    onExtraClickerClicked: (ExtraClickerInfo) -> Unit,
    onExtraClickerDisappeared: (ExtraClickerInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val localDensity = LocalDensity.current

    Box(modifier = modifier) {
        extraClickers.forEach { clickerInfo ->
            val scaleAndAlpha = remember { Animatable(initialValue = 1f) }

            LaunchedEffect(clickerInfo.remainedClicks) {
                if (clickerInfo.remainedClicks == 0) {
                    scaleAndAlpha.animateTo(
                        targetValue = 0.1f,
                        animationSpec = tween(200)
                    )
                    onExtraClickerDisappeared(clickerInfo)
                }
            }

            Clicker(
                onClickerClicked = { onExtraClickerClicked(clickerInfo) },
                onClickerPositioned = { },
                color = Color.Blue,
                modifier = Modifier
                    .size(clickerInfo.bounds.size.toDpSize())
                    .absoluteOffset(
                        x = with(localDensity) { clickerInfo.bounds.topLeft.x.toDp() },
                        y = with(localDensity) { clickerInfo.bounds.topLeft.y.toDp() }
                    )
                    .scale(scaleAndAlpha.value)
                    .alpha(scaleAndAlpha.value)
            )
        }
    }
}

@Composable
private fun Size.toDpSize(): DpSize {
    return with(LocalDensity.current) {
        DpSize(
            width = this@toDpSize.width.toDp(),
            height = this@toDpSize.height.toDp()
        )
    }
}
