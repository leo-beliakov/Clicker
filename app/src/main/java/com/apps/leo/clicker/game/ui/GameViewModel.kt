package com.apps.leo.clicker.game.ui

import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apps.leo.clicker.game.common.collections.swap
import com.apps.leo.clicker.game.common.random.nextFloat
import com.apps.leo.clicker.game.common.ui.formatAmountOfMoney
import com.apps.leo.clicker.game.domain.BoostsManager
import com.apps.leo.clicker.game.domain.ExtraClickersManager
import com.apps.leo.clicker.game.domain.GetInitialUpgradesUseCase
import com.apps.leo.clicker.game.domain.GetUpgradePriceUseCase
import com.apps.leo.clicker.game.domain.LevelManager
import com.apps.leo.clicker.game.domain.PassiveIncomeManager
import com.apps.leo.clicker.game.domain.model.ExtraClickerInfo
import com.apps.leo.clicker.game.domain.model.GameState
import com.apps.leo.clicker.game.domain.model.UpgradeType
import com.apps.leo.clicker.game.ui.mapper.GameStateMapper
import com.apps.leo.clicker.game.ui.model.GameAction
import com.apps.leo.clicker.game.ui.model.GameSideEffects
import com.apps.leo.clicker.game.ui.model.GameUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GameViewModel @Inject constructor(
    private val levelManager: LevelManager,
    private val boostsManager: BoostsManager,
    private val passiveIncomeManager: PassiveIncomeManager,
    private val extraClickersManager: ExtraClickersManager,
    private val getInitialUpgrades: GetInitialUpgradesUseCase,
    private val getUpgradePrice: GetUpgradePriceUseCase,
    private val mapper: GameStateMapper,
) : ViewModel() {

    //todo think about separation into upgrades / stats / etc
    private val _state = MutableStateFlow(getInitialGameState())
    private val _stateUi = MutableStateFlow(getInitialUIGameState())
    val state = _stateUi.asStateFlow()

    private val _sideEffects = MutableSharedFlow<GameSideEffects>(replay = 1)
    val sideEffects = _sideEffects.asSharedFlow()

    private var clickerBounds = Rect.Zero

    init {
        subscribeToLevelState()
        subscribeToBoosts()
//        subscribeToExtraClickersState()
        subscribeToPassiveIncomeState()

        _state.onEach { gameState ->
            _stateUi.update { uiState ->
                mapper.map(uiState, gameState)
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToLevelState() {
        levelManager.state.onEach { levelState ->
            _state.update {
                it.copy(
                    level = levelState,
                    extraClickerIncome = levelState.currentLevel * 300L, //todo economy??
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToBoosts() {
        boostsManager.state.onEach { boosts ->
            _state.update {
                it.copy(boosts = boosts)
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToExtraClickersState() {
        extraClickersManager.state.onEach { extraClickers ->
            _state.update { it.copy(extraClickers = extraClickers) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToPassiveIncomeState() {
        passiveIncomeManager.state.onEach { passiveIncomeState ->
            _state.update { it.copy(passiveIncome = passiveIncomeState) }
        }.launchIn(viewModelScope)
    }

    private fun getInitialGameState(): GameState {
        return GameState(
            totalBalance = 0L,
            clickIncome = 100L, //todo should be specific per map
            extraClickerIncome = 300L, //todo should be specific per map & level
            passiveIncome = GameState.PassiveIncome(
                speedOfWorkders = 1f, //todo constant
                incomePerWorkder = 1000, //todo should be specific per map
            ),
            upgrades = getInitialUpgrades(),
            extraClickers = emptyList(),
            level = GameState.LevelState(
                progress = 0f,
                currentLevel = 1,
                isUpgrading = false
            ),
            boosts = emptyList()
        )
    }

    private fun getInitialUIGameState(): GameUiState {
        return GameUiState(
            levelText = "",
            levelPercentage = 0f
        )
    }

    fun onAction(action: GameAction) {
        when (action) {
            is GameAction.OnBoostClicked -> {
                _stateUi.update {
                    it.copy(
                        dialogState = GameUiState.DialogState(
                            action.boost,
                            action.boost.status.toString()
                        )
                    )
                }
            }

            is GameAction.OnClickerPositioned -> {
                extraClickersManager.clickerBounds = action.bounds
                clickerBounds = action.bounds
            }

            is GameAction.OnClickerAreaPositioned -> {
                extraClickersManager.clickerAreaSize = action.size
            }

            is GameAction.OnExtraClickerClicked -> onExtraClickerClicked(action.info)

            is GameAction.OnExtraClickerDisappeared -> {
                extraClickersManager.onExtraClickerDisappeared(action.info)
            }

            is GameAction.OnUpgradeButtonClicked -> onUpgradeButtonClicked(action.upgradeButtonState)

            GameAction.OnClickerClicked -> onClickerClicked()
            GameAction.OnCustomizeClicked -> {}
            GameAction.OnSettingsClicked -> {}
            GameAction.OnStatsClicked -> {}
            GameAction.onLevelUpgradeAnimationFinished -> levelManager.finishLevelUpgrade()
            is GameAction.OnBoostsPositioned -> {
                extraClickersManager.boostsAreaBounds = action.bounds
            }

            is GameAction.OnStatisticsPositioned -> {
                extraClickersManager.statisticsAreaBounds = action.bounds
            }

            is GameAction.OnBoostConfirmed -> {
                boostsManager.activateBoost(action.boostType)
                _stateUi.update {
                    it.copy(dialogState = null)
                }
            }

            GameAction.OnDialogDismissed -> {
                _stateUi.update {
                    it.copy(dialogState = null)
                }
            }
        }
    }

    private fun onExtraClickerClicked(info: ExtraClickerInfo) {
        showClickIndication(
            clickerBounds = info.bounds,
            incomePerClick = _state.value.extraClickerIncome
        )

        extraClickersManager.onExtraClickerClicked(info)
        _state.update {
            it.copy(totalBalance = it.totalBalance + it.extraClickerIncome)
        }
    }

    private fun onClickerClicked() {
        levelManager.processClick()

        showClickIndication(
            clickerBounds = clickerBounds,
            incomePerClick = _state.value.clickIncome
        )

        _state.update {
            it.copy(totalBalance = it.totalBalance + it.clickIncome)
        }
    }

    private fun showClickIndication(
        clickerBounds: Rect,
        incomePerClick: Long
    ) {
        viewModelScope.launch {
            val clickerIncomeIndicationAreaRadius = clickerBounds.width * 0.15f
            val randomOffsetX = Random.nextFloat(
                from = -clickerIncomeIndicationAreaRadius,
                until = clickerIncomeIndicationAreaRadius
            )

            _sideEffects.emit(
                GameSideEffects.ShowIncome(
                    coordinates = clickerBounds.center.copy(x = clickerBounds.center.x + randomOffsetX),
                    incomeText = "+${formatAmountOfMoney(incomePerClick)}"
                )
            )
        }
    }

    private fun onUpgradeButtonClicked(buttonState: GameUiState.UpgradeButtonState) {
        val upgrade = _state.value.upgrades.first { it.type == buttonState.type }
        when (upgrade.type) {
            UpgradeType.ADD_CURSOR -> passiveIncomeManager.addWorker()
            UpgradeType.MERGE_CURSORS -> passiveIncomeManager.mergeWorkers()
            UpgradeType.CURSOR_INCOME -> passiveIncomeManager.upgradeIncome()
            UpgradeType.CURSOR_SPEED -> passiveIncomeManager.upgradeSpeed()
            UpgradeType.CLICK_INCOME -> {
                _state.update { it.copy(clickIncome = it.clickIncome * 2) }
            }
        }

        val updatedUpgrade = upgrade.copy(
            level = upgrade.level + 1,
            price = getUpgradePrice(upgrade.type, upgrade.level + 1)
        )

        _state.update {
            it.copy(
                totalBalance = it.totalBalance - buttonState.price,
                upgrades = it.upgrades.swap(upgrade, updatedUpgrade)
            )
        }
    }
}