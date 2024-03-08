package com.apps.leo.clicker.game.ui.model

import androidx.compose.ui.geometry.Offset

sealed interface GameSideEffects {
    data class ShowIncome(
        val coordinates: Offset,
        val incomeText: String,
    ) : GameSideEffects
}