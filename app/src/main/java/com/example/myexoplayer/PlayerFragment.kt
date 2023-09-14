package com.example.myexoplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brightcove.player.appcompat.BrightcovePlayerFragment
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

        return result
    }
}
