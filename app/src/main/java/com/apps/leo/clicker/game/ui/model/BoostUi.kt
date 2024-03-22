package com.apps.leo.clicker.game.ui.model

import androidx.annotation.DrawableRes
import com.apps.leo.clicker.game.domain.model.Boost

data class BoostUi(
    val type: Boost.Type,
    @DrawableRes val imageResId: Int,
    @DrawableRes val imageActivatedResId: Int,
    val color: androidx.compose.ui.graphics.Color,
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