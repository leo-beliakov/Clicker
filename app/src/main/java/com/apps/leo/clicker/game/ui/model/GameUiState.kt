package com.apps.leo.clicker.game.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.apps.leo.clicker.game.domain.model.Upgrade
import com.apps.leo.clicker.game.domain.model.UpgradeType

data class GameState(
    val totalBalance: Long,
    val clickIncome: Long,
    val passiveIncome: Long,
    val currentLevel: Int,
    val levelProgress: Float,
    val upgrades: List<Upgrade>,
)

data class GameUiState(
    val levelText: String,
    val levelPercentage: Float,
    val boosts: List<Boost> = emptyList(),
    val statistics: Statistics = Statistics(),
    val upgradeButtons: List<UpgradeButtonState> = emptyList()
) {
    data class Boost(
        val id: String,
        val text: String
    )

    data class Statistics(
        val total: String = "",
        val passive: String = ""
    )

    data class UpgradeButtonState(
        val type: UpgradeType,
        val price: Long,
        val priceText: String,
        val isAvailable: Boolean,
        val hasFreeBoost: Boolean,
        @StringRes val titleResId: Int,
        @StringRes val textResId: Int,
        @DrawableRes val iconResId: Int
    )
}