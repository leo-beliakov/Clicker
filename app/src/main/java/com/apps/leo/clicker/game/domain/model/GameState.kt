package com.apps.leo.clicker.game.domain.model

const val START_LEVEL = 1

data class GameState(
    val totalBalance: Long,
    val clickIncome: Long,
    val passiveIncome: PassiveIncome,
    val extraClickerIncome: Long,
    val level: LevelState,
    val upgrades: List<Upgrade>,
    val extraClickers: List<ExtraClickerInfo>,
    val boosts: BoostsState,
) {
    data class PassiveIncome(
        val workers: List<Worker> = emptyList(),
        val speedOfWorkders: Float = 0f,
        val incomePerWorkder: Long = 0,
    ) {
        data class Worker(
            val level: Int = START_LEVEL
        )
    }

    data class BoostsState(
        val availableBoosts: List<Boost> = emptyList(),
        val activeBoosts: List<Boost> = emptyList(),
    ) {
        data class Boost(
            val type: BoostType,
            val state: BoostState
        )

        sealed interface BoostState {
            object Available : BoostState
            data class Active(
                val timeTotal: Long,
                val timeLeft: Long
            ) : BoostState
        }

        enum class BoostType {
            INCOME_X2,
            INCOME_X6,
            AUTO_CLICK
        }
    }

    data class LevelState(
        val currentLevel: Int,
        val progress: Float,
        val isUpgrading: Boolean
    )
}