package com.apps.leo.clicker.game.ui.composables.clicker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatSpringSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.Role
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun Clicker(
    onClickerPositioned: (bounds: Rect) -> Unit,
    onClickerClicked: () -> Unit,
    color: Color = Color.Red,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(initialValue = 1f) }

    val animationSpec = FloatSpringSpec(0.8f, 2000f) //todo constants + expiriment
    val scope = rememberCoroutineScope()
    var job: Job? = null

    Canvas(
        modifier = modifier
            .onGloballyPositioned { onClickerPositioned(it.boundsInParent()) }
            .scale(scale.value)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = {
                    //launch animation of size
                    job?.cancel()
                    job = scope.launch {
                        scale.animateTo(0.7f, animationSpec) //todo constant
                        scale.animateTo(1f, animationSpec)
                    }

                    onClickerClicked()
                }
            )
    ) {
        drawCircle(
            color = color
        )
    }
}