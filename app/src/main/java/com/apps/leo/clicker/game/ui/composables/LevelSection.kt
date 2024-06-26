package com.apps.leo.clicker.game.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apps.leo.clicker.game.common.ui.composables.progress.HorizontalProgressBar
import com.apps.leo.clicker.game.common.ui.composables.text.OutlinedText
import com.apps.leo.clicker.ui.theme.ClickerTheme
import kotlinx.coroutines.launch

private const val LEVEL_WIDTH_FRACTION = 0.6f

@Composable
fun LevelSection(
    levelText: String,
    levelPercentage: Float,
    onLevelUpgradeAnimationFinished: () -> Unit
) {
    val tweenSpec = tween<Float>(durationMillis = 250, easing = LinearEasing)
    val animatedScale = remember {
        Animatable(initialValue = 1f)
    }
    val animatedPercentage = remember {
        Animatable(initialValue = 0f)
    }
    val animatedColor by animateColorAsState(
        targetValue = lerp(Color.Red, Color.Green, animatedPercentage.value)
    )

    LaunchedEffect(levelPercentage) {
        if (levelPercentage == 1f) {
            launch {
                repeat(5) {
                    animatedScale.animateTo(1.1f, tweenSpec)
                    animatedScale.animateTo(1f, tweenSpec)
                }
            }
            launch {
                animatedPercentage.animateTo(1f)
                animatedPercentage.animateTo(0f, tween(2500))
                onLevelUpgradeAnimationFinished()
            }
        } else {
            animatedPercentage.animateTo(levelPercentage)
        }
    }

    LevelSection(
        levelText = levelText,
        levelPercentage = animatedPercentage.value,
        levelColor = animatedColor,
        scale = animatedScale.value
    )
}

@Composable
private fun LevelSection(
    levelText: String,
    levelPercentage: Float,
    levelColor: Color,
    scale: Float
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .scale(scale)
    ) {
        HorizontalProgressBar(
            progress = levelPercentage,
            borderColor = Color.Blue,
            progressColor = levelColor,
            borderWidth = 4.dp,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
        )
        OutlinedText(
            text = levelText,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            ),
            fillColor = Color.White,
            strokeColor = Color.Black,
            strokeWidth = 4.sp,
        )
    }
}

@Preview
@Composable
private fun LevelSectionStartPreview() {
    ClickerTheme {
        LevelSection(
            levelText = "Level 1",
            levelPercentage = 0.05f,
            levelColor = Color.Red,
            scale = 1f
        )
    }
}

@Preview
@Composable
private fun LevelSectionMiddlePreview() {
    ClickerTheme {
        LevelSection(
            levelText = "Level 1",
            levelPercentage = 0.5f,
            levelColor = Color.Yellow,
            scale = 1f
        )
    }
}

@Preview
@Composable
private fun LevelSectionFullPreview() {
    ClickerTheme {
        LevelSection(
            levelText = "Level 1",
            levelPercentage = 1f,
            levelColor = Color.Green,
            scale = 1f
        )
    }
}
