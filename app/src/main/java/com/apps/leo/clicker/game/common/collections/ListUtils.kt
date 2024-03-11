package com.apps.leo.clicker.game.common.collections

fun <T> List<T>.remove(element: T): List<T> {
    val updatedList = this.toMutableList()
    updatedList.remove(element)
    return updatedList.toList()
}

fun <T> List<T>.add(element: T): List<T> {
    val updatedList = this.toMutableList()
    updatedList.add(element)
    return updatedList.toList()
}

fun <T> List<T>.swap(elementToRemove: T, elementToAdd: T): List<T> {
    val updatedList = this.toMutableList()
    val index = updatedList.indexOf(elementToRemove)

    if (index >= 0) {
        updatedList.remove(elementToRemove)
        updatedList.add(index, elementToAdd)
    }

    return updatedList.toList()
}