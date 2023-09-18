package com.example.myexoplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
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
                HomeScreen(modifier = Modifier.fillMaxSize())
                /*ExoPlayerView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    mediaUrl = stringResource(id = R.string.media_url_mp3)
                )*/
            }
        }
    }
}

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

@OptIn(ExperimentalMotionApi::class)
@Composable
fun HomeScreen(modifier: Modifier) {
    val context = LocalContext.current
    val motionScene = remember {
        context.resources.openRawResource(R.raw.motion_scene).readBytes().decodeToString()
    }
    val scrollState = rememberScrollState()
    MotionLayout(modifier = Modifier.fillMaxSize(), motionScene = MotionScene(content = motionScene)) {
        // scrollview
        Column(modifier = Modifier
            .verticalScroll(scrollState)
            .layoutId("scrollView")) {
            ExoPlayerView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                mediaUrl = stringResource(id = R.string.media_url_mp3)
            )
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
            Text(text = "This is a text", fontSize = 30.sp)
        }

        Text(
            text = "this is a dynamic text with scroll",
            modifier = Modifier
                .background(Color.Green)
                .layoutId("dynamicTextView")
        )
    }
}