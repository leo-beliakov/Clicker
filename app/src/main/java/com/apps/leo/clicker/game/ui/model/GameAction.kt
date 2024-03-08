package com.apps.leo.clicker.game.ui.model

import androidx.compose.ui.geometry.Offset

sealed interface GameAction {
    object OnSettingsClicked : GameAction
    object OnStatsClicked : GameAction
    object OnCustomizeClicked : GameAction

    data class OnClickerClicked(
        val coordinates: Offset
    ) : GameAction

    data class OnBoostClicked(
        val boost: GameUiState.Boost
    ) : GameAction

    data class OnUpgradeButtonClicked(
        val upgradeButtonState: GameUiState.UpgradeButtonState
    ) : GameAction
}