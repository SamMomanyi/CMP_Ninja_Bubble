package org.sam_momanyi.game

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform