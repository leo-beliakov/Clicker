package com.apps.leo.clicker.game.common.ui.composables.progress

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.sqrt

@Composable
fun CircleProgressBar(
    imageBitmap: ImageBitmap,
    fillColor: Color,
    borderColor: Color = Color.Black,
    strokeWidth: Dp,
    borderWidth: Dp,
    modifier: Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidthPx = strokeWidth.toPx()
        val strokePaddingPx = borderWidth.toPx()
        val outerStrokeWidth = strokeWidthPx + strokePaddingPx * 2

        val adjustedDiameter = size.minDimension - outerStrokeWidth
        val innerDiameter = adjustedDiameter - outerStrokeWidth
        val innerRadius = innerDiameter / 2
        val innerVolume = innerRadius * sqrt(2f)
        val radius = adjustedDiameter / 2
        val topLeft = Offset(outerStrokeWidth / 2, outerStrokeWidth / 2)
        val arcSize = Size(radius * 2, radius * 2)

        // Draw the outer stroke arc
        drawArc(
            color = borderColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = outerStrokeWidth)
        )

        // Draw the full circle (background) inside the black stroke
        drawArc(
            color = Color.LightGray,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidthPx)
        )

        // Draw the progress arc inside the black stroke
        drawArc(
            color = fillColor,
            startAngle = -90f, // Starting from the top (-90 degrees)
            sweepAngle = 360 * 0.4f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidthPx)
        )

        // Calculate the top left position to center the image
        val imageTopLeft = IntOffset(
            ((size.width - innerVolume) / 2).toInt(),
            ((size.height - innerVolume) / 2).toInt(),
        )

        // Draw the image centered in the circle
        drawImage(
            image = imageBitmap,
            dstOffset = imageTopLeft,
            dstSize = IntSize(innerVolume.toInt(), innerVolume.toInt())
        )
    }
}