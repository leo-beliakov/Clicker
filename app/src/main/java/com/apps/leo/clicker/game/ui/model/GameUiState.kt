package com.apps.leo.clicker.game.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.apps.leo.clicker.game.domain.model.ExtraClickerInfo
import com.apps.leo.clicker.game.domain.model.UpgradeType

data class GameUiState(
    val levelText: String,
    val levelPercentage: Float,
    val boosts: List<BoostUi> = emptyList(),
    val statistics: Statistics = Statistics(),
    val extraClickers: List<ExtraClickerInfo> = emptyList(),
    val upgradeButtons: List<UpgradeButtonState> = emptyList(),
    val dialogState: DialogState? = null
) {
    data class DialogState(
        val boost: BoostUi,
        val text: String,
    )

    data class Statistics(
        val total: String = "",
        val passive: String = ""
    )

    data class UpgradeButtonState(
        val type: UpgradeType,
        val price: Long,
        val priceText: String,
        val isMax: Boolean,
        val isAvailable: Boolean,
        val hasFreeBoost: Boolean,
        @StringRes val titleResId: Int,
        @StringRes val textResId: Int,
        @DrawableRes val iconResId: Int
    )
}