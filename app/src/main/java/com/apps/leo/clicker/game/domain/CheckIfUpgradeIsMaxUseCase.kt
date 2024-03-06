package com.apps.leo.clicker.game.domain

import com.apps.leo.clicker.game.domain.model.Upgrade
import com.apps.leo.clicker.game.domain.model.UpgradeType
import com.apps.leo.clicker.game.ui.model.GameState
import javax.inject.Inject

private const val PASSIVE_INCOME_MAX_WORKERS = 12

class CheckIfUpgradeIsMaxUseCase @Inject constructor() {
    operator fun invoke(
        upgrade: Upgrade,
        gameState: GameState //todo gameState is in UI. Consider moving it to domain
    ): Boolean {
        return when (upgrade.type) {
            UpgradeType.CLICK_INCOME -> false
            UpgradeType.ADD_CURSOR -> gameState.passiveIncome.workers.size == PASSIVE_INCOME_MAX_WORKERS
            UpgradeType.MERGE_CURSORS -> false
            UpgradeType.CURSOR_INCOME -> false
            UpgradeType.CURSOR_SPEED -> false
        }
    }
}