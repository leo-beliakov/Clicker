package com.apps.leo.clicker.game.domain

import com.apps.leo.clicker.game.ui.model.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

private const val LEVEL_PROGRESS_DECREASE_TICK = 60L
private const val LEVEL_PROGRESS_DECREASE_PER_TICK = 0.02f
private const val NEW_LEVEL_PROGRESS_EXCEED_THRESHOLD = 0.03f
private const val LEVEL_PROGRESS_PER_CLICK = 0.07f

class LevelManager @Inject constructor() {

    private val coroutineScope = CoroutineScope(Job())

    private val _state = MutableStateFlow(getInitialLevel())
    val state = _state.asStateFlow()

    init {
        startLevelDecreaseTimer()
    }

    fun processClick() {
        val levelState = _state.value
        val levelProgress = if (levelState.isUpgrading) {
            levelState.progress
        } else {
            levelState.progress + LEVEL_PROGRESS_PER_CLICK
        }
        val isLevelCompleted = (levelProgress) >= (1f + NEW_LEVEL_PROGRESS_EXCEED_THRESHOLD)
        val levelProgressNormalized = min(levelProgress, 1f)
        val newLevel =
            if (isLevelCompleted) levelState.currentLevel + 1 else levelState.currentLevel

        _state.update {
            it.copy(
                currentLevel = newLevel,
                progress = levelProgressNormalized,
                isUpgrading = if (isLevelCompleted) true else it.isUpgrading
            )
        }
    }

    fun finishLevelUpgrade() {
        _state.update {
            it.copy(
                progress = 0f,
                isUpgrading = false
            )
        }
    }

    private fun getInitialLevel(): GameState.LevelState {
        return GameState.LevelState(
            progress = 0f,
            currentLevel = 1,
            isUpgrading = false
        )
    }

    private fun startLevelDecreaseTimer() {
        coroutineScope.launch {
            while (true) {
                delay(LEVEL_PROGRESS_DECREASE_TICK)
                if (!_state.value.isUpgrading) {
                    _state.update { level ->
                        level.copy(
                            progress = max(
                                0f,
                                level.progress - LEVEL_PROGRESS_DECREASE_PER_TICK
                            )
                        )
                    }
                }
            }
        }
    }

}