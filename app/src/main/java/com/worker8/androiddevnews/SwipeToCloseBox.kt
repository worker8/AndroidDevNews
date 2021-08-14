package com.worker8.androiddevnews

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeToCloseBox(
    onCloseCallback: () -> Unit,
    closeThreshold: Float = 0.2f, /* should between 0 - 1f */
    closeAlpha: Float = 0.7f, /* should between 0 - 1f */
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Transparent)
    ) {
        var offsetX = remember { mutableStateOf(0f) }
        var screenWidth = remember { mutableStateOf(0) }
        var isMoreThanThreshold = remember { mutableStateOf(false) }
        val animationScope = rememberCoroutineScope()
        val animateFloat = remember { Animatable(0f) }

        Box(
            Modifier
                .offset {
                    IntOffset(
                        animateFloat.value.roundToInt(),
                        0
                    )
                }
                .onSizeChanged {
                    screenWidth.value = it.width
                }
                .fillMaxSize()
                .background(Transparent)
                .alpha(
                    if (isMoreThanThreshold.value) {
                        closeAlpha
                    } else {
                        1f
                    }
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            val targetValue = if (isMoreThanThreshold.value) {
                                screenWidth.value.toFloat()
                            } else {
                                0f
                            }
                            animationScope.launch {
                                animateFloat.animateTo(
                                    targetValue = targetValue,
                                    animationSpec = tween(
                                        durationMillis = 100,
                                        easing = LinearEasing
                                    )
                                )
                            }
                            if (isMoreThanThreshold.value) {
                                onCloseCallback()
                            }
                        }, onDrag = { _, dragAmount ->
                            isMoreThanThreshold.value =
                                offsetX.value.toInt() > (screenWidth.value) * closeThreshold
                            offsetX.value = animateFloat.value + dragAmount.x
                            animationScope.launch {
                                animateFloat.snapTo(offsetX.value)
                            }
                        })
                }
        ) {
            content()
        }
    }
}

