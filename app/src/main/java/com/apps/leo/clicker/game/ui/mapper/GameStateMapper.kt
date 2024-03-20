package com.apps.leo.clicker.game.ui.mapper

import androidx.compose.ui.graphics.Color
import com.apps.leo.clicker.R
import com.apps.leo.clicker.game.common.ui.formatAmountOfMoney
import com.apps.leo.clicker.game.domain.CalculatePassiveIncomeUseCase
import com.apps.leo.clicker.game.domain.CheckIfUpgradeIsAvailableUseCase
import com.apps.leo.clicker.game.domain.CheckIfUpgradeIsMaxUseCase
import com.apps.leo.clicker.game.domain.model.GameState
import com.apps.leo.clicker.game.domain.model.Upgrade
import com.apps.leo.clicker.game.domain.model.UpgradeType
import com.apps.leo.clicker.game.ui.model.GameUiState
import javax.inject.Inject

class GameStateMapper @Inject constructor(
    private val checkIfUpgradeIsMax: CheckIfUpgradeIsMaxUseCase,
    private val checkIfUpgradeIsAvailable: CheckIfUpgradeIsAvailableUseCase,
    private val calculatePassiveIncome: CalculatePassiveIncomeUseCase
) {

    fun map(currentUiState: GameUiState, gameState: GameState): GameUiState {
        return currentUiState.copy(
            levelText = "Level ${gameState.level.currentLevel}",
            levelPercentage = gameState.level.progress,
            statistics = GameUiState.Statistics(
                total = formatBalance(gameState.totalBalance),
                passive = formatIncome(calculatePassiveIncome(gameState.passiveIncome))
            ),
            upgradeButtons = gameState.upgrades.map {//todo i don't like that we have to map it on every state change
                mapUpgradeToButtonState(
                    upgrade = it,
                    gameState = gameState
                )
            },
            extraClickers = gameState.extraClickers,
            boosts = mapBoosts(gameState.boosts)
        )
    }

    fun mapUpgradeToButtonState(
        upgrade: Upgrade,
        gameState: GameState
    ): GameUiState.UpgradeButtonState {
        return GameUiState.UpgradeButtonState(
            type = upgrade.type,
            price = upgrade.price,
            priceText = formatAmountOfMoney(
                amount = upgrade.price,
                moneySymbol = "",
                spaceSymbol = ""
            ),
            isMax = checkIfUpgradeIsMax(upgrade, gameState),
            isAvailable = checkIfUpgradeIsAvailable(upgrade, gameState),
            hasFreeBoost = false,
            titleResId = when (upgrade.type) {
                UpgradeType.CLICK_INCOME -> R.string.upgrade_click_income_title
                UpgradeType.ADD_CURSOR -> R.string.upgrade_add_cursor_title
                UpgradeType.MERGE_CURSORS -> R.string.upgrade_merge_cursors_title
                UpgradeType.CURSOR_INCOME -> R.string.upgrade_cursor_income_title
                UpgradeType.CURSOR_SPEED -> R.string.upgrade_cursor_speed_title
            },
            textResId = when (upgrade.type) {
                UpgradeType.CLICK_INCOME -> R.string.upgrade_click_income_text
                UpgradeType.ADD_CURSOR -> R.string.upgrade_add_cursor_text
                UpgradeType.MERGE_CURSORS -> R.string.upgrade_merge_cursors_text
                UpgradeType.CURSOR_INCOME -> R.string.upgrade_cursor_income_text
                UpgradeType.CURSOR_SPEED -> R.string.upgrade_cursor_speed_text
            },
            iconResId = when (upgrade.type) {
                UpgradeType.CLICK_INCOME -> R.drawable.ic_cursor_upgrade
                UpgradeType.ADD_CURSOR -> R.drawable.ic_cursor_upgrade
                UpgradeType.MERGE_CURSORS -> R.drawable.ic_cursor_upgrade
                UpgradeType.CURSOR_INCOME -> R.drawable.ic_cursor_upgrade
                UpgradeType.CURSOR_SPEED -> R.drawable.ic_cursor_upgrade
            },
        )
    }

    private fun mapBoosts(boosts: GameState.BoostsState): List<GameUiState.Boost> {
        val activeBoosts = boosts.activeBoosts.map { boost ->
            GameUiState.Boost(
                type = boost.type,
                imageResId = when (boost.type) {
                    GameState.BoostsState.BoostType.INCOME_X2 -> R.drawable.ic_income_x2
                    GameState.BoostsState.BoostType.INCOME_X6 -> R.drawable.ic_income_x6
                    GameState.BoostsState.BoostType.AUTO_CLICK -> R.drawable.ic_auto_click
                },
                imageActivatedResId = when (boost.type) {
                    GameState.BoostsState.BoostType.INCOME_X2 -> R.drawable.ic_income_x2_zoom
                    GameState.BoostsState.BoostType.INCOME_X6 -> R.drawable.ic_income_x6_zoom
                    GameState.BoostsState.BoostType.AUTO_CLICK -> R.drawable.ic_auto_click
                },
                color = when (boost.type) {
                    GameState.BoostsState.BoostType.INCOME_X2 -> Color.Green
                    GameState.BoostsState.BoostType.INCOME_X6 -> Color.Yellow
                    GameState.BoostsState.BoostType.AUTO_CLICK -> Color.Magenta
                },
                status = when (val state = boost.state) {
                    is GameState.BoostsState.BoostState.Available -> GameUiState.Boost.BoostStatus.PermanentlyAvailable
                    is GameState.BoostsState.BoostState.Active -> GameUiState.Boost.BoostStatus.Activated(
                        timeLeftPercentage = state.timeLeft.toFloat() / state.timeTotal
                    )
                }
            )
        }

        val availableBoosts = boosts.availableBoosts.map { boost ->
            GameUiState.Boost(
                type = boost.type,
                imageResId = when (boost.type) {
                    GameState.BoostsState.BoostType.INCOME_X2 -> R.drawable.ic_income_x2
                    GameState.BoostsState.BoostType.INCOME_X6 -> R.drawable.ic_income_x6
                    GameState.BoostsState.BoostType.AUTO_CLICK -> R.drawable.ic_auto_click
                },
                imageActivatedResId = when (boost.type) {
                    GameState.BoostsState.BoostType.INCOME_X2 -> R.drawable.ic_income_x2_zoom
                    GameState.BoostsState.BoostType.INCOME_X6 -> R.drawable.ic_income_x6_zoom
                    GameState.BoostsState.BoostType.AUTO_CLICK -> R.drawable.ic_auto_click
                },
                color = when (boost.type) {
                    GameState.BoostsState.BoostType.INCOME_X2 -> Color.Green
                    GameState.BoostsState.BoostType.INCOME_X6 -> Color.Yellow
                    GameState.BoostsState.BoostType.AUTO_CLICK -> Color.Magenta
                },
                status = when (val state = boost.state) {
                    is GameState.BoostsState.BoostState.Available -> GameUiState.Boost.BoostStatus.PermanentlyAvailable
                    is GameState.BoostsState.BoostState.Active -> GameUiState.Boost.BoostStatus.Activated(
                        timeLeftPercentage = state.timeLeft.toFloat() / state.timeTotal
                    )
                }
            )
        }

        return activeBoosts + availableBoosts
    }

    private fun formatIncome(passiveIncome: Long): String {
        val formatedMoney = formatAmountOfMoney(passiveIncome)
        return "$formatedMoney/Sec"
    }

    private fun formatBalance(totalBalance: Long): String {
        return formatAmountOfMoney(totalBalance)
    }
}