package com.apps.leo.clicker.game.common.geometry

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

fun distance2D(a: Offset, b: Offset): Float {
    val deltaX = abs(a.x - b.x)
    val deltaY = abs(a.y - b.y)
    return sqrt(deltaX.pow(2) + deltaY.pow(2))
}