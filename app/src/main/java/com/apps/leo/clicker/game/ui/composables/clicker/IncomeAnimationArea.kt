package com.apps.leo.clicker.game.ui.composables.clicker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector3D
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import com.apps.leo.clicker.game.ui.model.GameSideEffects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class IncomeAnimatorInfo(
    val id: UUID,
    val initialCoordinates: Offset,
    val animatedParams: Animatable<Float, AnimationVector3D>,
    val incomeText: String
)

private const val ANIMATION_START_VALUE = 0f
private const val ANIMATION_TARGET_VALUE = 1f

private const val ANIMATION_ANGLE_MIN = 70
private const val ANIMATION_ANGLE_MAX = 110
private const val ANIMATION_PROGRESS_ALPHA_THRESHOLD = 800
private const val ANIMATION_DISTANCE = 450f //todo should depend on the available space
private const val ANIMATION_DURATION = 800


@Composable
fun IncomeIdicationArea(
    incomeSideEffects: Flow<GameSideEffects.ShowIncome>,
    modifier: Modifier = Modifier
) {
    val startedAnim = remember { mutableStateListOf<IncomeAnimatorInfo>() }
    val textMeasurer = rememberTextMeasurer()
    val localDensity = LocalDensity.current

    Box(modifier = modifier) {
        startedAnim.forEach { animatorInfo ->
            Text(
                text = animatorInfo.incomeText,
                modifier = Modifier
                    .absoluteOffset(
                        x = with(localDensity) { animatorInfo.initialCoordinates.x.toDp() },
                        y = with(localDensity) { animatorInfo.initialCoordinates.y.toDp() }
                    )
                    .graphicsLayer {
                        val progress = animatorInfo.animatedParams.value
                        val animatedParams =
                            animatorInfo.animatedParams.typeConverter.convertToVector(progress)
                        translationX = animatedParams.v1
                        translationY = -animatedParams.v2
                        alpha = animatedParams.v3
                    }
            )
        }
    }

    LaunchedEffect(Unit) {
        incomeSideEffects.collect { incomeEffect ->
            launch {
                val textMeasureResult = textMeasurer.measure(incomeEffect.incomeText)
                val textCoordinates = incomeEffect.coordinates
                    .minus(
                        Offset(
                            x = textMeasureResult.size.width / 2f,
                            y = textMeasureResult.size.height / 2f
                        )
                    )

                val distance = ANIMATION_DISTANCE
                val angle = Random.nextInt(from = ANIMATION_ANGLE_MIN, until = ANIMATION_ANGLE_MAX)
                val angleRad = (angle / 360f) * 2 * PI
                val endCoordinatesOffsetX = distance * cos(angleRad).toFloat()
                val endCoordinatesOffsetY = distance * sin(angleRad).toFloat()

                val animatorInfo = IncomeAnimatorInfo(
                    id = UUID.randomUUID(),
                    initialCoordinates = textCoordinates,
                    animatedParams = Animatable(
                        initialValue = ANIMATION_START_VALUE,
                        typeConverter = TwoWayConverter(
                            convertToVector = { progress ->
                                AnimationVector3D(
                                    v1 = endCoordinatesOffsetX * progress, //x offset
                                    v2 = endCoordinatesOffsetY * progress, //y offset
                                    v3 = when { //alpha
                                        progress < ANIMATION_PROGRESS_ALPHA_THRESHOLD -> 1f
                                        else -> ANIMATION_TARGET_VALUE - (progress - ANIMATION_PROGRESS_ALPHA_THRESHOLD) / (ANIMATION_TARGET_VALUE - ANIMATION_PROGRESS_ALPHA_THRESHOLD)
                                    }
                                )
                            },
                            convertFromVector = { vector ->
                                vector.v1 / endCoordinatesOffsetX
                            }
                        )
                    ),
                    incomeText = incomeEffect.incomeText,
                )

                startedAnim.add(animatorInfo)
                animatorInfo.animatedParams.animateTo(
                    ANIMATION_TARGET_VALUE,
                    animationSpec = tween(
                        durationMillis = ANIMATION_DURATION,
                        easing = LinearOutSlowInEasing
                    )
                )
                startedAnim.remove(animatorInfo)
            }
        }
    }
}