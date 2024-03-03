package com.apps.leo.clicker.game.domain

import com.apps.leo.clicker.game.domain.model.Upgrade
import com.apps.leo.clicker.game.domain.model.UpgradeType
import javax.inject.Inject
import kotlin.math.roundToInt

class GetInitialUpgradesUseCase @Inject constructor() {

    operator fun invoke(): List<Upgrade> {
        val standardPrice = 1000 // should depend on the current Map

        return UpgradeType.entries.map { type ->
            Upgrade(
                type = type,
                price = (type.priceCoefficient * standardPrice).roundToInt()
            )
        }
    }
}