package com.apps.leo.clicker.game.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.apps.leo.clicker.game.ui.model.GameState

@Composable
fun ClickerSection(
    boosts: List<GameState.Boost>,
    statistics: GameState.Statistics,
    onBoostClicked: () -> Unit,
    onClickerClicked: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        BoostsSecton(
            boosts = boosts,
            modifier = Modifier.align(Alignment.TopStart)
        )
        StatisticsSecton(
            statistics = statistics,
            modifier = Modifier.align(Alignment.TopEnd)
        )
        Clicker(
            onClickerClicked = onClickerClicked,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun Clicker(
    onClickerClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        drawCircle(
            color = Color.Red,
//            radius =
        )
    }
}

@Composable
fun StatisticsSecton(
    statistics: GameState.Statistics,
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

@Composable
fun BoostsSecton(
    boosts: List<GameState.Boost>,
    modifier: Modifier = Modifier
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
