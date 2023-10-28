package com.worker8.androiddevnews.reddit.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.kirkbushman.araw.models.Comment
import com.kirkbushman.araw.models.MoreComments
import com.kirkbushman.araw.models.Submission
import com.kirkbushman.araw.models.base.CommentData
import com.kirkbushman.araw.utils.createdDate
import com.worker8.androiddevnews.common.compose.HtmlView
import com.worker8.androiddevnews.common.compose.theme.Neutral02
import com.worker8.androiddevnews.common.compose.theme.Primary01
import com.worker8.androiddevnews.common.util.toRelativeTimeString
import com.worker8.androiddevnews.reddit.shared.RedditContentCard
import kotlinx.coroutines.cancel

private const val ColorBarWidth = 3

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
    val context = LocalContext.current
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
                    RedditContentCard(
                        submission = submission,
                        truncation = false,
                        onLinkClick = { submission ->
                            val browserIntent =
                                Intent(Intent.ACTION_VIEW, Uri.parse(submission.url))
                            context.startActivity(browserIntent)
                        }) {/* do nothing */ }
                } else {
                    val (columnHeight, setColumnHeight) = remember { mutableStateOf(1) }
                    val commentData = state.value[index - 1]
                    val columnHeightDp = LocalDensity.current.run { columnHeight.toDp() }
                    ConstraintLayout(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .wrapContentHeight()
                    ) {
                        val (colorBarRef, bodyRef) = createRefs()
                        val color = colors[commentData.depth % colors.size]
                        Box(
                            modifier = Modifier
                                .padding(start = (ColorBarWidth * commentData.depth).dp)
                                .background(color)
                                .constrainAs(colorBarRef) {
                                    start.linkTo(parent.start)
                                    width = Dimension.value(ColorBarWidth.dp + (ColorBarWidth * commentData.depth).dp)
                                    height = Dimension.value(columnHeightDp)
                                }
                        )
                        Column(
                            modifier = Modifier
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
                            if (commentData is Comment) {
                                Row(
                                    modifier = Modifier.padding(top = 10.dp),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    if (commentData.author == submission.author) {
                                        Card(
                                            Modifier.padding(
                                                start = 9.dp,
                                                end = 6.dp
                                            ),
                                            shape = RoundedCornerShape(50),
                                            backgroundColor = MaterialTheme.colors.Primary01,
                                        ) {
                                            Text(
                                                style = MaterialTheme.typography.subtitle2.copy(
                                                    color = MaterialTheme.colors.onBackground
                                                ),
                                                modifier = Modifier.padding(
                                                    horizontal = 8.dp,
                                                ),
                                                text = commentData.author
                                            )
                                        }
                                    } else {
                                        Text(
                                            modifier = Modifier
                                                .wrapContentHeight()
                                                .padding(start = 16.dp, end = 6.dp),
                                            style = MaterialTheme.typography.subtitle2,
                                            text = commentData.author
                                        )
                                    }
                                    Text(
                                        style = MaterialTheme.typography.caption,
                                        text = commentData.score.toString() + " points | "
                                    )
                                    Text(
                                        style = MaterialTheme.typography.caption,
                                        text = commentData.createdDate.toRelativeTimeString()
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .padding(
                                            top = 4.dp,
                                            bottom = 10.dp,
                                            start = 16.dp,
                                            end = 16.dp
                                        )
                                ) {
                                    HtmlView(
                                        content = commentData.bodyHtml,
                                        strip = true,
                                        truncation = false
                                    )
                                }
                            } else if (commentData is MoreComments) {
                                Text(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    style = MaterialTheme.typography.caption,
                                    text = "Load More (${commentData.children.size})"
                                )
                            }
                            Divider(color = MaterialTheme.colors.Neutral02, thickness = 1.dp)
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