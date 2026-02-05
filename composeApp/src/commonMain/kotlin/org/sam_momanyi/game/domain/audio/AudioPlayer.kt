package org.sam_momanyi.game.domain.audio

expect class AudioPlayer {
    fun playSound(index : Int)
}

//we could also add on a list of sounds here
val soundResList = listOf(
    "files/pop.mp3"
)