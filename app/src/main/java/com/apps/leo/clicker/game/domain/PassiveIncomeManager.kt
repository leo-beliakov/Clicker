package com.apps.leo.clicker.game.domain

import com.apps.leo.clicker.game.common.collections.add
import com.apps.leo.clicker.game.ui.model.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PASSIVE_INCOME_TICK = 1000L
const val PASSIVE_INCOME_WORKERS_REQUIRED_FOR_UPGRADE = 3

class PassiveIncomeManager @Inject constructor() {

    private val coroutineScope = CoroutineScope(Job())

    private val _state = MutableStateFlow(getInitialState())
    val state = _state.asStateFlow()

    init {
        startPassiveIncomeTimer()
    }

    private fun startPassiveIncomeTimer() {
        coroutineScope.launch {
            while (true) {
                delay(PASSIVE_INCOME_TICK)
                _state.update {
                    it.copy(
                        //todo
                        //totalBalance = gameState.totalBalance + calculatePassiveIncome(gameState.passiveIncome)
                    )
                }
            }
        }
    }

    private fun getInitialState(): GameState.PassiveIncome {
        return GameState.PassiveIncome(
            workers = emptyList(),
            speedOfWorkders = 1f, //todo constant
            incomePerWorkder = 1000, //todo should be specific per map
        )
    }

    fun addWorker() {
        _state.update {
            it.copy(
                workers = it.workers
                    .add(GameState.PassiveIncome.Worker())
                    .sortedBy { it.level }
            )
        }
    }

    fun mergeWorkers() {
        _state.update {
            val workers = it.workers
            val updatedWorkersList = workers.toMutableList()
            val workersLevels = workers.map { it.level }.toSet()

            for (level in workersLevels) {
                if (workers.count { it.level == level } >= PASSIVE_INCOME_WORKERS_REQUIRED_FOR_UPGRADE) {
                    repeat(PASSIVE_INCOME_WORKERS_REQUIRED_FOR_UPGRADE) {
                        val workerToRemove = updatedWorkersList.first { it.level == level }
                        updatedWorkersList.remove(workerToRemove)
                    }
                    updatedWorkersList.add(GameState.PassiveIncome.Worker(level = level + 1))
                    updatedWorkersList.sortBy { it.level }
                    break
                }
            }

            it.copy(
                workers = updatedWorkersList.toList()
            )
        }
    }

    fun upgradeIncome() {
        _state.update {
            it.copy(
                incomePerWorkder = (it.incomePerWorkder * 1.1).toLong()
            )
        }
    }

    fun upgradeSpeed() {
        _state.update {
            it.copy(
                speedOfWorkders = it.speedOfWorkders * 1.1f
            )
        }
    }
}