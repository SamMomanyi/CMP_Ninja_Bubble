package org.sam_momanyi.game.domain.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import cmp_ninjabubble.composeapp.generated.resources.Res


actual class AudioPlayer(context : Context) {

    private val mediaPlayer = ExoPlayer.Builder(context).build()
    private val mediaItems = soundResList.map {
        MediaItem.fromUri(Res.getUri(it))
    }
    init {
        mediaPlayer.prepare()
    }

    actual fun playSound(index: Int) {
        mediaPlayer.setMediaItem(mediaItems[index])
        mediaPlayer.play()
    }
}