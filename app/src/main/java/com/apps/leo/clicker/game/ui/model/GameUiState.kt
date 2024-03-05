package com.apps.leo.clicker.game.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.apps.leo.clicker.R

data class GameState(
    val totalBalance: Long,
    val clickIncome: Long,
    val passiveIncome: Long,
    val currentLevel: Int,
    val levelProgress: Float,
)

data class GameUiState(
    val levelText: String,
    val levelPercentage: Float,
    val boosts: List<Boost>,
    val statistics: Statistics,
    val upgradeButtons: List<UpgradeButtonState>
) {
    data class Boost(
        val id: String,
        val text: String
    )

    data class Statistics(
        val total: String,
        val passive: String
    )

    data class UpgradeButtonState(
        val type: UpgradeType,
        val priceText: String,
        val isAvailable: Boolean,
        val hasFreeBoost: Boolean
    ) {

        enum class UpgradeType(
            @StringRes val titleResId: Int,
            @StringRes val textResId: Int,
            @DrawableRes val iconResId: Int,
        ) {
            CLICK_INCOME(
                titleResId = R.string.upgrade_click_income_title,
                textResId = R.string.upgrade_click_income_text,
                iconResId = R.drawable.ic_cursor_upgrade
            ),
            ADD_CURSOR(
                titleResId = R.string.upgrade_add_cursor_title,
                textResId = R.string.upgrade_add_cursor_text,
                iconResId = R.drawable.ic_cursor_upgrade
            ),
            MERGE_CURSORS(
                titleResId = R.string.upgrade_merge_cursors_title,
                textResId = R.string.upgrade_merge_cursors_text,
                iconResId = R.drawable.ic_cursor_upgrade
            ),
            CURSOR_INCOME(
                titleResId = R.string.upgrade_cursor_income_title,
                textResId = R.string.upgrade_cursor_income_text,
                iconResId = R.drawable.ic_cursor_upgrade
            ),
            CURSOR_SPEED(
                titleResId = R.string.upgrade_cursor_speed_title,
                textResId = R.string.upgrade_cursor_speed_text,
                iconResId = R.drawable.ic_cursor_upgrade
            )
        }
    }
}