package com.apps.leo.clicker.game.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apps.leo.clicker.game.ui.composables.LevelSection
import com.apps.leo.clicker.game.ui.composables.UpgradeButtonsSection
import com.apps.leo.clicker.game.ui.composables.clicker.ClickerSection
import com.apps.leo.clicker.game.ui.model.GameAction
import com.apps.leo.clicker.game.ui.model.GameSideEffects
import com.apps.leo.clicker.game.ui.model.GameUiState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sideEffects = viewModel.sideEffects

    GameScreen(
        state = state,
        sideEffects = sideEffects,
        onAction = viewModel::onAction
    )
}

@Composable
private fun GameScreen(
    state: GameUiState,
    sideEffects: SharedFlow<GameSideEffects>,
    onAction: (GameAction) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(
            modifier = Modifier.windowInsetsTopHeight(WindowInsets.systemBars)
        )
        LevelSection(
            state.levelText,
            state.levelPercentage,
        )
        ClickerSection(
            boosts = state.boosts,
            statistics = state.statistics,
            incomeSideEffects = sideEffects.filterIsInstance<GameSideEffects.ShowIncome>(),
            onBoostClicked = {},
            onClickerPositioned = { onAction(GameAction.OnClickerPositioned(it)) },
            onClickerClicked = { onAction(GameAction.OnClickerClicked) }
        )
        UpgradeButtonsSection(
            upgrades = state.upgradeButtons,
            onButtonClicked = { onAction(GameAction.OnUpgradeButtonClicked(it)) }
        )
        Spacer(
            modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars)
        )
    }
}