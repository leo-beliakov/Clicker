package com.apps.leo.clicker.game.domain

import com.apps.leo.clicker.game.domain.model.GameState
import com.apps.leo.clicker.game.domain.model.Upgrade
import com.apps.leo.clicker.game.domain.model.UpgradeType
import javax.inject.Inject

class CheckIfUpgradeIsAvailableUseCase @Inject constructor() {
    operator fun invoke(
        upgrade: Upgrade,
        gameState: GameState
    ): Boolean {
        val hasEnoughMoney = upgrade.price <= gameState.totalBalance
        val isUpgradeApplicable = when (upgrade.type) {
            UpgradeType.CLICK_INCOME,
            UpgradeType.ADD_CURSOR,
            UpgradeType.CURSOR_INCOME,
            UpgradeType.CURSOR_SPEED -> true

            UpgradeType.MERGE_CURSORS -> {
                gameState.passiveIncome.workers
                    .groupBy { it.level }
                    .any { (_, workers) -> workers.size >= PASSIVE_INCOME_WORKERS_REQUIRED_FOR_UPGRADE }
            }
        }

        return hasEnoughMoney && isUpgradeApplicable
    }
}