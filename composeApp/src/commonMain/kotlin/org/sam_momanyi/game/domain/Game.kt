package org.sam_momanyi.game.domain

data class Game(
    val status: GameStatus = GameStatus.Idle,
    val score : Int = 0,
    // to increase difficulty
    val settings : GameSettings = GameSettings()
)

data class GameSettings (
    val ninjaSpeed: Float = 15f,
    val weaponSpeed: Float = 20f,
    val targetSpeed: Float = 30f,

)
