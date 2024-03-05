package com.apps.leo.clicker.game.ui.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatSpringSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import com.apps.leo.clicker.game.ui.model.GameUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun ClickerSection(
    boosts: List<GameUiState.Boost>,
    statistics: GameUiState.Statistics,
    onBoostClicked: () -> Unit,
    onClickerClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f / 0.8f)
    ) {
        BoostsSecton(
            boosts = boosts,
            modifier = Modifier.align(Alignment.TopStart),
            onBoostClicked = onBoostClicked
        )
        StatisticsSecton(
            statistics = statistics,
            modifier = Modifier.align(Alignment.TopEnd)
        )
        Clicker(
            onClickerClicked = onClickerClicked,
            modifier = Modifier.align(
                BiasAlignment(0f, 0.5f)
            )
        )
    }
}

@Composable
fun Clicker(
    onClickerClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(initialValue = 1f) }

    val animationSpec = FloatSpringSpec(0.8f, 2000f) //todo constants + expiriment
    val scope = rememberCoroutineScope()
    var job: Job? = null

    Canvas(
        modifier = modifier
            .fillMaxWidth(0.6f)
            .aspectRatio(1f)
            .scale(scale.value)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = {
                    job?.cancel()
                    job = scope.launch {
                        scale.animateTo(0.7f, animationSpec) //todo constant
                        scale.animateTo(1f, animationSpec)
                    }
                    onClickerClicked()
                }
            )
    ) {
        drawCircle(
            color = Color.Red
        )
    }
}

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
