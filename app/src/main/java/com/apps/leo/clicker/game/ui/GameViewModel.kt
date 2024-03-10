package com.apps.leo.clicker.game.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apps.leo.clicker.game.common.geometry.distance2D
import com.apps.leo.clicker.game.common.random.nextFloat
import com.apps.leo.clicker.game.common.random.nextOffset
import com.apps.leo.clicker.game.common.ui.formatAmountOfMoney
import com.apps.leo.clicker.game.domain.CalculatePassiveIncomeUseCase
import com.apps.leo.clicker.game.domain.GetInitialUpgradesUseCase
import com.apps.leo.clicker.game.domain.GetUpgradePriceUseCase
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
import kotlin.math.min
import kotlin.random.Random

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
                    },
                    extraClickers = gameState.extraClickers
                )
            }
        }.launchIn(viewModelScope)

        startLevelDecreaseTimer()
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
                    val updatedExtraClickers = it.extraClickers.toMutableList()
                    updatedExtraClickers.add(extraClickerInfo)

                    it.copy(
                        extraClickers = updatedExtraClickers.toList()
                    )
                }

                delay(2000) // delay between two extra clickers

                launch {
                    delay(1500) //lifespan
                    _state.update {
                        val updatedExtraClickers = it.extraClickers.toMutableList()
                        updatedExtraClickers.remove(extraClickerInfo)

                        it.copy(
                            extraClickers = updatedExtraClickers.toList()
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
            extraClickerIncome = 300L, //todo should be specific per map & level
            passiveIncome = GameState.PassiveIncome(
                speedOfWorkders = 1f, //todo constant
                incomePerWorkder = 1000, //todo should be specific per map
            ),
            levelProgress = 0f,
            currentLevel = 1,
            upgrades = getInitialUpgrades(),
            extraClickers = emptyList()
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

            is GameAction.OnUpgradeButtonClicked -> onUpgradeButtonClicked(action.upgradeButtonState)

            GameAction.OnClickerClicked -> onClickerClicked()
            GameAction.OnCustomizeClicked -> {}
            GameAction.OnSettingsClicked -> {}
            GameAction.OnStatsClicked -> {}
        }
    }

    private fun onExtraClickerClicked(info: ExtraClickerInfo) { //todo подумать над унификацией с onClickerClicked
        viewModelScope.launch {
            val clickerRadius = info.radius
            val clickerIncomeIndicationAreaRadius = clickerRadius * 0.3f
            val randomOffsetX = Random.nextFloat(
                -clickerIncomeIndicationAreaRadius,
                clickerIncomeIndicationAreaRadius
            ) //todo

            _sideEffects.emit(
                GameSideEffects.ShowIncome(
                    coordinates = info.centerCoordinates.copy(x = info.centerCoordinates.x + randomOffsetX),
                    incomeText = "+${formatAmountOfMoney(_state.value.clickIncome)}"
                )
            )
        }
        _state.update {
//            val levelProgress = it.levelProgress + LEVEL_PROGRESS_PER_CLICK
//            val isLevelCompleted = (levelProgress) >= (1f + NEW_LEVEL_PROGRESS_EXCEED_THRESHOLD)
//            val levelProcessNormalized = if (isLevelCompleted) 0f else min(levelProgress, 1f)
//            val newLevel = if (isLevelCompleted) it.currentLevel + 1 else it.currentLevel

            it.copy(
                totalBalance = it.totalBalance + it.clickIncome,
//                currentLevel = newLevel,
//                levelProgress = levelProcessNormalized
            )
        }
    }

    private fun onClickerClicked() {
        viewModelScope.launch {
            val clickerRadius = clickerSize.width / 2f
            val clickerIncomeIndicationAreaRadius = clickerRadius * 0.3f
            val randomOffsetX = Random.nextFloat(
                -clickerIncomeIndicationAreaRadius,
                clickerIncomeIndicationAreaRadius
            ) //todo

            _sideEffects.emit(
                GameSideEffects.ShowIncome(
                    coordinates = clickerPosition.copy(x = clickerPosition.x + randomOffsetX),
                    incomeText = "+${formatAmountOfMoney(_state.value.clickIncome)}"
                )
            )
        }
        _state.update {
            val levelProgress = it.levelProgress + LEVEL_PROGRESS_PER_CLICK
            val isLevelCompleted = (levelProgress) >= (1f + NEW_LEVEL_PROGRESS_EXCEED_THRESHOLD)
            val levelProcessNormalized = if (isLevelCompleted) 0f else min(levelProgress, 1f)
            val newLevel = if (isLevelCompleted) it.currentLevel + 1 else it.currentLevel

            it.copy(
                totalBalance = it.totalBalance + it.clickIncome,
                currentLevel = newLevel,
                extraClickerIncome = newLevel * 300L, //todo exonomy??
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
                    updatedWorkersList.sortBy { it.level }

                    it.copy(
                        passiveIncome = it.passiveIncome.copy(
                            workers = updatedWorkersList.toList()
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
                upgrades = updatedUpgradesList.toList()
            )
        }
    }
}