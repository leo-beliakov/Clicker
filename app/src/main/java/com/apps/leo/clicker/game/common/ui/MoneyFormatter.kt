package com.apps.leo.clicker.game.common.ui

import java.text.DecimalFormat

fun formatAmountOfMoney(amount: Long): String {
    val numberLength = amount.toString().length
    val significantNumbersCount = numberLength % 3
    var totalDigitsGoup = if (significantNumbersCount == 0) {
        numberLength / 3
    } else {
        numberLength / 3 + 1
    }

    val (divisor, magnitudeSuffix) = when (totalDigitsGoup) {
        2 -> 1_000.0 to "K" // Thousands
        3 -> 1_000_000.0 to "M" // Millions
        4 -> 1_000_000_000.0 to "T" // Trillions
        5 -> 1_000_000_000_000.0 to "Q" // Quadrillions
        6 -> 1_000_000_000_000_000.0 to "Qi" // Quintillions
        else -> 1.0 to null
    }

    val formatter = DecimalFormat("#.##")
    val formattedAmmount = formatter.format(amount / divisor)

    return magnitudeSuffix?.let { "$$formattedAmmount $magnitudeSuffix" } ?: "$$formattedAmmount"
}