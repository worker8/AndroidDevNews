package com.worker8.androiddevnews.reddit.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import com.kirkbushman.araw.models.Submission
import com.kirkbushman.araw.models.base.CommentData
import com.worker8.androiddevnews.ui.theme.AndroidDevNewsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class RedditDetailActivity : AppCompatActivity() {

    @Inject
    lateinit var redditDetailController: RedditDetailController

    val submission: Submission get() = intent.getParcelableExtra(SubmissionKey)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidDevNewsTheme {
                // A surface container using the 'background' color from the theme
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Transparent)
                ) {
                    var offsetX = remember { mutableStateOf(0f) }
                    var offsetY = remember { mutableStateOf(0f) }
                    var screenWidth = remember { mutableStateOf(0) }
                    var isMoreThanHalf = remember { mutableStateOf(false) }
                    var onPressed = remember { mutableStateOf(false) }
                    val animationScope = rememberCoroutineScope()
                    val animateFloat = remember { Animatable(0f) }

//                    val offsetAnimation = animateDpAsState(
//                        if (onPressed.value) {
//                            Log.d("ddw", "1")
//                            LocalDensity.current.run { offsetX.value.toDp() }
//                        } else {
//                            if (!isMoreThanHalf.value) {
//                                Log.d("ddw", "2")
//                                0.dp
//                            } else {
//                                Log.d("ddw", "3")
//                                LocalDensity.current.run { screenWidth.value.toDp() }
//                            }
//                        }
//                    ) { endValue ->
//                        Log.d("ddw", "animation ends")
//                        if (!onPressed.value) {
//                            offsetX.value = endValue.value
//                        }
//                    }
                    Box(
                        Modifier
                            .offset {
                                IntOffset(
//                                    offsetX.value.roundToInt(),
//                                    if (onDragStarted.value) {
//                                    offsetX.value.roundToInt()
                                    animateFloat.value.roundToInt()
//                                    } else {
//                                    offsetAnimation.value.roundToPx()
//                                    }
                                    ,
                                    offsetY.value.roundToInt()
                                )
                            }
                            .onSizeChanged {
                                screenWidth.value = it.width
                            }
                            .fillMaxSize()
//                            .size(200.dp, 200.dp)
                            .background(Transparent)
                            .alpha(
                                if (isMoreThanHalf.value) {
                                    0.5f
                                } else {
                                    1f
                                }
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        onPressed.value = true
                                        Log.d("ddw", "DOWN DOWN DOWN DOWN DOWN")

                                    },
                                    onTap = {
//                                        onPressed.value = false
                                        Log.d("ddw", "UP UP UP UP UP UP UP UP")
                                    }
                                )
                            }
                            .pointerInput(Unit) {
                                detectDragGestures(onDragEnd = {
                                    Log.d("ddw", "END END END END END")
                                    onPressed.value = false
                                    animationScope.launch {
                                        animateFloat.animateTo(
                                            targetValue = 0f,
                                            animationSpec = tween(
                                                durationMillis = 100,
                                                easing = LinearEasing
                                            )
                                        )
                                    }
                                    if (isMoreThanHalf.value) {
                                        finish()
                                    }
                                }, onDrag = { change, dragAmount ->
                                    Log.d("ddw", "offsetX.value.toInt(): ${offsetX.value.toInt()}")
//                                    Log.d("ddw", "screenWidth.value: ${screenWidth.value}")
                                    isMoreThanHalf.value =
                                        offsetX.value.toInt() > (screenWidth.value) * 0.2
                                    offsetX.value = animateFloat.value + dragAmount.x
                                    animationScope.launch {
                                        animateFloat.snapTo(offsetX.value)
                                    }
//                                        offsetY.value = offsetY.value + dragAmount.y
                                })
                            }
                    ) {
                        Surface(color = MaterialTheme.colors.background) {
                            val redditDetailState =
                                remember { mutableStateOf(listOf<CommentData>()) }
                            RedditDetailScreen(
                                redditDetailController,
                                redditDetailState,
                                submission
                            )
                        }
                    }
                }

            }
        }
    }

    companion object {
        const val SubmissionKey = "SubmissionKey"
    }
}