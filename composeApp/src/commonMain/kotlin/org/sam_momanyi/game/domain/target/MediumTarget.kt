package org.sam_momanyi.game.domain.target

import androidx.compose.animation.core.Animatable
import androidx.compose.ui.graphics.Color

data class MediumTarget(
    override val x: Float = 0f,
    override val y: Animatable<Float, *> = Animatable(0f),
    override val radius: Float = 0f,
    override val fallingSpeed: Float = 0f,
    //purple color
    override val color: Color = Color(0xFF7F52FF),
    //extra property lives to represent how many values it will take t
    val lives: Int = 2
) : Target
