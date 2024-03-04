package com.apps.leo.clicker.game.ui.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apps.leo.clicker.game.ui.model.GameState
import com.apps.leo.clicker.ui.theme.ClickerTheme

@Composable
fun UpgradeButtonsSection(
    upgrades: List<GameState.UpgradeButtonState>,
    onButtonClicked: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        upgrades.forEach { buttonState ->
            UpgradeButton(
                state = buttonState,
                onButtonClicked = onButtonClicked
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UpgradeButton(
    state: GameState.UpgradeButtonState,
    onButtonClicked: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale = remember { Animatable(initialValue = 1f) }
    val borderStroke = BorderStroke(4.dp, Color.Blue)

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
                    onButtonClicked()
                }
            )
            .border(
                border = borderStroke,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = state.type.iconResId),
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .border(
                    border = borderStroke,
                    shape = RoundedCornerShape(12.dp)
                )
        )
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = stringResource(id = state.type.titleResId),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
            Text(
                text = stringResource(id = state.type.textResId),
                style = TextStyle(
                    color = Color.DarkGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .border(border = borderStroke, shape = RoundedCornerShape(12.dp))
                .background(color = Color.Green, shape = RoundedCornerShape(12.dp))
                .padding( // todo make all buttons of the same size
                    vertical = 8.dp,
                    horizontal = 12.dp
                )
        ) {
            Text(text = state.priceText)
        }
    }
}

@Preview
@Composable
private fun UpgradeButtonPreview() {
    ClickerTheme {
        UpgradeButton(
            state = GameState.UpgradeButtonState(
                type = GameState.UpgradeButtonState.UpgradeType.CLICK_INCOME,
                priceText = "100$",
                isAvailable = true,
                hasFreeBoost = true,
            ),
            onButtonClicked = {}
        )
    }
}