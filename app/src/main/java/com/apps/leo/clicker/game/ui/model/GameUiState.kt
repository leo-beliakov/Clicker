package com.apps.leo.clicker.game.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.apps.leo.clicker.game.domain.model.ExtraClickerInfo
import com.apps.leo.clicker.game.domain.model.UpgradeType
import java.util.UUID

data class GameUiState(
    val levelText: String,
    val levelPercentage: Float,
    val boosts: List<Boost> = emptyList(),
    val statistics: Statistics = Statistics(),
    val extraClickers: List<ExtraClickerInfo> = emptyList(),
    val upgradeButtons: List<UpgradeButtonState> = emptyList()
) {
    data class Boost(
        val id: UUID,
        @DrawableRes val imageResId: Int,
        @DrawableRes val imageActivatedResId: Int,
        val color: Color,
        val status: BoostStatus
    ) {
        sealed class BoostStatus {
            object PermanentlyAvailable : BoostStatus()

            data class TemporarilyAvailable(
                val timeLeftPercentage: Float,
            ) : BoostStatus()

            data class Activated(
                val timeLeftPercentage: Float,
            ) : BoostStatus()
        }
    }

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