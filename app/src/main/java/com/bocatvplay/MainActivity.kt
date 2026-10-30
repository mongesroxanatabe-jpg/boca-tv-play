package com.bocatvplay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.bocatvplay.databinding.ActivityMainBinding
import org.json.JSONObject
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var player: ExoPlayer? = null
    private lateinit var channelList: List<Channel>

    data class Channel(
        val name: String,
        val category: String,
        val logo: String,
        val url: String,
        val kid: String?,
        val key: String?
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadChannels()
        setupPlayer()
    }

    private fun loadChannels() {
        val json = """
            {
              "canales": [
                { "name": "ESPN Premium", "category": "LIGA FUTBOL PROFESIONAL", "logo": "https://i.ibb.co/0jSPkhK/1917793265-ESPNPremium-11-png-c58d52a209062a470b942de50e11f529.png", "url": "https://cdn.cvattv.com.ar/live/c7eds/Fox_Sports_Premiun_HD/SA_Live_dash_enc_wl/Fox_Sports_Premiun_HD.mpd", "kid": "xQKJLgN6nzSCcUeysk5Czw", "key": "JTG9NL7GwOj6Tgeu2yYa3g" },
                { "name": "TNT Sports", "category": "LIGA FUTBOL PROFESIONAL", "logo": "https://i.postimg.cc/WbsZGtb2/Fondo_Oscuro.png", "url": "https://cdn.cvattv.com.ar/live/c6eds/TNT_Sports_HD/SA_Live_dash_enc/TNT_Sports_HD.mpd", "kid": "vID6PaaW5QP5QP3l9oGhxQ", "key": "6kbk6fETLo3XH7d_fVUFig" },
                { "name": "TyC Sports Internacional", "category": "LIGA FUTBOL PROFESIONAL", "logo": "https://i.postimg.cc/mD82Lb12/Ty_C_Sports.png", "url": "https://cdn.cvattv.com.ar/live/c3eds/TyC_Internacional/SA_Live_dash_enc/TyC_Internacional.mpd", "kid": "54gOGw1QyjJGRrnXPXgeHw", "key": "K-8teRLuUjENQhwdd1bQcg" },
                { "name": "TyC Sports", "category": "COPA ARGENTINA", "logo": "https://i.postimg.cc/mD82Lb12/Ty_C_Sports.png", "url": "https://cdn.cvattv.com.ar/live/c7eds/TyCSport/SA_Live_dash_enc_wl/TyCSport.mpd", "kid": "Cx5oJTUPobwAMvjzYofgBg", "key": "EfxUCkU50Y-RzUA6XtQihA" },
                { "name": "TyC Sports Internacional", "category": "COPA ARGENTINA", "logo": "https://i.postimg.cc/mD82Lb12/Ty_C_Sports.png", "url": "https://cdn.cvattv.com.ar/live/c3eds/TyC_Internacional/SA_Live_dash_enc/TyC_Internacional.mpd", "kid": "54gOGw1QyjJGRrnXPXgeHw", "key": "K-8teRLuUjENQhwdd1bQcg" },
                { "name": "ESPN", "category": "COPA SUDAMERICANA", "logo": "https://i.postimg.cc/T5YBc3cB/ESPN-1-Argentina.png", "url": "https://cdn.cvattv.com.ar/live/c3eds/ESPN2HD/SA_Live_dash_enc/ESPN2HD.mpd", "kid": "0Hn_o3V-gzVhH8SL5rpH_w", "key": "BQWdHLIbZwilXpwm-zji8w" },
                { "name": "ESPN 2", "category": "COPA SUDAMERICANA", "logo": "https://i.postimg.cc/D4yRcwg2/ESPN-2-Argentina.png", "url": "https://cdn.cvattv.com.ar/live/c6eds/ESPN2_Arg/SA_Live_dash_enc/ESPN2_Arg.mpd", "kid": "5uq1PPjqliuZnFnCmFlqZQ", "key": "EMEDrnb5liDWb6dfLAMwKg" },
                { "name": "ESPN 3", "category": "COPA SUDAMERICANA", "logo": "https://i.postimg.cc/0MCLmw7Z/ESPN-3-Argentina.png", "url": "https://cdn.cvattv.com.ar/live/c3eds/ESPN3/SA_Live_dash_enc/ESPN3.mpd", "kid": "3u7Q9ExrReBk9NUGRIboIQ", "key": "v4nO65NWTdxgO6av2Mkmag" },
                { "name": "DSports", "category": "COPA SUDAMERICANA", "logo": "https://i.postimg.cc/1tJPSst0/dsp-woldtv2.png", "url": "https://cdn.cvattv.com.ar/live/c3eds/DSports_1/SA_Live_dash_enc_wl/DSports_1.mpd", "kid": "htGZBEKfeLr5jPH9SBxDDg", "key": "UikZdzKpo-EJJksnK9eC8g" },
                { "name": "DSports 2", "category": "COPA SUDAMERICANA", "logo": "https://i.postimg.cc/ydY4vtPx/dsp2-woldtv2.png", "url": "https://cdn.cvattv.com.ar/live/c7eds/Dsports2_UY/SA_Live_dash_enc/Dsports2_UY.mpd", "kid": "30E2XL5lAz9OV_S3uvr78w", "key": "YsEfdbaDbh3v5IDYqXdTIA" },
                { "name": "DSports +", "category": "COPA SUDAMERICANA", "logo": "https://i.postimg.cc/Y9SZ1grS/dsp-woldtv2.png", "url": "https://cdn.cvattv.com.ar/live/c7eds/Dsports_Plus_UY/SA_Live_dash_enc/Dsports_Plus_UY.mpd", "kid": "_FG1jmZLYQZjMpB5ci2Ysw", "key": "OkRNKNFTE2ZbMFu5nA5Mgg" }
              ]
            }
        """.trimIndent()

        val obj = JSONObject(json)
        val arr = obj.getJSONArray("canales")
        val list = mutableListOf<Channel>()
        for (i in 0 until arr.length()) {
            val c = arr.getJSONObject(i)
            list.add(Channel(
                c.getString("name"), c.getString("category"), c.getString("logo"),
                c.getString("url"), c.optString("kid", null), c.optString("key", null)
            ))
        }
        channelList = list
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build()
        binding.playerView.player = player
    }

    private fun base64UrlToHex(base64Url: String): String {
        val base64 = base64Url.replace('-', '+').replace('_', '/')
        val decoded = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
        return decoded.joinToString("") { "%02x".format(it) }
    }

    private fun playChannel(channel: Channel) {
        val mediaItemBuilder = MediaItem.Builder()
            .setUri(channel.url)
            .setMimeType(if (channel.url.endsWith(".mpd")) MimeTypes.APPLICATION_MPD else MimeTypes.APPLICATION_M3U8)

        if (channel.kid != null && channel.key != null) {
            val kidHex = base64UrlToHex(channel.kid)
            val keyHex = base64UrlToHex(channel.key)
            val clearKeyJson = """{"keys":[{"kty":"oct","k":"$keyHex","kid":"$kidHex"}],"type":"temporary"}"""
            val drmConfig = MediaItem.DrmConfiguration.Builder(UUID.fromString("1077efec-c0b2-4d02-ace3-3c1e52e2fb4b"))
                .setLicenseUri("data:text/plain;base64," + android.util.Base64.encodeToString(clearKeyJson.toByteArray(), android.util.Base64.NO_WRAP))
                .build()
            mediaItemBuilder.setDrmConfiguration(drmConfig)
        }

        val mediaItem = mediaItemBuilder.build()

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(mapOf("Origin" to "https://portal.app.flow.com.ar", "Referer" to "https://portal.app.flow.com.ar/"))
        
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
