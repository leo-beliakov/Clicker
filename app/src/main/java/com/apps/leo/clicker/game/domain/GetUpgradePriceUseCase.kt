package com.apps.leo.clicker.game.domain

import com.apps.leo.clicker.game.domain.model.UpgradeType
import javax.inject.Inject
import kotlin.math.roundToLong

class GetUpgradePriceUseCase @Inject constructor(
    private val getBasePrice: GetBasePricePerMapUseCase
) {
    operator fun invoke(type: UpgradeType, level: Int): Long {
        val basePrice = getBasePrice()
        return (basePrice * type.priceCoefficient * level).roundToLong() //todo should be more sophisticated
    }
}