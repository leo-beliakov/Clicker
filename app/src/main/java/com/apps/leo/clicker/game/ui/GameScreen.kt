package com.apps.leo.clicker.game.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apps.leo.clicker.game.ui.composables.ClickerSection
import com.apps.leo.clicker.game.ui.composables.LevelSection
import com.apps.leo.clicker.game.ui.composables.UpgradeButtonsSection
import com.apps.leo.clicker.game.ui.model.GameAction
import com.apps.leo.clicker.game.ui.model.GameUiState

@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    GameScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun GameScreen(
    state: GameUiState,
    onAction: (GameAction) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        LevelSection(
            state.levelText,
            state.levelPercentage,
        )
        ClickerSection(
            state.boosts,
            state.statistics,
            onBoostClicked = {},
            onClickerClicked = { onAction(GameAction.OnClickerClicked) }
        )
        UpgradeButtonsSection(
            upgrades = state.upgradeButtons,
            onButtonClicked = {}
        )
    }
}