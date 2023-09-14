package com.example.myexoplayer

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.brightcove.player.model.DeliveryType
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcoveExoPlayerVideoView
import com.brightcove.player.view.BrightcovePlayer
import com.example.myexoplayer.ui.theme.MyExoPlayerTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyExoPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        /*ExoPlayerView(
                                             modifier = Modifier.fillMaxSize(),
                                             mediaUrl = stringResource(id = R.string.media_url_mp3)
                                         )*/

                        FragmentContainer(
                            modifier = Modifier.fillMaxWidth(),
                            fragmentManager = supportFragmentManager,
                            commit = { add(it, PlayerFragment()) })

                        //BrightCoveView(modifier = Modifier.fillMaxSize())

                        LazyColumnDemo()

                    }
                }
            }
        }
    }
}

@Composable
fun LazyColumnDemo() {
    LazyColumn {
        // Add 10 items
        items(10) { index ->
            // Content of each item
            Text(
                text = "Item $index",
                modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}


//TODO Trying a way to inflate brightcove player directly from activity
/*@Composable
fun BrightCoveView(modifier: Modifier = Modifier) {
    AndroidView(modifier = modifier, factory = {
        BrightcoveExoPlayerVideoView(it).apply {
            val video: Video =
                Video.createVideo("https://media.w3.org/2010/05/sintel/trailer.mp4", DeliveryType.MP4)
            this.add(video)
            this.analytics.account = "1760897681001"
            this.start()
        }
    })
}*/

@Composable
fun ExoPlayerView(modifier: Modifier = Modifier, mediaUrl: String) {
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
            when (event) {
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

@Composable
fun FragmentContainer(
    modifier: Modifier = Modifier,
    fragmentManager: FragmentManager,
    commit: FragmentTransaction.(containerId: Int) -> Unit
) {
    val containerId by rememberSaveable { mutableStateOf(View.generateViewId()) }
    var initialized by rememberSaveable { mutableStateOf(false) }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            FragmentContainerView(context)
                .apply { id = containerId }
        },
        update = { view ->
            if (!initialized) {
                fragmentManager.commit { commit(view.id) }
                initialized = true
            } else {
                fragmentManager.onContainerAvailable(view)
            }
        }
    )
}

/** Access to package-private method in FragmentManager through reflection */
private fun FragmentManager.onContainerAvailable(view: FragmentContainerView) {
    val method = FragmentManager::class.java.getDeclaredMethod(
        "onContainerAvailable",
        FragmentContainerView::class.java
    )
    method.isAccessible = true
    method.invoke(this, view)
}