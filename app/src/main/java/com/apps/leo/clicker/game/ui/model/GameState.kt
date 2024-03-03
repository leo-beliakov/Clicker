package com.apps.leo.clicker.game.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.apps.leo.clicker.R

data class GameState(
    val levelText: String,
    val levelPercentage: Float,
    val boosters: List<Booster>,
    val statistics: Statistics,
    val upgradeButtons: List<UpgradeButtonState>
) {
    data class Booster(
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
                iconResId = 0
            ),
            ADD_CURSOR(
                titleResId = R.string.upgrade_add_cursor_title,
                textResId = R.string.upgrade_add_cursor_text,
                iconResId = 0
            ),
            MERGE_CURSORS(
                titleResId = R.string.upgrade_merge_cursors_title,
                textResId = R.string.upgrade_merge_cursors_text,
                iconResId = 0
            ),
            CURSOR_INCOME(
                titleResId = R.string.upgrade_cursor_income_title,
                textResId = R.string.upgrade_cursor_income_text,
                iconResId = 0
            ),
            CURSOR_SPEED(
                titleResId = R.string.upgrade_cursor_speed_title,
                textResId = R.string.upgrade_cursor_speed_text,
                iconResId = 0
            )
        }
    }
}