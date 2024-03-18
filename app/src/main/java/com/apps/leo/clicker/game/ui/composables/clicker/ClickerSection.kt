package com.apps.leo.clicker.game.ui.composables.clicker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
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
    onStatisticsPositioned: (bounds: Rect) -> Unit,
    onBoostsPositioned: (bounds: Rect) -> Unit,
    onClickerPositioned: (bounds: Rect) -> Unit,
    incomeSideEffects: Flow<GameSideEffects.ShowIncome>,
    extraClickers: List<ExtraClickerInfo>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f / 0.8f)
            .padding(horizontal = 12.dp)
            .onGloballyPositioned { coordinates -> onClickerAreaPositioned(coordinates.size) }
    ) {
        BoostsSecton(
            boosts = boosts,
            onBoostClicked = onBoostClicked,
            onBoostsPositioned = onBoostsPositioned,
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(0.18f)
        )
        StatisticsSecton(
            statistics = statistics,
            onStatisticsPositioned = onStatisticsPositioned,
            modifier = Modifier.align(Alignment.TopEnd)
        )
        Clicker(
            onClickerClicked = onClickerClicked,
            onClickerPositioned = onClickerPositioned,
            modifier = Modifier
                .align(BiasAlignment(0f, 0.5f))
                .fillMaxWidth(0.45f)
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
