package com.apps.leo.clicker.game.ui.composables.clicker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import com.apps.leo.clicker.game.ui.model.ExtraClickerInfo
import com.apps.leo.clicker.game.ui.model.GameSideEffects
import com.apps.leo.clicker.game.ui.model.GameUiState
import kotlinx.coroutines.flow.Flow

@Composable
fun ClickerSection(
    boosts: List<GameUiState.Boost>,
    statistics: GameUiState.Statistics,
    onBoostClicked: () -> Unit,
    onClickerClicked: () -> Unit,
    onExtraClickerClicked: (ExtraClickerInfo) -> Unit,
    onExtraClickerDisappeared: (ExtraClickerInfo) -> Unit,
    onClickerAreaPositioned: (size: IntSize) -> Unit,
    onClickerPositioned: (size: IntSize, centerCoordinates: Offset) -> Unit,
    incomeSideEffects: Flow<GameSideEffects.ShowIncome>,
    extraClickers: List<ExtraClickerInfo>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f / 0.8f)
            .onGloballyPositioned { coordinates -> onClickerAreaPositioned(coordinates.size) }
    ) {
        BoostsSecton(
            boosts = boosts,
            modifier = Modifier.align(Alignment.TopStart),
            onBoostClicked = onBoostClicked
        )
        StatisticsSecton(
            statistics = statistics,
            modifier = Modifier.align(Alignment.TopEnd)
        )
        Clicker(
            onClickerClicked = onClickerClicked,
            onClickerPositioned = onClickerPositioned,
            modifier = Modifier
                .align(BiasAlignment(0f, 0.5f))
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
        )
        ExtraClickersSection(
            extraClickers = extraClickers,
            onExtraClickerClicked = onExtraClickerClicked,
            onExtraClickerDisappeared = onExtraClickerDisappeared,
            modifier = Modifier.fillMaxWidth()
        )
        IncomeIdicationArea(
            incomeSideEffects = incomeSideEffects,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

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
                onClickerPositioned = { _, _ -> },
                color = Color.Blue,
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .aspectRatio(1f)
                    .absoluteOffset(
                        x = with(localDensity) { clickerInfo.topLeftCoordinates.x.toDp() },
                        y = with(localDensity) { clickerInfo.topLeftCoordinates.y.toDp() }
                    )
                    .scale(scaleAndAlpha.value)
                    .alpha(scaleAndAlpha.value)
            )
        }
    }
}