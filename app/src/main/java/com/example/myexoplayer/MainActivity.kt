package com.example.myexoplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.myexoplayer.ui.theme.MyExoPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyExoPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExoPlayerView(modifier = Modifier.fillMaxSize(), mediaUrl = stringResource(id = R.string.media_url_mp3))
                }
            }
        }
    }
}

@Composable
fun ExoPlayerView(modifier: Modifier = Modifier, mediaUrl : String) {
    val context = LocalContext.current
    var mediaItemIndex by remember {
        mutableStateOf(0)
    }

    var playbackPosition by remember {
        mutableStateOf(0L)
    }

    var playWhenReady by remember {
        mutableStateOf(false)
    }

    // create exo player
    val exoplayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .also { exoPlayer ->
                // create media item
                val mediaItem = MediaItem.fromUri(mediaUrl)
                exoPlayer.setMediaItems(listOf(mediaItem), mediaItemIndex, playbackPosition)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.prepare()
            }
    }

    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(
        AndroidView(modifier = modifier, factory = {
            PlayerView(context).apply {
                player = exoplayer
            }
        })
    ) {
        val observer = LifecycleEventObserver { _, event ->
            when(event) {
                //Lifecycle.Event.ON_RESUME -> exoplayer.play()
                Lifecycle.Event.ON_PAUSE -> exoplayer.stop()
                else -> {}
            }
        }

        val lifecycle = lifecycleOwner.value.lifecycle
        lifecycle.addObserver(observer)

        onDispose {
            exoplayer?.let {
                playbackPosition = it.currentPosition
                mediaItemIndex = it.currentMediaItemIndex
                playWhenReady = it.playWhenReady
            }
            exoplayer.release()
            lifecycle.removeObserver(observer)
        }
    }
}