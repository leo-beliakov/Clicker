package com.apps.leo.clicker.game.ui.composables.clicker

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.apps.leo.clicker.game.common.ui.composables.text.OutlinedText
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
        OutlinedText(
            text = statistics.total,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            ),
            fillColor = Color.White,
            strokeColor = Color.Black,
            strokeWidth = 2.sp,
        )
        OutlinedText(
            text = statistics.passive,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            ),
            fillColor = Color.White,
            strokeColor = Color.Black,
            strokeWidth = 2.sp,
        )
    }
}