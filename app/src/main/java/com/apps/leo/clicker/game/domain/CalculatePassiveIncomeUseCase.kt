package com.apps.leo.clicker.game.domain

import com.apps.leo.clicker.game.ui.model.GameState
import javax.inject.Inject
import kotlin.math.roundToLong

class CalculatePassiveIncomeUseCase @Inject constructor() {
    operator fun invoke(
        passiveIncome: GameState.PassiveIncome //todo passiveIncome is in UI. Consider moving it to domain
    ): Long {
        return passiveIncome.workers.sumOf { worker ->
            (passiveIncome.incomePerWorkder * passiveIncome.speedOfWorkders * worker.level).roundToLong()
        }
    }
}