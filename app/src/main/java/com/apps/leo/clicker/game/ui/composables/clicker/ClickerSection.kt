package com.apps.leo.clicker.game.ui.composables.clicker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.apps.leo.clicker.game.ui.model.GameSideEffects
import com.apps.leo.clicker.game.ui.model.GameUiState
import kotlinx.coroutines.flow.Flow

@Composable
fun ClickerSection(
    boosts: List<GameUiState.Boost>,
    statistics: GameUiState.Statistics,
    onBoostClicked: () -> Unit,
    onClickerClicked: () -> Unit,
    onClickerPositioned: (centerCoordinates: Offset) -> Unit,
    incomeSideEffects: Flow<GameSideEffects.ShowIncome>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f / 0.8f)
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
            modifier = Modifier.align(
                BiasAlignment(0f, 0.5f)
            )
        )
        IncomeIdicationArea(
            incomeSideEffects = incomeSideEffects,
            modifier = Modifier.fillMaxWidth()
        )
    }
}