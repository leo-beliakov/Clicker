package com.apps.leo.clicker.game.ui.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

sealed interface GameAction {
    object OnClickerClicked : GameAction
    object OnSettingsClicked : GameAction
    object OnStatsClicked : GameAction
    object OnCustomizeClicked : GameAction

    data class OnClickerPositioned(
        val size: IntSize,
        val center: Offset
    ) : GameAction

    data class OnClickerAreaPositioned(
        val size: IntSize,
    ) : GameAction

    data class OnBoostClicked(
        val boost: GameUiState.Boost
    ) : GameAction

    data class OnUpgradeButtonClicked(
        val upgradeButtonState: GameUiState.UpgradeButtonState
    ) : GameAction

    data class OnExtraClickerClicked(
        val info: ExtraClickerInfo
    ) : GameAction
}