package com.apps.leo.clicker.game.ui.model

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize

sealed interface GameAction {
    object OnClickerClicked : GameAction
    object onLevelUpgradeAnimationFinished : GameAction
    object OnSettingsClicked : GameAction
    object OnStatsClicked : GameAction
    object OnCustomizeClicked : GameAction

    data class OnClickerPositioned(
        val bounds: Rect
    ) : GameAction

    data class OnClickerAreaPositioned(
        val size: IntSize,
    ) : GameAction

    data class OnBoostsPositioned(
        val bounds: Rect,
    ) : GameAction

    data class OnStatisticsPositioned(
        val bounds: Rect,
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

    data class OnExtraClickerDisappeared(
        val info: ExtraClickerInfo
    ) : GameAction
}