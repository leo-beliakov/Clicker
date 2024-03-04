package com.apps.leo.clicker.game.ui.model

sealed interface GameAction {
    object OnClickerClicked : GameAction
    object OnSettingsClicked : GameAction
    object OnStatsClicked : GameAction
    object OnCustomizeClicked : GameAction
    data class OnBoostClicked(
        val boost: GameState.Boost
    ) : GameAction

    data class OnUpgradeButtonClicked(
        val upgradeButtonState: GameState.UpgradeButtonState
    ) : GameAction
}