package com.apps.leo.clicker.game.domain

import com.apps.leo.clicker.game.domain.model.Upgrade
import com.apps.leo.clicker.game.domain.model.UpgradeType
import javax.inject.Inject

class GetInitialUpgradesUseCase @Inject constructor(
    private val getUpgradePrice: GetUpgradePriceUseCase
) {
    operator fun invoke(): List<Upgrade> {
        return UpgradeType.entries.map { type ->
            Upgrade(
                type = type,
                level = 1,
                price = getUpgradePrice(type, 1)
            )
        }
    }
}