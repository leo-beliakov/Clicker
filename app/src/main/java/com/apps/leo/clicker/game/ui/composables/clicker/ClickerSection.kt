package com.apps.leo.clicker.game.ui.composables.clicker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import com.apps.leo.clicker.game.ui.model.GameSideEffects
import com.apps.leo.clicker.game.ui.model.GameUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun ClickerSection(
    boosts: List<GameUiState.Boost>,
    statistics: GameUiState.Statistics,
    onBoostClicked: () -> Unit,
    onClickerClicked: () -> Unit,
    onClickerPositioned: (centerCoordinates: Offset) -> Unit,
    incomeSideEffects: Flow<GameSideEffects.ShowIncome>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f / 0.8f)
    ) {
        BoostsSecton(
            boosts = boosts,
            modifier = Modifier.align(Alignment.TopStart),
            onBoostClicked = onBoostClicked
        )
        StatisticsSecton(
            statistics = statistics,
            modifier = Modifier.align(Alignment.TopEnd)
        )
        Clicker(
            onClickerClicked = onClickerClicked,
            onClickerPositioned = onClickerPositioned,
            modifier = Modifier.align(
                BiasAlignment(0f, 0.5f)
            )
        )
        IncomeIdicationArea(
            incomeSideEffects = incomeSideEffects,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun IncomeIdicationArea(
    incomeSideEffects: Flow<GameSideEffects.ShowIncome>,
    modifier: Modifier = Modifier
) {
    val startedAnim = remember { mutableStateListOf<IncomeAnimatorInfo>() }
    val localDensity = LocalDensity.current

    //todo take into account the text size
    //todo alpha and timt change at the end
    //todo set angle
    //todo set random start position
    //todo think about the animation speed / type / time

    startedAnim.forEach { animatorInfo ->
        Text(
            text = animatorInfo.incomeText,
            modifier = Modifier
                .absoluteOffset(
                    x = with(localDensity) { animatorInfo.initialCoordinates.x.toDp() },
                    y = with(localDensity) { animatorInfo.initialCoordinates.y.toDp() }
                )
                .graphicsLayer {
                    val progress = animatorInfo.animatedOffset.value
                    val offset = animatorInfo.animatedOffset.typeConverter.convertToVector(progress)
                    translationX = offset.v1
                    translationY = -offset.v2
                }
        )
    }

    LaunchedEffect(Unit) {
        incomeSideEffects.collect { incomeEffect ->
            launch {
                val animatorInfo = IncomeAnimatorInfo(
                    id = UUID.randomUUID(),
                    initialCoordinates = incomeEffect.coordinates,
                    animatedOffset = Animatable(
                        initialValue = 0f,
                        typeConverter = TwoWayConverter(
                            convertToVector = { progress ->
                                AnimationVector2D(
                                    0f,
                                    progress * 200f
                                ) //todo
                            },
                            convertFromVector = { vector ->
                                vector.v2 / 200f // todo
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

data class IncomeAnimatorInfo(
    val id: UUID,
    val initialCoordinates: Offset,
    val animatedOffset: Animatable<Float, AnimationVector2D>,
    val incomeText: String
)
