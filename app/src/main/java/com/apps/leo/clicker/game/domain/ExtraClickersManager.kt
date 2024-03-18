package com.apps.leo.clicker.game.domain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import com.apps.leo.clicker.game.common.collections.add
import com.apps.leo.clicker.game.common.collections.remove
import com.apps.leo.clicker.game.common.collections.swap
import com.apps.leo.clicker.game.common.random.nextOffset
import com.apps.leo.clicker.game.ui.model.ExtraClickerInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

private const val EXTRA_CLICKER_LIFESPAN = 500L
private const val EXTRA_CLICKER_SPAWN_DELAY = 300L

class ExtraClickersManager @Inject constructor() {

    private val coroutineScope = CoroutineScope(Job())

    private val _state = MutableStateFlow<List<ExtraClickerInfo>>(emptyList())
    val state = _state.asStateFlow()

    var clickerBounds = Rect.Zero
    var clickerAreaSize = IntSize.Zero
    var boostsAreaBounds = Rect.Zero
    var statisticsAreaBounds = Rect.Zero

    init {
        startExtraClickersTimer()
    }


    private fun startExtraClickersTimer() {
        coroutineScope.launch {
            delay(2000)
            while (true) {
                //todo reifine conditions to emit random clickers (start & end of the level)
                val extraClickerInfo = ExtraClickerInfo(
                    id = UUID.randomUUID(),
                    remainedClicks = 1,
                    bounds = generateExtraClickerCoordinates()
                )

                launch {
                    _state.update {
                        it.add(extraClickerInfo)
                    }
                    delay(EXTRA_CLICKER_LIFESPAN)
                    _state.update {
                        it.swap(
                            extraClickerInfo,
                            extraClickerInfo.reduceClicks()
                        )
                    }
                }

                delay(EXTRA_CLICKER_SPAWN_DELAY)
            }
        }
    }

    private fun generateExtraClickerCoordinates(): Rect {
        val smallClickerSize = clickerBounds.width / 3f //todo should be a field?
        val prohibitedAreas = mutableListOf(clickerBounds, statisticsAreaBounds, boostsAreaBounds)
        prohibitedAreas.addAll(_state.value.map { it.bounds })

        while (true) {
            val randomPoint = Random.nextOffset(
                xFrom = 0f,
                xUntil = clickerAreaSize.width - smallClickerSize,
                yFrom = 0f,
                yUntil = clickerAreaSize.height - smallClickerSize,
            )

            val randomClickerBounds = Rect( //todo Utils
                topLeft = randomPoint,
                bottomRight = Offset(
                    x = randomPoint.x + smallClickerSize,
                    y = randomPoint.y + smallClickerSize,
                )
            )

            if (prohibitedAreas.none { it.overlaps(randomClickerBounds) }) {
                return randomClickerBounds
            }
        }
    }

    fun onExtraClickerDisappeared(info: ExtraClickerInfo) {
        _state.update { it.remove(info) }
    }

    fun onExtraClickerClicked(info: ExtraClickerInfo) {
        _state.update { it.swap(info, info.reduceClicks()) }
    }
}