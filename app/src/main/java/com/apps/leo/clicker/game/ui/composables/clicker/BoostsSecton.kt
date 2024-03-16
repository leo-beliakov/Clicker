package com.apps.leo.clicker.game.ui.composables.clicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apps.leo.clicker.game.common.ui.composables.text.OutlinedText
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
            Boost(boost = boost)
        }
    }
}

@Composable
fun Boost(boost: GameUiState.Boost) {
    when (boost.status) {
        GameUiState.Boost.BoostStatus.PermanentlyAvailable -> PermanentBoost(boost)
        is GameUiState.Boost.BoostStatus.TemporarilyAvailable -> TemporaryBoost(boost)
        is GameUiState.Boost.BoostStatus.Activated -> ActivatedBoost(boost)
    }
}

@Composable
fun PermanentBoost(boost: GameUiState.Boost) {
    OutlinedText(
        text = stringResource(id = boost.textResId),
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        ),
        fillColor = Color.White,
        strokeColor = Color.Black,
        strokeWidth = 2.sp,
    )
}

@Composable
fun TemporaryBoost(
    boost: GameUiState.Boost
) {
    val boostStatus = boost.status as GameUiState.Boost.BoostStatus.TemporarilyAvailable

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(IntrinsicSize.Min)
    ) {
        OutlinedText(
            text = stringResource(id = boost.textResId),
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            fillColor = boost.color,
            strokeColor = Color.Black,
            strokeWidth = 2.sp,
        )
        Canvas(
            modifier = Modifier
                .height(15.dp)
                .fillMaxWidth()
        ) {
            drawRect(
                color = Color.LightGray,
                style = Fill,
                topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
                size = Size(size.width - 4.dp.toPx(), size.height - 4.dp.toPx()),
            )
            drawRect(
                color = boost.color,
                style = Fill,
                topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
                size = Size(
                    size.width * boostStatus.timeLeftPercentage - 2.dp.toPx(),
                    size.height - 4.dp.toPx()
                ),
            )
            drawRoundRect(
                color = Color.Black,
                style = Stroke(width = 4.dp.toPx()),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(12.dp.toPx())
            )
        }
    }
}

@Composable
fun ActivatedBoost(boost: GameUiState.Boost) {

}
