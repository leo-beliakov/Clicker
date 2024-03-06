package com.apps.leo.clicker.game.domain.model

data class Upgrade(
    val type: UpgradeType,
    val level: Int = 1,
    val price: Long
)