package com.example.myexoplayer

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.brightcove.player.appcompat.BrightcovePlayerFragment
import com.brightcove.player.model.DeliveryType
import com.brightcove.player.model.Video
import com.brightcove.player.view.BaseVideoView

class MiniPlayerFragment : BrightcovePlayerFragment() {

    private lateinit var viewModel: HomePlayerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val result: View = inflater.inflate(R.layout.fragment_mini_player, container, false)
        baseVideoView = result.findViewById<View>(R.id.brightcove_video_view) as BaseVideoView
        super.onCreateView(inflater, container, savedInstanceState)

        val video: Video =
            Video.createVideo("https://media.w3.org/2010/05/sintel/trailer.mp4", DeliveryType.MP4)
        baseVideoView.add(video)
        baseVideoView.analytics.account = "1760897681001"
        baseVideoView.start()

        return result
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            showMiniPlayer()
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideMiniPlayer()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[HomePlayerViewModel::class.java]
        //observeStates()
    }

    private fun observeStates() {
        viewModel.viewState.observe(viewLifecycleOwner, EventObserver { onStateChanged(it) })
    }

    private fun onStateChanged(state: HomePlayerViewState) {
        when (state) {
            HomePlayerViewState.MiniPlayerVisible -> showMiniPlayer()
            HomePlayerViewState.MiniPlayerGone -> hideMiniPlayer()
        }
    }

    private fun showMiniPlayer() {
        baseVideoView.visibility = View.VISIBLE
        baseVideoView.start()
    }

    private fun hideMiniPlayer() {
        baseVideoView.visibility = View.GONE
        baseVideoView.pause()
    }
}
