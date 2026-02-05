package org.sam_momanyi.game

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.sam_momanyi.game.di.initializeKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin {
            androidContext(this@MyApplication)
        }
    }
}
