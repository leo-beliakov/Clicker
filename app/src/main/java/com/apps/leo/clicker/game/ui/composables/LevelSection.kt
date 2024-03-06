package com.apps.leo.clicker.game.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apps.leo.clicker.ui.theme.ClickerTheme

private const val LEVEL_WIDTH_FRACTION = 0.6f

@Composable
fun LevelSection(
    levelText: String,
    levelPercentage: Float
) {
    val border = 4.dp
    val textMeasurer = rememberTextMeasurer()
    val animatedColor by animateColorAsState(
        targetValue = lerp(Color.Red, Color.Green, levelPercentage)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
                .drawBehind {
                    val borderWidth = border.toPx()
                    val halfBorderWidth = borderWidth / 2f

                    drawRect(
                        color = Color.LightGray,
                        style = Fill,
                        topLeft = Offset(halfBorderWidth, halfBorderWidth),
                        size = Size(size.width - borderWidth, size.height - borderWidth),
                    )
                    if (levelPercentage > 0f) {
                        drawRect(
                            color = animatedColor,
                            style = Fill,
                            topLeft = Offset(halfBorderWidth, halfBorderWidth),
                            size = Size(
                                size.width * levelPercentage - borderWidth,
                                size.height - borderWidth
                            ),
                        )
                    }
                    drawRoundRect(
                        color = Color.Blue,
                        style = Stroke(width = 4.dp.toPx()),
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height),
                        cornerRadius = CornerRadius(12.dp.toPx())
                    )
                }
                .drawWithCache {
                    val measuredText = textMeasurer.measure(
                        text = levelText,
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    )

                    onDrawBehind {
                        drawText(
                            textLayoutResult = measuredText,
                            topLeft = Offset(
                                (size.width - measuredText.size.width) / 2f,
                                (size.height - measuredText.size.height) / 2f
                            )
                        )
                    }
                }
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
        )
    }
}
