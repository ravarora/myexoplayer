package com.example.myexoplayer

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.brightcove.player.appcompat.BrightcovePlayerFragment
import com.brightcove.player.event.EventType
import com.brightcove.player.model.DeliveryType
import com.brightcove.player.model.Video
import com.brightcove.player.view.BaseVideoView

class PlayerFragment : BrightcovePlayerFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val result: View = inflater.inflate(R.layout.fragment_player, container, false)
        baseVideoView = result.findViewById<View>(R.id.brightcove_video_view) as BaseVideoView
        super.onCreateView(inflater, container, savedInstanceState)

        val video: Video =
            Video.createVideo("https://media.w3.org/2010/05/sintel/trailer.mp4", DeliveryType.MP4)
        baseVideoView.add(video)
        baseVideoView.analytics.account = "1760897681001"
        baseVideoView.start()

        baseVideoView.eventEmitter.on(EventType.ENTER_FULL_SCREEN) {
            screenSetup(this.activity as MainActivity, true)
        }

        baseVideoView.eventEmitter.on(EventType.EXIT_FULL_SCREEN) {
            screenSetup(this.activity as MainActivity, false)
        }

        return result
    }

    private fun screenSetup(activity: MainActivity, onFullScreen: Boolean) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        val windowInsetsCompat =
            WindowInsetsControllerCompat(activity.window, activity.window.decorView)
        windowInsetsCompat.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsCompat.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        activity.window.statusBarColor = android.graphics.Color.BLACK
        activity.window.navigationBarColor = android.graphics.Color.BLACK
        activity.requestedOrientation =
            if (onFullScreen) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
