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
    val boosts: List<Boost>,
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

    data class LevelState(
        val currentLevel: Int,
        val progress: Float,
        val isUpgrading: Boolean
    )
}