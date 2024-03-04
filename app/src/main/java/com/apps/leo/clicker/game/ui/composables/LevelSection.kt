package com.apps.leo.clicker.game.ui.composables

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun LevelSection(
    levelText: String,
    levelPercentage: Float
) {
    Text(
        text = "$levelText, $levelPercentage"
    )
}
