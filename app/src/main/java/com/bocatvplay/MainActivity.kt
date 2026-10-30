package com.bocatvplay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playerView = findViewById<PlayerView>(R.id.playerView)
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        // Probamos directamente con un canal
        val url = "https://cdn.cvattv.com.ar/live/c6eds/TNT_Sports_HD/SA_Live_dash_enc/TNT_Sports_HD.mpd"
        val kid = "vID6PaaW5QP5QP3l9oGhxQ"
        val key = "6kbk6fETLo3XH7d_fVUFig"

        val kidHex = base64UrlToHex(kid)
        val keyHex = base64UrlToHex(key)
        val clearKeyJson = """{"keys":[{"kty":"oct","k":"$keyHex","kid":"$kidHex"}],"type":"temporary"}"""

        val drmConfig = MediaItem.DrmConfiguration.Builder(UUID.fromString("1077efec-c0b2-4d02-ace3-3c1e52e2fb4b"))
            .setLicenseUri("data:text/plain;base64," + android.util.Base64.encodeToString(clearKeyJson.toByteArray(), android.util.Base64.NO_WRAP))
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .setDrmConfiguration(drmConfig)
            .build()

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(mapOf(
                "Origin" to "https://portal.app.flow.com.ar",
                "Referer" to "https://portal.app.flow.com.ar/"
            ))

        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }

    private fun base64UrlToHex(base64Url: String): String {
        val base64 = base64Url.replace('-', '+').replace('_', '/')
        val decoded = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
        return decoded.joinToString("") { "%02x".format(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
