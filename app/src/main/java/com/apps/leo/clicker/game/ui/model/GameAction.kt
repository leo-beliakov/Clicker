package com.apps.leo.clicker.game.ui.model

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import com.apps.leo.clicker.game.domain.model.Boost
import com.apps.leo.clicker.game.domain.model.ExtraClickerInfo

sealed interface GameAction {
    object OnDialogDismissed : GameAction
    object OnClickerClicked : GameAction
    object onLevelUpgradeAnimationFinished : GameAction
    object OnSettingsClicked : GameAction
    object OnStatsClicked : GameAction
    object OnCustomizeClicked : GameAction

    data class OnClickerPositioned(
        val bounds: Rect
    ) : GameAction

    data class OnBoostConfirmed(
        val boostType: Boost.Type
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
        val boost: BoostUi
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