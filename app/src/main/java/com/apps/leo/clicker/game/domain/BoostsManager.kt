package com.apps.leo.clicker.game.domain

import com.apps.leo.clicker.game.common.collections.add
import com.apps.leo.clicker.game.common.collections.remove
import com.apps.leo.clicker.game.common.collections.swap
import com.apps.leo.clicker.game.domain.model.Boost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val BOOST_ACTIVE_TIME_MS = 15_000L
private const val BOOST_TICK_MS = 50L
private const val BOOST_SPAWN_DELAY = 2_000L

@Singleton
class BoostsManager @Inject constructor(
    levelManager: LevelManager
) {

    private val coroutineScope = CoroutineScope(Job())

    private val _state = MutableStateFlow<List<Boost>>(emptyList())
    val state = _state.asStateFlow()

    init {
        levelManager.state
            .map { it.currentLevel }
            .distinctUntilChanged()
            .onEach { newLevel ->
                when {
//                    newLevel == 2 -> _state.update {
                    newLevel == 1 -> _state.update {
                        val incomeX2Boost = Boost(
                            type = Boost.Type.INCOME_X2,
                            state = Boost.State.Available
                        )

                        listOf(incomeX2Boost)
                    }

                    newLevel == 3 -> _state.update {
                        val autoClickBoost = Boost(
                            type = Boost.Type.AUTO_CLICK,
                            state = Boost.State.Available
                        )

                        it + autoClickBoost
                    }

                    newLevel % 3 == 0 -> {
                        //don't schedule if we have an active x2
                        //otherwise delete the current x2
                        _state.update {
                            val incomeX6Boost = Boost(
                                type = Boost.Type.INCOME_X6,
                                state = Boost.State.Available
                            )

                            it + incomeX6Boost
                        }
                    }
                }
            }.launchIn(coroutineScope)
    }

    fun activateBoost(boostType: Boost.Type) {
        _state.update { boosts ->
            val boost = boosts.firstOrNull { it.type == boostType } ?: return
            val updatedBoost = boost.copy(
                state = Boost.State.Active(
                    timeTotal = BOOST_ACTIVE_TIME_MS,
                    timeLeft = BOOST_ACTIVE_TIME_MS,
                )
            )
            boosts.swap(boost, updatedBoost)
        }

        launchCountDownForBoost(boostType)
    }

    private fun launchCountDownForBoost(boostType: Boost.Type) {
        coroutineScope.launch {
            var boostIsActive = true

            while (boostIsActive) {
                delay(BOOST_TICK_MS)
                boostIsActive = decreaseBoostActiveTime(boostType)
            }

            removeBoost(boostType)
            scheduleBoostAgain(boostType)
        }
    }

    private fun decreaseBoostActiveTime(boostType: Boost.Type): Boolean {
        val boost = _state.value.firstOrNull { it.type == boostType } ?: return false
        val boostState = boost.state as? Boost.State.Active ?: return false
        val timeLeft = boostState.timeLeft - BOOST_TICK_MS
        if (timeLeft <= 0) return false

        _state.update { boosts ->
            val updatedBoost = boost.copy(
                state = boostState.copy(timeLeft = timeLeft)
            )
            boosts.swap(boost, updatedBoost)
        }

        return true
    }

    private fun removeBoost(boostType: Boost.Type) {
        val boost = _state.value.firstOrNull { it.type == boostType } ?: return

        _state.update { boosts ->
            boosts.remove(boost)
        }
    }

    private suspend fun scheduleBoostAgain(boostType: Boost.Type) {
        if (boostType == Boost.Type.INCOME_X6) return
        //shouldn't we schedule x2 if we have x6?

        delay(BOOST_SPAWN_DELAY)

        //chech if we already have a boost of the same type or maybe we already have x6?
        _state.update { boosts ->
            boosts.add(
                Boost(
                    type = boostType,
                    state = Boost.State.Available
                )
            )
        }
    }
}