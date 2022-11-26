package com.worker8.androiddevnews.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
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
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/**
 * A layout composable with [content].
 * [SwipeToCloseBox] is actually just a [androidx.compose.foundation.layout.Box] that detects swiping to close.
 * If [SwipeToCloseBox] is used with a transparent [android.app.Activity], swiping towards right over the [closeThreshold] will close the entire activity.
 *
 * @param onCloseCallback this callback will be invoked after the close animation is completed
 * @param closeThreshold range: 0the percentage of the screenWidth to close the view. E.g. 0.2f means when you drag the view more than 20%, and release, this view will be closed. Must be between 0 and 1, inclusive.
 * @param closeAlpha the alpha of the view when the dragging crosses the threshold. Must be between 0 and 1, inclusive.
 * @param animationDuration duration of the animation. In milliseconds.
 * @param content the content to be rendered
 */
@Composable
fun SwipeToCloseBox(
    onCloseCallback: () -> Unit,
    closeThreshold: Float = 0.2f,
    closeAlpha: Float = 0.7f,
    animationDuration: Int = 300,
    content: @Composable BoxScope.() -> Unit
) {
    val offsetX = remember { mutableStateOf(0f) }
    val screenWidth = remember { mutableStateOf(0) }
    val isMoreThanThreshold = remember { mutableStateOf(false) }
    val animationScope = rememberCoroutineScope()
    val animatableFloat = remember { Animatable(0f) }
    Box(
        Modifier
            .offset { IntOffset(animatableFloat.value.roundToInt(), 0) }
            .onSizeChanged { screenWidth.value = it.width }
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
                            animatableFloat.animateTo(
                                targetValue = targetValue,
                                animationSpec = tween(
                                    durationMillis = animationDuration,
                                    easing = LinearOutSlowInEasing
                                )
                            )
                            if (isMoreThanThreshold.value) {
                                onCloseCallback()
                            }
                        }
                    },
                    onDrag = { _, dragAmount ->
                        isMoreThanThreshold.value =
                            offsetX.value.toInt() > (screenWidth.value) * closeThreshold
                        offsetX.value = animatableFloat.value + dragAmount.x
                        animationScope.launch {
                            animatableFloat.snapTo(offsetX.value)
                        }
                    })
            }
    ) {
        content()
    }
}
