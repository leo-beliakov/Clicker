package com.apps.leo.clicker.game.ui

import androidx.lifecycle.ViewModel
import com.apps.leo.clicker.game.domain.GetInitialUpgradesUseCase
import com.apps.leo.clicker.game.domain.model.UpgradeType
import com.apps.leo.clicker.game.ui.model.GameAction
import com.apps.leo.clicker.game.ui.model.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val getInitialUpgrades: GetInitialUpgradesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(getInitialGameState())
    val state = _state.asStateFlow()

    private fun getInitialGameState(): GameState {
        val upgrades = getInitialUpgrades()
        return GameState(
            levelText = "Level 1",
            levelPercentage = 0.3f,
            boosts = listOf(),
            statistics = GameState.Statistics(
                total = "$12.3 K",
                passive = "1.3 K/Sec",
            ),
            upgradeButtons = upgrades.map { upgrade ->
                GameState.UpgradeButtonState(
                    priceText = "${upgrade.price}$",
                    isAvailable = true,
                    hasFreeBoost = false,
                    type = when (upgrade.type) {
                        UpgradeType.CLICK_INCOME -> GameState.UpgradeButtonState.UpgradeType.CLICK_INCOME
                        UpgradeType.ADD_CURSOR -> GameState.UpgradeButtonState.UpgradeType.ADD_CURSOR
                        UpgradeType.MERGE_CURSORS -> GameState.UpgradeButtonState.UpgradeType.MERGE_CURSORS
                        UpgradeType.CURSOR_INCOME -> GameState.UpgradeButtonState.UpgradeType.CURSOR_INCOME
                        UpgradeType.CURSOR_SPEED -> GameState.UpgradeButtonState.UpgradeType.CURSOR_SPEED
                    }
                )
            },
        )
    }

    fun onAction(action: GameAction) {
        when (action) {
            is GameAction.OnClickerClicked -> {}
            is GameAction.OnBoostClicked -> {}
            is GameAction.OnUpgradeButtonClicked -> {}
            GameAction.OnCustomizeClicked -> {}
            GameAction.OnSettingsClicked -> {}
            GameAction.OnStatsClicked -> {}
        }
    }
}