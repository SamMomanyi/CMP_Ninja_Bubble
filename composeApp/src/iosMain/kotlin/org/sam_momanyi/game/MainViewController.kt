package org.sam_momanyi.game

import androidx.compose.ui.window.ComposeUIViewController
import org.sam_momanyi.game.app.App
import org.sam_momanyi.game.di.initializeKoin

//we then initialize koin on ios inside here
fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) { App() }