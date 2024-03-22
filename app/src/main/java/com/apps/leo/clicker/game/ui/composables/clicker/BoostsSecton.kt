package com.apps.leo.clicker.game.ui.composables.clicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.apps.leo.clicker.game.common.ui.composables.clickable.invisibleClickable
import com.apps.leo.clicker.game.common.ui.composables.progress.HorizontalProgressBar
import com.apps.leo.clicker.game.ui.model.BoostUi
import kotlin.math.sqrt


private const val BOOST_SIZE = 80

@Composable
fun BoostsSecton(
    boosts: List<BoostUi>,
    onBoostClicked: (boost: BoostUi) -> Unit,
    modifier: Modifier = Modifier,
    onBoostsPositioned: (bounds: Rect) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.onGloballyPositioned { onBoostsPositioned(it.boundsInParent()) }
    ) {
        boosts.forEach { boost ->
            Boost(
                boost = boost,
                onBoostClicked = onBoostClicked
            )
        }
    }
}

@Composable
fun Boost(
    boost: BoostUi,
    onBoostClicked: (boost: BoostUi) -> Unit
) {
    when (boost.status) {
        BoostUi.BoostStatus.PermanentlyAvailable -> PermanentBoost(
            boost = boost,
            onBoostClicked = onBoostClicked
        )

        is BoostUi.BoostStatus.TemporarilyAvailable -> TemporaryBoost(
            boost = boost,
            onBoostClicked = onBoostClicked
        )

        is BoostUi.BoostStatus.Activated -> ActivatedBoost(
            boost = boost,
            onBoostClicked = onBoostClicked
        )
    }
}

@Composable
fun PermanentBoost(
    boost: BoostUi,
    onBoostClicked: (boost: BoostUi) -> Unit
) {
    Image(
        painter = painterResource(id = boost.imageResId),
        contentDescription = null,
        modifier = Modifier.invisibleClickable { onBoostClicked(boost) }
    )
}

@Composable
fun TemporaryBoost(
    boost: BoostUi,
    onBoostClicked: (boost: BoostUi) -> Unit
) {
    val boostStatus = boost.status as BoostUi.BoostStatus.TemporarilyAvailable

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .invisibleClickable { onBoostClicked(boost) }
    ) {
        Image(
            painter = painterResource(id = boost.imageResId),
            contentDescription = null,
        )
        HorizontalProgressBar(
            progress = boostStatus.timeLeftPercentage,
            borderColor = Color.Black,
            progressColor = boost.color,
            borderWidth = 4.dp,
            modifier = Modifier
                .height(15.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun ActivatedBoost(
    boost: BoostUi,
    onBoostClicked: (boost: BoostUi) -> Unit
) {
    val imageResource = ImageBitmap.imageResource(id = boost.imageActivatedResId)
    val activatedStatus = boost.status as BoostUi.BoostStatus.Activated //todo looks ugly

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .invisibleClickable { onBoostClicked(boost) }
    ) {
        val strokeWidth = 10.dp
        val borderWidth = 3.dp
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
            color = Color.Black,
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
            color = boost.color,
            startAngle = -90f, // Starting from the top (-90 degrees)
            sweepAngle = 360 * activatedStatus.timeLeftPercentage,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(
                cap = StrokeCap.Round,
                width = strokeWidthPx
            )
        )

        // Calculate the top left position to center the image
        val imageTopLeft = IntOffset(
            ((size.width - innerVolume) / 2).toInt(),
            ((size.height - innerVolume) / 2).toInt(),
        )

        // Draw the image centered in the circle
        drawImage(
            image = imageResource,
            dstOffset = imageTopLeft,
            dstSize = IntSize(innerVolume.toInt(), innerVolume.toInt())
        )
    }
}
