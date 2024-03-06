package com.apps.leo.clicker.game.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apps.leo.clicker.game.common.ui.formatAmountOfMoney
import com.apps.leo.clicker.game.domain.GetInitialUpgradesUseCase
import com.apps.leo.clicker.game.domain.model.UpgradeType
import com.apps.leo.clicker.game.ui.model.GameAction
import com.apps.leo.clicker.game.ui.model.GameState
import com.apps.leo.clicker.game.ui.model.GameUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

private const val NEW_LEVEL_PROGRESS_EXCEED_THRESHOLD = 0.03f
private const val LEVEL_PROGRESS_PER_CLICK = 0.07f
private const val LEVEL_PROGRESS_DECREASE_TICK = 60L
private const val LEVEL_PROGRESS_DECREASE_PER_TICK = 0.02f

@HiltViewModel
class GameViewModel @Inject constructor(
    private val getInitialUpgrades: GetInitialUpgradesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(getInitialGameState())
    private val _stateUi = MutableStateFlow(getInitialUIGameState())
    val state = _stateUi.asStateFlow()

    init {
        _state.onEach { gameState ->
            _stateUi.update { uiState ->
                uiState.copy(
                    levelText = "Level ${gameState.currentLevel}",
                    levelPercentage = gameState.levelProgress,
                    statistics = GameUiState.Statistics(
                        total = formatBalance(gameState.totalBalance),
                        passive = formatIncome(gameState.passiveIncome)
                    ),
                )
            }
        }.launchIn(viewModelScope)

        startLevelDecreaseTimer()
    }

    private fun startLevelDecreaseTimer() {
        viewModelScope.launch {
            while (true) {
                delay(LEVEL_PROGRESS_DECREASE_TICK)
                _state.update { state ->
                    val decreasedProgress = state.levelProgress - LEVEL_PROGRESS_DECREASE_PER_TICK
                    state.copy(
                        levelProgress = if (decreasedProgress < 0f) 0f else decreasedProgress
                    )
                }
            }
        }
    }

    private fun getInitialGameState(): GameState {
        return GameState(
            totalBalance = 0L,
            clickIncome = 100L,
            passiveIncome = 0L,
            levelProgress = 0f,
            currentLevel = 1,
        )
    }

    private fun getInitialUIGameState(): GameUiState {
        val upgrades = getInitialUpgrades()
        return GameUiState(
            levelText = "",
            levelPercentage = 0f,
            boosts = listOf(),
            statistics = GameUiState.Statistics(
                total = "",
                passive = "",
            ),
            upgradeButtons = upgrades.map { upgrade ->
                GameUiState.UpgradeButtonState(
                    priceText = "${upgrade.price}$",
                    isAvailable = true,
                    hasFreeBoost = false,
                    type = when (upgrade.type) {
                        UpgradeType.CLICK_INCOME -> GameUiState.UpgradeButtonState.UpgradeType.CLICK_INCOME
                        UpgradeType.ADD_CURSOR -> GameUiState.UpgradeButtonState.UpgradeType.ADD_CURSOR
                        UpgradeType.MERGE_CURSORS -> GameUiState.UpgradeButtonState.UpgradeType.MERGE_CURSORS
                        UpgradeType.CURSOR_INCOME -> GameUiState.UpgradeButtonState.UpgradeType.CURSOR_INCOME
                        UpgradeType.CURSOR_SPEED -> GameUiState.UpgradeButtonState.UpgradeType.CURSOR_SPEED
                    }
                )
            },
        )
    }

    private fun formatIncome(passiveIncome: Long): String {
        val formatedMoney = formatAmountOfMoney(passiveIncome)
        return "$formatedMoney/ Sec"
    }

    private fun formatBalance(totalBalance: Long): String {
        return formatAmountOfMoney(totalBalance)
    }

    fun onAction(action: GameAction) {
        when (action) {
            is GameAction.OnClickerClicked -> {
                _state.update {
                    val levelProgress = it.levelProgress + LEVEL_PROGRESS_PER_CLICK
                    val isLevelCompleted =
                        (levelProgress) >= 1f + NEW_LEVEL_PROGRESS_EXCEED_THRESHOLD
                    val levelProcessNormalized =
                        if (isLevelCompleted) 0f else min(levelProgress, 1f)
                    val newLevel = if (isLevelCompleted) it.currentLevel + 1 else it.currentLevel

                    it.copy(
                        totalBalance = it.totalBalance + it.clickIncome,
                        currentLevel = newLevel,
                        levelProgress = levelProcessNormalized
                    )
                }
            }

            is GameAction.OnBoostClicked -> {}
            is GameAction.OnUpgradeButtonClicked -> {}
            GameAction.OnCustomizeClicked -> {}
            GameAction.OnSettingsClicked -> {}
            GameAction.OnStatsClicked -> {}
        }
    }
}