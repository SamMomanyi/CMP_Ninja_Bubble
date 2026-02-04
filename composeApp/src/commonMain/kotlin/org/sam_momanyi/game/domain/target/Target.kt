package org.sam_momanyi.game.domain.target

import androidx.compose.animation.core.Animatable
import androidx.compose.ui.graphics.Color

//an  interface defining targets
interface Target {
    //spawmed at a random position of canvas y position is animated to enable smooth descend
    val x: Float
    val y: Animatable<Float, *>
    val radius: Float
    val fallingSpeed :Float
    val color : Color
}