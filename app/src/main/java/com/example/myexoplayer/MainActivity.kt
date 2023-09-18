package com.example.myexoplayer

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
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
import com.brightcove.player.pictureinpicture.PictureInPictureManager
import com.example.myexoplayer.ui.theme.MyExoPlayerTheme

class MainActivity : FragmentActivity() {

    lateinit var pipManager: PictureInPictureManager

    @OptIn(ExperimentalMotionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyExoPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {

                            FragmentContainer(
                                modifier = Modifier
                                    .height(300.dp)
                                    .fillMaxWidth(),
                                fragmentManager = supportFragmentManager,
                                commit = { add(it, PlayerFragment()) })


                            repeat(30) {
                                Text("Item $it", modifier = Modifier.padding(6.dp))
                            }
                            Spacer(modifier = Modifier.height(150.dp))
                        }

                        MiniPlayer(modifier = Modifier
                            .height(150.dp)
                            .align(Alignment.BottomEnd),
                            fragmentManager = supportFragmentManager,
                            commit = { add(it, PlayerFragment()) })
                    }
                }
            }
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        pipManager.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        pipManager.onUserLeaveHint()
    }
}

@OptIn(ExperimentalMotionApi::class)
@Composable
fun MotionPlayer(progress: Float) {
    val context = LocalContext.current
    val motionSceneContent = remember {
        context.resources
            .openRawResource(R.raw.motion_scene)
            .readBytes()
            .decodeToString()
    }
    MotionLayout(
        motionScene = MotionScene(motionSceneContent),
        progress = progress,
        modifier = Modifier
            .fillMaxSize(),
        //debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL)
    ) {
        val properties = motionProperties(id = "my_text")

        Image(
            painter = painterResource(
                id = R.drawable.ic_launcher_foreground
            ),
            contentDescription = "",
            modifier = Modifier
                .layoutId("my_image")
        )
        Divider(
            color = Color.Gray,
            thickness = 2.dp,
            modifier = Modifier
                .layoutId("my_divider")
        )
        Text(
            text = "unit",
            color = properties.value.color("textColor"),
            modifier = Modifier
                .layoutId("my_text")
        )
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
fun MiniPlayer(
    modifier: Modifier = Modifier,
    fragmentManager: FragmentManager,
    commit: FragmentTransaction.(containerId: Int) -> Unit
) {
    Row(
        modifier = modifier
            .padding(start = 8.dp, top = 20.dp)
            .background(Color.Gray)
    ) {
        Text(text = "MOVIE")
        FragmentContainer(fragmentManager = fragmentManager, commit = commit)
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