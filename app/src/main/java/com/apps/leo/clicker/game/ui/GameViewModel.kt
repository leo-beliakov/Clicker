package com.apps.leo.clicker.game.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apps.leo.clicker.game.common.ui.formatAmountOfMoney
import com.apps.leo.clicker.game.domain.CalculatePassiveIncomeUseCase
import com.apps.leo.clicker.game.domain.GetInitialUpgradesUseCase
import com.apps.leo.clicker.game.domain.GetUpgradePriceUseCase
import com.apps.leo.clicker.game.domain.model.UpgradeType
import com.apps.leo.clicker.game.ui.mapper.GameStateMapper
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

private const val PASSIVE_INCOME_TICK = 1000L

@HiltViewModel
class GameViewModel @Inject constructor(
    private val calculatePassiveIncome: CalculatePassiveIncomeUseCase,
    private val getInitialUpgrades: GetInitialUpgradesUseCase,
    private val getUpgradePrice: GetUpgradePriceUseCase,
    private val mapper: GameStateMapper,
) : ViewModel() {

    private val _state =
        MutableStateFlow(getInitialGameState()) //todo think about separation into upgrades / stats / etc
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
                        passive = formatIncome(calculatePassiveIncome(gameState.passiveIncome))
                    ),
                    upgradeButtons = gameState.upgrades.map {//todo i don't like that we have to map it on every state change
                        mapper.mapUpgradeToButtonState(
                            upgrade = it,
                            gameState = gameState
                        )
                    }
                )
            }
        }.launchIn(viewModelScope)

        startLevelDecreaseTimer()
        startPassiveIncomeTimer()
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

    private fun startPassiveIncomeTimer() {
        viewModelScope.launch {
            while (true) {
                delay(PASSIVE_INCOME_TICK)
                _state.update { gameState ->
                    gameState.copy(
                        totalBalance = gameState.totalBalance + calculatePassiveIncome(gameState.passiveIncome)
                    )
                }
            }
        }
    }

    private fun getInitialGameState(): GameState {
        return GameState(
            totalBalance = 0L,
            clickIncome = 100L, //todo should be specific per map
            passiveIncome = GameState.PassiveIncome(
                speedOfWorkders = 1f, //todo constant
                incomePerWorkder = 1000, //todo should be specific per map
            ),
            levelProgress = 0f,
            currentLevel = 1,
            upgrades = getInitialUpgrades()
        )
    }

    private fun getInitialUIGameState(): GameUiState {
        return GameUiState(
            levelText = "",
            levelPercentage = 0f
        )
    }

    private fun formatIncome(passiveIncome: Long): String {
        val formatedMoney = formatAmountOfMoney(passiveIncome)
        return "$formatedMoney/Sec"
    }

    private fun formatBalance(totalBalance: Long): String {
        return formatAmountOfMoney(totalBalance)
    }

    fun onAction(action: GameAction) {
        when (action) {
            GameAction.OnClickerClicked -> onClickerClicked()

            is GameAction.OnBoostClicked -> {}

            is GameAction.OnUpgradeButtonClicked -> onUpgradeButtonClicked(action.upgradeButtonState)

            GameAction.OnCustomizeClicked -> {}
            GameAction.OnSettingsClicked -> {}
            GameAction.OnStatsClicked -> {}
        }
    }

    private fun onClickerClicked() {
        _state.update {
            val levelProgress = it.levelProgress + LEVEL_PROGRESS_PER_CLICK
            val isLevelCompleted = (levelProgress) >= (1f + NEW_LEVEL_PROGRESS_EXCEED_THRESHOLD)
            val levelProcessNormalized = if (isLevelCompleted) 0f else min(levelProgress, 1f)
            val newLevel = if (isLevelCompleted) it.currentLevel + 1 else it.currentLevel

            it.copy(
                totalBalance = it.totalBalance + it.clickIncome,
                currentLevel = newLevel,
                levelProgress = levelProcessNormalized
            )
        }
    }

    private fun onUpgradeButtonClicked(buttonState: GameUiState.UpgradeButtonState) {
        _state.update {
            val indexOfUpgrade = it.upgrades.indexOfFirst { it.type == buttonState.type }
            val upgrade = it.upgrades[indexOfUpgrade]
            val upgradeUpdatedLevel = upgrade.level + 1
            val updatedUpgrade = upgrade.copy(
                level = upgrade.level + 1,
                price = getUpgradePrice(upgrade.type, upgradeUpdatedLevel)
            )

            val updatedUpgradesList = it.upgrades.toMutableList() //todo move to utils?
            updatedUpgradesList.removeAt(indexOfUpgrade)
            updatedUpgradesList.add(indexOfUpgrade, updatedUpgrade)

            //apply effects of the upgrade
            val updatedState = when (upgrade.type) {
                UpgradeType.CLICK_INCOME -> {
                    //increase income per click
                    it.copy(
                        clickIncome = it.clickIncome * 2
                    )
                }

                UpgradeType.ADD_CURSOR -> {
                    val updatedWorkersList = it.passiveIncome.workers.toMutableList()
                    updatedWorkersList.add(GameState.PassiveIncome.Worker())

                    it.copy(
                        passiveIncome = it.passiveIncome.copy(
                            workers = updatedWorkersList.toList()
                        )
                    )
                }

                UpgradeType.MERGE_CURSORS -> {
                    // merge passive income miners
                    it
                }

                UpgradeType.CURSOR_INCOME -> {
                    // increase passive income per miner
                    it
                }

                UpgradeType.CURSOR_SPEED -> {
                    // increase speed of passive income miners
                    it
                }
            }

            updatedState.copy(
                totalBalance = it.totalBalance - buttonState.price,
                upgrades = updatedUpgradesList.toList()
            )
        }
    }
}