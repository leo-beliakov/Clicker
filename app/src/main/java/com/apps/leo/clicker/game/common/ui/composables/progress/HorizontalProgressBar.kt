package com.apps.leo.clicker.game.common.ui.composables.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalProgressBar(
    progress: Float,
    baseColor: Color = Color.LightGray,
    borderColor: Color,
    progressColor: Color,
    borderWidth: Dp = 2.dp,
    modifier: Modifier = Modifier
) {
    val halfBorderWidth = remember(borderWidth) { borderWidth / 2 }

    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .padding(halfBorderWidth)
    ) {
        drawRect(
            color = baseColor,
            style = Fill,
            topLeft = Offset(halfBorderWidth.toPx(), halfBorderWidth.toPx()),
            size = Size(size.width - borderWidth.toPx(), size.height - borderWidth.toPx()),
        )
        drawRect(
            color = progressColor,
            style = Fill,
            topLeft = Offset(halfBorderWidth.toPx(), halfBorderWidth.toPx()),
            size = Size(
                size.width * progress - halfBorderWidth.toPx(),
                size.height - borderWidth.toPx()
            ),
        )
        drawRoundRect(
            color = borderColor,
            style = Stroke(width = borderWidth.toPx()),
            topLeft = Offset(0f, 0f),
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(12.dp.toPx())
        )
    }
}