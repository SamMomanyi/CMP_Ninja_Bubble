package org.sam_momanyi.game.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.sam_momanyi.game.domain.audio.AudioPlayer

actual val targetModule = module{
    single<AudioPlayer> {AudioPlayer() }
}