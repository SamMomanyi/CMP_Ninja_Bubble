package org.sam_momanyi.game.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import org.sam_momanyi.game.domain.audio.AudioPlayer

actual val targetModule = module{
    //singleton instance of andoird player and androidContext from androidContext()
    single<AudioPlayer> {
        AudioPlayer(context = androidContext())
    }
}