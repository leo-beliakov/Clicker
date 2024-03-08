package com.apps.leo.clicker.game.ui.composables.clicker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.TwoWayConverter
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
    val animatedOffset: Animatable<Float, AnimationVector2D>,
    val incomeText: String
)

@Composable
fun IncomeIdicationArea(
    incomeSideEffects: Flow<GameSideEffects.ShowIncome>,
    modifier: Modifier = Modifier
) {
    val startedAnim = remember { mutableStateListOf<IncomeAnimatorInfo>() }
    val textMeasurer = rememberTextMeasurer()
    val localDensity = LocalDensity.current

    //todo alpha and timt change at the end
    //todo set angle
    //todo set random start position
    //todo think about the animation speed / type / time

    Box(modifier = modifier) {
        startedAnim.forEach { animatorInfo ->
            val measureResult = textMeasurer.measure(animatorInfo.incomeText)

            Text(
                text = animatorInfo.incomeText,
                modifier = Modifier
                    .absoluteOffset(
                        x = with(localDensity) { animatorInfo.initialCoordinates.x.toDp() },
                        y = with(localDensity) { animatorInfo.initialCoordinates.y.toDp() }
                    )
                    .graphicsLayer {
                        val progress = animatorInfo.animatedOffset.value
                        val offset =
                            animatorInfo.animatedOffset.typeConverter.convertToVector(progress)
                        translationX = offset.v1
                        translationY = -offset.v2
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

                val distance = 400f //todo should depend on the available space
                val angle = Random.nextInt(from = 70, until = 110)
                val angleRad = (angle / 360f) * 2 * PI
                val endCoordinatesOffsetX = distance * cos(angleRad).toFloat()
                val endCoordinatesOffsetY = distance * sin(angleRad).toFloat()

                val animatorInfo = IncomeAnimatorInfo(
                    id = UUID.randomUUID(),
                    initialCoordinates = textCoordinates,
                    animatedOffset = Animatable(
                        initialValue = 0f,
                        typeConverter = TwoWayConverter(
                            convertToVector = { progress ->
                                AnimationVector2D(
                                    endCoordinatesOffsetX * progress,
                                    endCoordinatesOffsetY * progress
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
                animatorInfo.animatedOffset.animateTo(1f)
                startedAnim.remove(animatorInfo)
            }
        }
    }
}