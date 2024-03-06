package com.apps.leo.clicker.game.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apps.leo.clicker.R
import com.apps.leo.clicker.game.domain.model.UpgradeType
import com.apps.leo.clicker.game.ui.model.GameUiState
import com.apps.leo.clicker.ui.theme.ClickerTheme

@Composable
fun UpgradeButtonsSection(
    upgrades: List<GameUiState.UpgradeButtonState>,
    onButtonClicked: (GameUiState.UpgradeButtonState) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val priceWidthLimit = remember {
        textMeasurer.measure(
            text = "999.9K$",
            style = TextStyle(
                color = Color.Black,
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
    ) {
        ButtonContent(
            state = state,
            priceButtonWidth = priceButtonWidth,
            onButtonClicked = onButtonClicked
        )
        AnimatedVisibility(
            visible = !state.isAvailable,
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
    priceButtonWidth: Dp,
    onButtonClicked: (GameUiState.UpgradeButtonState) -> Unit
) {
    val borderStroke = BorderStroke(4.dp, Color.Blue)
    var isPressed by remember { mutableStateOf(false) }
    val scale = remember { Animatable(initialValue = 1f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(0.8f)
            scale.animateTo(1f)
            isPressed = false
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale.value)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = state.isAvailable,
                role = Role.Button,
                onClick = {
                    isPressed = true
                    onButtonClicked(state)
                }
            )
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
                    color = if (state.isAvailable) Color.DarkGray else Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .border(border = borderStroke, shape = RoundedCornerShape(12.dp))
                .background(color = Color.Green, shape = RoundedCornerShape(12.dp))
                .padding(vertical = 8.dp, horizontal = 12.dp)
                .width(priceButtonWidth)
        ) {
            Text(
                text = state.priceText,
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }
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