package com.apps.leo.clicker.game.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apps.leo.clicker.R
import com.apps.leo.clicker.game.common.collections.add
import com.apps.leo.clicker.game.common.collections.remove
import com.apps.leo.clicker.game.common.collections.swap
import com.apps.leo.clicker.game.common.geometry.distance2D
import com.apps.leo.clicker.game.common.random.nextFloat
import com.apps.leo.clicker.game.common.random.nextOffset
import com.apps.leo.clicker.game.common.ui.formatAmountOfMoney
import com.apps.leo.clicker.game.domain.CalculatePassiveIncomeUseCase
import com.apps.leo.clicker.game.domain.GetInitialUpgradesUseCase
import com.apps.leo.clicker.game.domain.GetUpgradePriceUseCase
import com.apps.leo.clicker.game.domain.LevelManager
import com.apps.leo.clicker.game.domain.PASSIVE_INCOME_WORKERS_REQUIRED_FOR_UPGRADE
import com.apps.leo.clicker.game.domain.model.UpgradeType
import com.apps.leo.clicker.game.ui.mapper.GameStateMapper
import com.apps.leo.clicker.game.ui.model.ExtraClickerInfo
import com.apps.leo.clicker.game.ui.model.GameAction
import com.apps.leo.clicker.game.ui.model.GameSideEffects
import com.apps.leo.clicker.game.ui.model.GameState
import com.apps.leo.clicker.game.ui.model.GameUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

private const val PASSIVE_INCOME_TICK = 1000L

private const val EXTRA_CLICKER_LIFESPAN = 4000L
private const val EXTRA_CLICKER_SPAWN_DELAY = 3000L

@HiltViewModel
class GameViewModel @Inject constructor(
    private val levelManager: LevelManager,
    private val calculatePassiveIncome: CalculatePassiveIncomeUseCase,
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

    private var clickerSize = IntSize.Zero
    private var clickerPosition = Offset.Zero
    private var clickerAreaSize = IntSize.Zero

    init {
        levelManager.level.onEach { levelState ->
            _state.update {
                it.copy(
                    level = levelState,
                    extraClickerIncome = levelState.currentLevel * 300L, //todo economy??
                )
            }
        }.launchIn(viewModelScope)

        _state.onEach { gameState ->
            _stateUi.update { uiState ->
                uiState.copy(
                    levelText = "Level ${gameState.level.currentLevel}",
                    levelPercentage = gameState.level.progress,
                    statistics = GameUiState.Statistics(
                        total = formatBalance(gameState.totalBalance),
                        passive = formatIncome(calculatePassiveIncome(gameState.passiveIncome))
                    ),
                    upgradeButtons = gameState.upgrades.map {//todo i don't like that we have to map it on every state change
                        mapper.mapUpgradeToButtonState(
                            upgrade = it,
                            gameState = gameState
                        )
                    },
                    extraClickers = gameState.extraClickers,
                    boosts = listOf(
                        GameUiState.Boost(
                            id = UUID.randomUUID(),
                            textResId = R.string.boostIncomeX2,
                            color = Color.Green,
                            status = GameUiState.Boost.BoostStatus.TemporarilyAvailable(
                                timeLeftPercentage = 0.6f,
                            )
                        )
                    )
                )
            }
        }.launchIn(viewModelScope)


        startPassiveIncomeTimer()
        startExtraClickersTimer()
    }

    private fun startExtraClickersTimer() {
        viewModelScope.launch {
            delay(2000)
            while (true) {
                //todo reifine conditions to emit random clickers (start & end of the level)
                val clickerRadius = clickerSize.width / 2f
                val extraClickerRadius = clickerRadius / 3

                val centerCoordinates = generateExtraClickerCoordinates()
                val topLeftCoordinates = centerCoordinates.copy(
                    x = centerCoordinates.x - extraClickerRadius,
                    y = centerCoordinates.y - extraClickerRadius
                )

                val extraClickerInfo = ExtraClickerInfo(
                    id = UUID.randomUUID(),
                    remainedClicks = 1,
                    radius = extraClickerRadius,
                    centerCoordinates = centerCoordinates,
                    topLeftCoordinates = topLeftCoordinates,
                )

                _state.update {
                    val updatedClickersList = it.extraClickers.add(extraClickerInfo)
                    it.copy(extraClickers = updatedClickersList)
                }

                delay(EXTRA_CLICKER_SPAWN_DELAY)

                launch {
                    delay(EXTRA_CLICKER_LIFESPAN)
                    _state.update {
                        it.copy(
                            extraClickers = it.extraClickers.swap(
                                extraClickerInfo,
                                extraClickerInfo.reduceClicks()
                            )
                        )
                    }
                }
            }
        }
    }

    private fun generateExtraClickerCoordinates(): Offset {
        val clickerRadius = clickerSize.width / 2f
        val smallClickerRadius = clickerRadius / 3//todo should be a field?
        val prohibitedAreaRadius = clickerRadius + smallClickerRadius
        val otherClickers = _state.value.extraClickers

        while (true) {
            val randomPoint = Random.nextOffset(
                xFrom = smallClickerRadius,
                xUntil = clickerAreaSize.width - smallClickerRadius,
                yFrom = smallClickerRadius,
                yUntil = clickerAreaSize.height - smallClickerRadius,
            )

            val isNotOverlaping = otherClickers.all { clicker ->
                distance2D(randomPoint, clicker.centerCoordinates) > (smallClickerRadius * 2)
            }

            if (isNotOverlaping && distance2D(
                    randomPoint,
                    clickerPosition
                ) > prohibitedAreaRadius
            ) {
                return randomPoint
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
            boosts = GameState.BoostsState(
                availableBoosts = emptyList(),
                activeBoosts = emptyList(),
            )
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
            is GameAction.OnBoostClicked -> {}
            is GameAction.OnClickerPositioned -> {
                clickerSize = action.size
                clickerPosition = action.center
            }

            is GameAction.OnClickerAreaPositioned -> {
                clickerAreaSize = action.size
            }

            is GameAction.OnExtraClickerClicked -> onExtraClickerClicked(action.info)

            is GameAction.OnExtraClickerDisappeared -> onExtraClickerDisappeared(action.info)

            is GameAction.OnUpgradeButtonClicked -> onUpgradeButtonClicked(action.upgradeButtonState)

            GameAction.OnClickerClicked -> onClickerClicked()
            GameAction.OnCustomizeClicked -> {}
            GameAction.OnSettingsClicked -> {}
            GameAction.OnStatsClicked -> {}
            GameAction.onLevelUpgradeAnimationFinished -> levelManager.finishLevelUpgrade()
        }
    }

    private fun onExtraClickerDisappeared(info: ExtraClickerInfo) {
        _state.update {
            it.copy(extraClickers = it.extraClickers.remove(info))
        }
    }

    private fun onExtraClickerClicked(info: ExtraClickerInfo) {
        showClickIndication(
            clickerRadius = info.radius,
            clickerCenter = info.centerCoordinates,
            incomePerClick = _state.value.extraClickerIncome
        )

        _state.update {
            it.copy(
                totalBalance = it.totalBalance + it.extraClickerIncome,
                extraClickers = it.extraClickers.swap(info, info.reduceClicks())
            )
        }
    }

    private fun onClickerClicked() {
        levelManager.processClick()

        showClickIndication(
            clickerRadius = clickerSize.width / 2f,
            clickerCenter = clickerPosition,
            incomePerClick = _state.value.clickIncome
        )

        _state.update {
            it.copy(totalBalance = it.totalBalance + it.clickIncome)
        }
    }

    private fun showClickIndication(
        clickerRadius: Float,
        clickerCenter: Offset,
        incomePerClick: Long
    ) {
        viewModelScope.launch {
            val clickerIncomeIndicationAreaRadius = clickerRadius * 0.3f
            val randomOffsetX = Random.nextFloat(
                from = -clickerIncomeIndicationAreaRadius,
                until = clickerIncomeIndicationAreaRadius
            )

            _sideEffects.emit(
                GameSideEffects.ShowIncome(
                    coordinates = clickerCenter.copy(x = clickerCenter.x + randomOffsetX),
                    incomeText = "+${formatAmountOfMoney(incomePerClick)}"
                )
            )
        }
    }

    private fun onUpgradeButtonClicked(buttonState: GameUiState.UpgradeButtonState) {
        _state.update {
            val upgrade = it.upgrades.first { it.type == buttonState.type }
            val updatedUpgrade = upgrade.copy(
                level = upgrade.level + 1,
                price = getUpgradePrice(upgrade.type, upgrade.level + 1)
            )

            //apply effects of the upgrade
            val updatedState = when (upgrade.type) {
                UpgradeType.CLICK_INCOME -> {
                    //increase income per click
                    it.copy(
                        clickIncome = it.clickIncome * 2
                    )
                }

                UpgradeType.ADD_CURSOR -> {
                    val updatedWorkersList = it.passiveIncome.workers
                        .add(GameState.PassiveIncome.Worker())
                        .sortedBy { it.level }

                    it.copy(
                        passiveIncome = it.passiveIncome.copy(
                            workers = updatedWorkersList
                        )
                    )
                }

                UpgradeType.MERGE_CURSORS -> {
                    val workers = it.passiveIncome.workers
                    val updatedWorkersList = workers.toMutableList()
                    val workersLevels = workers.map { it.level }.toSet()

                    for (level in workersLevels) {
                        if (workers.count { it.level == level } >= PASSIVE_INCOME_WORKERS_REQUIRED_FOR_UPGRADE) {
                            repeat(PASSIVE_INCOME_WORKERS_REQUIRED_FOR_UPGRADE) {
                                val workerToRemove = updatedWorkersList.first { it.level == level }
                                updatedWorkersList.remove(workerToRemove)
                            }
                            updatedWorkersList.add(GameState.PassiveIncome.Worker(level = level + 1))
                            updatedWorkersList.sortBy { it.level }
                            break
                        }
                    }

                    it.copy(
                        passiveIncome = it.passiveIncome.copy(
                            workers = updatedWorkersList.toList()
                        )
                    )
                }

                UpgradeType.CURSOR_INCOME -> {
                    it.copy(
                        passiveIncome = it.passiveIncome.copy(
                            incomePerWorkder = (it.passiveIncome.incomePerWorkder * 1.1).toLong()
                        )
                    )
                }

                UpgradeType.CURSOR_SPEED -> {
                    it.copy(
                        passiveIncome = it.passiveIncome.copy(
                            speedOfWorkders = it.passiveIncome.speedOfWorkders * 1.1f
                        )
                    )
                }
            }

            updatedState.copy(
                totalBalance = it.totalBalance - buttonState.price,
                upgrades = it.upgrades.swap(upgrade, updatedUpgrade)
            )
        }
    }
}