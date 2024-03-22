package com.apps.leo.clicker.game.domain.model

data class Boost(
    val type: Type,
    val state: State
) {
    sealed interface State {
        object Available : State
        data class TemporaryAvailable(
            val timeTotal: Long,
            val timeLeft: Long
        ) : State

        data class Active(
            val timeTotal: Long,
            val timeLeft: Long
        ) : State
    }

    enum class Type {
        INCOME_X2,
        INCOME_X6,
        AUTO_CLICK
    }
}
