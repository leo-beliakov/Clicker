package com.apps.leo.clicker.game.ui.model

import androidx.compose.ui.geometry.Rect
import java.util.UUID

data class ExtraClickerInfo(
    val id: UUID,
    val remainedClicks: Int,
    val bounds: Rect
) {
    fun reduceClicks(): ExtraClickerInfo {
        return this.copy(remainedClicks = remainedClicks - 1)
    }
}