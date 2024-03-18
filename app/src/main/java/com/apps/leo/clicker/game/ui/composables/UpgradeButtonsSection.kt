package com.apps.leo.clicker.game.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatSpringSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apps.leo.clicker.R
import com.apps.leo.clicker.game.common.ui.composables.clickable.invisibleClickable
import com.apps.leo.clicker.game.common.ui.composables.text.OutlinedText
import com.apps.leo.clicker.game.domain.model.UpgradeType
import com.apps.leo.clicker.game.ui.model.GameUiState
import com.apps.leo.clicker.ui.theme.ClickerTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun UpgradeButtonsSection(
    upgrades: List<GameUiState.UpgradeButtonState>,
    onButtonClicked: (GameUiState.UpgradeButtonState) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val priceWidthLimit = remember {
        textMeasurer.measure(
            text = "999.99K",
            style = TextStyle(
                //todo should be same for the button as well
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        ).size.width
    }
    val priceWidthLimitDp = with(LocalDensity.current) { priceWidthLimit.toDp() }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        upgrades.forEach { buttonState ->
            UpgradeButton(
                state = buttonState,
                priceButtonWidth = priceWidthLimitDp,
                onButtonClicked = onButtonClicked
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UpgradeButton(
    state: GameUiState.UpgradeButtonState,
    priceButtonWidth: Dp,
    onButtonClicked: (GameUiState.UpgradeButtonState) -> Unit
) {
    val scale = remember { Animatable(initialValue = 1f) }

    val animationSpec = FloatSpringSpec(0.8f, 2000f) //todo constants + expiriment
    val scope = rememberCoroutineScope()
    var job: Job? = null

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .scale(scale.value)
            .invisibleClickable(
                enabled = state.isAvailable && !state.isMax,
                onClick = {
                    job?.cancel()
                    job = scope.launch {
                        scale.animateTo(0.8f, animationSpec) //todo constant
                        scale.animateTo(1f, animationSpec)
                    }
                    onButtonClicked(state)
                }
            )
    ) {
        ButtonContent(
            state = state,
            priceButtonWidth = priceButtonWidth,
        )
        AnimatedVisibility(
            visible = !state.isAvailable && !state.isMax,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            ButtonInactiveIndicator()
        }
    }
}

@Composable
private fun ButtonInactiveIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.3f)
            .background(
                color = Color.Black,
                shape = RoundedCornerShape(12.dp)
            )
    )
}

@Composable
private fun ButtonContent(
    state: GameUiState.UpgradeButtonState,
    priceButtonWidth: Dp
) {
    val borderStroke = BorderStroke(4.dp, Color.Blue)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = borderStroke,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = state.iconResId),
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .border(
                    border = borderStroke,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
        )
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = stringResource(id = state.titleResId),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
            Text(
                text = stringResource(id = state.textResId),
                style = TextStyle(
                    color = if (state.isAvailable || state.isMax) Color.DarkGray else Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            )
        }
        PriceButton(
            isMax = state.isMax,
            priceText = state.priceText,
            priceButtonWidth = priceButtonWidth
        )
    }
}

@Composable
private fun RowScope.PriceButton(
    isMax: Boolean,
    priceText: String,
    priceButtonWidth: Dp
) {
    val backgroundColor = if (isMax) Color.Gray else Color.Green
    val buttonText = if (isMax) stringResource(id = R.string.upgrade_max) else priceText

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .border(border = BorderStroke(4.dp, Color.Blue), shape = RoundedCornerShape(12.dp))
            .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(8.dp)
            .width(priceButtonWidth)
    ) {
        OutlinedText(
            text = buttonText,
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            ),
            fillColor = Color.White,
            strokeColor = Color.DarkGray,
            strokeWidth = 3.sp,
        )
    }
}

@Preview
@Composable
private fun UpgradeButtonPreview() {
    ClickerTheme {
        UpgradeButton(
            state = GameUiState.UpgradeButtonState(
                type = UpgradeType.CLICK_INCOME,
                price = 100L,
                priceText = "100$",
                isMax = false,
                isAvailable = true,
                hasFreeBoost = true,
                titleResId = R.string.upgrade_click_income_title,
                textResId = R.string.upgrade_click_income_text,
                iconResId = R.drawable.ic_cursor_upgrade,
            ),
            priceButtonWidth = 50.dp
        ) {}
    }
}