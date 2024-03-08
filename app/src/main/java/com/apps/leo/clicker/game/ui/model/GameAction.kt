package com.apps.leo.clicker.game.ui.model

import androidx.compose.ui.geometry.Offset

sealed interface GameAction {
    object OnClickerClicked : GameAction
    object OnSettingsClicked : GameAction
    object OnStatsClicked : GameAction
    object OnCustomizeClicked : GameAction

    data class OnClickerPositioned(
        val centerCoordinates: Offset
    ) : GameAction

    data class OnBoostClicked(
        val boost: GameUiState.Boost
    ) : GameAction

    data class OnUpgradeButtonClicked(
        val upgradeButtonState: GameUiState.UpgradeButtonState
    ) : GameAction
}