package com.apps.leo.clicker.game.common.ui.composables.text

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun OutlinedText(
    text: String,
    textStyle: TextStyle,
    fillColor: Color = Color.White,
    strokeColor: Color = Color.Black,
    strokeWidth: TextUnit = 1.sp,
    modifier: Modifier = Modifier
) {
    val localDensity = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val measuredText = remember(text) {
        textMeasurer.measure(
            text = text,
            style = textStyle
        )
    }
    val size by remember(text) {
        derivedStateOf {
            with(localDensity) {
                DpSize(measuredText.size.width.toDp(), measuredText.size.height.toDp())
            }
        }
    }

    Spacer(
        modifier = modifier
            .size(size)
            .drawWithCache {
                onDrawBehind {
                    drawText(
                        textLayoutResult = measuredText,
                        color = strokeColor,
                        drawStyle = Stroke(
                            width = strokeWidth.toPx(),
                            cap = StrokeCap.Round,
                            miter = 10f
                        )
                    )
                    drawText(
                        textLayoutResult = measuredText,
                        color = fillColor,
                        drawStyle = Fill
                    )
                }
            }
    )
}