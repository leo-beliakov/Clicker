package com.apps.leo.clicker.game.domain.model

enum class UpgradeType(
    val priceCoefficient: Float
) {
    CLICK_INCOME(0.75f),
    ADD_CURSOR(1f),
    MERGE_CURSORS(1.5f),
    CURSOR_INCOME(1.5f),
    CURSOR_SPEED(1.75f)
}