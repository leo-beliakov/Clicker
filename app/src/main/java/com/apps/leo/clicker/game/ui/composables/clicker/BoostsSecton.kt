package com.apps.leo.clicker.game.ui.composables.clicker

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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.apps.leo.clicker.game.common.ui.composables.progress.CircleProgressBar
import com.apps.leo.clicker.game.common.ui.composables.progress.HorizontalProgressBar
import com.apps.leo.clicker.game.ui.model.GameUiState


private const val BOOST_SIZE = 80

@Composable
fun BoostsSecton(
    boosts: List<GameUiState.Boost>,
    onBoostClicked: () -> Unit,
    modifier: Modifier = Modifier,
    onBoostsPositioned: (bounds: Rect) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.onGloballyPositioned { onBoostsPositioned(it.boundsInParent()) }
    ) {
        boosts.forEach { boost ->
            Boost(boost = boost)
        }
    }
}

@Composable
fun Boost(boost: GameUiState.Boost) {
    when (boost.status) {
        GameUiState.Boost.BoostStatus.PermanentlyAvailable -> PermanentBoost(boost)
        is GameUiState.Boost.BoostStatus.TemporarilyAvailable -> ActivatedBoost(boost)//TemporaryBoost(boost)
        is GameUiState.Boost.BoostStatus.Activated -> ActivatedBoost(boost)
    }
}

@Composable
fun PermanentBoost(boost: GameUiState.Boost) {
    Image(
        painter = painterResource(id = boost.imageResId),
        contentDescription = null,
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
        Image(
            painter = painterResource(id = boost.imageResId),
            contentDescription = null,
//            modifier = Modifier.width(BOOST_SIZE.dp)
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
fun ActivatedBoost(boost: GameUiState.Boost) {
    CircleProgressBar(
        imageBitmap = ImageBitmap.imageResource(id = boost.imageActivatedResId),
        fillColor = boost.color,
        borderColor = Color.Black,
        strokeWidth = 10.dp,
        borderWidth = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    )
}
