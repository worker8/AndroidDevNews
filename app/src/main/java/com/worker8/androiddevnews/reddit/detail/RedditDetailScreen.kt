package com.worker8.androiddevnews.reddit.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.kirkbushman.araw.models.Comment
import com.kirkbushman.araw.models.MoreComments
import com.kirkbushman.araw.models.Submission
import com.kirkbushman.araw.models.base.CommentData
import com.worker8.androiddevnews.reddit.HtmlViewEncode
import com.worker8.androiddevnews.reddit.shared.RedditContentCard
import kotlinx.coroutines.cancel

private val ColorBarWidth = 3

@Composable
fun RedditDetailScreen(
    controller: RedditDetailController,
    state: MutableState<List<CommentData>>,
    submission: Submission
) {
    val scope = rememberCoroutineScope()
    DisposableEffect(scope) {
        controller.init(scope, state, submission)
        onDispose {
            scope.cancel()
        }
    }
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxHeight()
            .padding(start = 4.dp)
    ) {
        items(
            count = state.value.count() + 1,
            itemContent = { index ->
                if (index == 0) {
                    RedditContentCard(submission)
                } else {
                    val (columnHeight, setColumnHeight) = remember { mutableStateOf(1) }
                    val item = state.value[index - 1]
                    val columnHeightDp = LocalDensity.current.run { columnHeight.toDp() }
                    ConstraintLayout(
                        modifier = Modifier
                            .padding(horizontal = (ColorBarWidth * item.depth).dp)
                            .fillParentMaxWidth()
                            .wrapContentHeight()
                    ) {
                        val (colorBarRef, bodyRef) = createRefs()
                        Box(
                            modifier = Modifier
                                .background(colors[item.depth % colors.size])
                                .constrainAs(colorBarRef) {
                                    start.linkTo(parent.start)
                                    width = Dimension.value(ColorBarWidth.dp)
                                    height = Dimension.value(columnHeightDp)
                                }
                        )
                        Column(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .constrainAs(bodyRef) {
                                    height = Dimension.wrapContent
                                    start.linkTo(colorBarRef.end)
                                    end.linkTo(parent.end)
                                }
                                .fillMaxWidth()
                                .onSizeChanged {
                                    setColumnHeight(it.height)
                                }
                        ) {
                            if (item is Comment) {
                                Text(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .padding(start = 4.dp),
                                    style = MaterialTheme.typography.subtitle2,
                                    text = item.author
                                )
                                Box(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .padding(start = 6.dp)
                                ) {
                                    HtmlViewEncode(item.bodyHtml)
                                }
                            } else if (item is MoreComments) {
                                Text(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .padding(start = 4.dp),
                                    style = MaterialTheme.typography.subtitle2,
                                    text = "Load More (${item.children.size})"
                                )
                            }

                        }

                    }
                }
            })
    }
}

private val colors = listOf(
    Color.Red,
    Color.Green,
    Color.Blue,
    Color.Gray,
    Color.DarkGray,
    Color.LightGray,
    Color.Yellow,
    Color.Cyan,
    Color.Magenta
)