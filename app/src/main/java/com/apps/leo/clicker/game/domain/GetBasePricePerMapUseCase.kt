package com.apps.leo.clicker.game.domain

import javax.inject.Inject

class GetBasePricePerMapUseCase @Inject constructor() {
    operator fun invoke(): Long {
        val standardPrice = 1000L // should depend on the current Map
        return standardPrice
    }
}