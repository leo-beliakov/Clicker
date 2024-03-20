package com.apps.leo.clicker.game.domain

import com.apps.leo.clicker.game.domain.model.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoostsManager @Inject constructor(
    levelManager: LevelManager
) {

    private val coroutineScope = CoroutineScope(Job())

    private val _state = MutableStateFlow<GameState.BoostsState>(GameState.BoostsState())
    val state = _state.asStateFlow()

    init {
        levelManager.state
            .map { it.currentLevel }
            .distinctUntilChanged()
            .onEach { newLevel ->
                when {
                    newLevel == 2 -> _state.update {
                        it.copy(
                            availableBoosts = listOf(
                                GameState.BoostsState.Boost(
                                    type = GameState.BoostsState.BoostType.INCOME_X2,
                                    state = GameState.BoostsState.BoostState.Available
                                )
                            )
                        )
                    }

                    newLevel == 3 -> _state.update {
                        it.copy(
                            availableBoosts = it.availableBoosts + listOf(
                                GameState.BoostsState.Boost(
                                    type = GameState.BoostsState.BoostType.AUTO_CLICK,
                                    state = GameState.BoostsState.BoostState.Available
                                )
                            )
                        )
                    }
                }
            }.launchIn(coroutineScope)
    }

    fun activateBoost(boostType: GameState.BoostsState.BoostType) {
        //change boost's state
        //launch a countdown timer
        //when the timer ends, delete the boost
        //?add the boost again? or only for next level? or after a timeout?
    }
}