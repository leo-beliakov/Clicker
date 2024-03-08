package com.apps.leo.clicker.game.ui.composables.clicker

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.apps.leo.clicker.game.ui.model.GameUiState

@Composable
fun StatisticsSecton(
    statistics: GameUiState.Statistics,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
    ) {
        Text(text = statistics.total)
        Text(text = statistics.passive)
    }
}