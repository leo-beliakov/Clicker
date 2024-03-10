package com.apps.leo.clicker.game.common.random

import androidx.compose.ui.geometry.Offset
import kotlin.random.Random

fun Random.nextFloat(from: Float, until: Float): Float {
    return Random.nextDouble(
        from = from.toDouble(),
        until = until.toDouble()
    ).toFloat()
}

fun Random.nextOffset(
    xFrom: Float,
    xUntil: Float,
    yFrom: Float,
    yUntil: Float
): Offset {
    return Offset(
        x = Random.nextFloat(
            from = xFrom,
            until = xUntil
        ).toFloat(),
        y = Random.nextFloat(
            from = yFrom,
            until = yUntil
        ).toFloat()
    )
}