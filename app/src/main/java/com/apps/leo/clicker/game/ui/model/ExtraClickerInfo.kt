package com.apps.leo.clicker.game.ui.model

import androidx.compose.ui.geometry.Offset
import java.util.UUID

data class ExtraClickerInfo(
    val id: UUID,
    val remainedClicks: Int,
    val radius: Float,
    val centerCoordinates: Offset,
    val topLeftCoordinates: Offset
) {
    fun reduceClicks(): ExtraClickerInfo {
        return this.copy(remainedClicks = remainedClicks - 1)
    }
}