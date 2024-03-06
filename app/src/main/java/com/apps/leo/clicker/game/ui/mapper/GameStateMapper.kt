package com.apps.leo.clicker.game.ui.mapper

import com.apps.leo.clicker.R
import com.apps.leo.clicker.game.common.ui.formatAmountOfMoney
import com.apps.leo.clicker.game.domain.model.Upgrade
import com.apps.leo.clicker.game.domain.model.UpgradeType
import com.apps.leo.clicker.game.ui.model.GameUiState
import javax.inject.Inject

class GameStateMapper @Inject constructor() {
    fun mapUpgradeToButtonState(
        upgrade: Upgrade,
        totalBalance: Long
    ): GameUiState.UpgradeButtonState {
        return GameUiState.UpgradeButtonState(
            type = upgrade.type,
            price = upgrade.price,
            priceText = formatAmountOfMoney(
                amount = upgrade.price,
                moneySymbol = "",
                spaceSymbol = ""
            ),
            isAvailable = upgrade.price <= totalBalance,
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
}