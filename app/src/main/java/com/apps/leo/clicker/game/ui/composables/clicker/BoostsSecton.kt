package com.apps.leo.clicker.game.ui.composables.clicker

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.apps.leo.clicker.game.ui.model.GameUiState

@Composable
fun BoostsSecton(
    boosts: List<GameUiState.Boost>,
    modifier: Modifier = Modifier,
    onBoostClicked: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        //todo we need to show active boosts first
        boosts.forEach { boost ->
            Text(text = boost.text)
        }
    }
}
