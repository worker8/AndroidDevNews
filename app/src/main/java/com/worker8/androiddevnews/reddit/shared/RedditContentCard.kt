package com.worker8.androiddevnews.reddit.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import coil.compose.rememberImagePainter
import coil.imageLoader
import com.kirkbushman.araw.models.Submission
import com.kirkbushman.araw.utils.createdDate
import com.worker8.androiddevnews.R
import com.worker8.androiddevnews.reddit.RenderFlair
import com.worker8.androiddevnews.reddit.isImageUrl
import com.worker8.androiddevnews.reddit.upvoteRatioPercentage
import com.worker8.androiddevnews.ui.HtmlView
import com.worker8.androiddevnews.ui.theme.Neutral01
import com.worker8.androiddevnews.ui.theme.Neutral06
import com.worker8.androiddevnews.ui.theme.Neutral10
import com.worker8.androiddevnews.ui.theme.Primary08
import com.worker8.androiddevnews.util.createImageRequest
import com.worker8.androiddevnews.util.toRelativeTimeString


@Composable
fun RedditContentCard(
    submission: Submission,
    truncation: Boolean,
    onLinkClick: (submission: Submission) -> Unit,
    onClick: (submission: Submission) -> Unit
) {
    Column(modifier = Modifier
        .padding(16.dp)
        .clickable {
            onClick(submission)
        }) {
        Text(
            text = submission.title,
            style = MaterialTheme.typography.subtitle1.copy(
                color = MaterialTheme.colors.Neutral10,
                fontWeight = FontWeight.Bold
            )
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                style = MaterialTheme.typography.subtitle2.copy(color = MaterialTheme.colors.Neutral06),
                text = submission.author
            )
            Text(
                style = MaterialTheme.typography.caption,
                text = submission.createdDate.toRelativeTimeString(),
                modifier = Modifier.padding(start = 8.dp)
            )
            submission.linkFlairText?.let { RenderFlair(it) }
        }
        val imageUrl = if (submission.url.isImageUrl()) {
            submission.url
        } else {
            submission.preview?.source()?.url?.let {
                HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    .toString()
            } ?: ""
        }
        if (imageUrl.isNotBlank()) {
            val height = submission.preview?.source()?.height ?: -1
            val width = submission.preview?.source()?.width ?: -1
            if (height != -1 && width != -1 && height > width) {
                // TODO: scale image properly, re-enable this text for debugging
//                                Text("PORTRAIT")
            } else if (height != -1 && width != -1 && height < width) {
//                                Text("LANDSCAPE")
            } else {
//                                Text("SQUARE")
            }
//                // TODO fix error image
//                .error(R.drawable.image_place_holder)
//                .placeholder(R.drawable.image_place_holder)
//                .data(imageUrl)
//                .build()
            // 1. link - portrait (show at side), landscape (show full)
            // 2. selftext - portrait (show at side), landscape (show full)

            Image(
                painter = rememberImagePainter(
                    createImageRequest(imageUrl),
                    LocalContext.current.imageLoader
                ),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 8.dp)
            )
        }

        /* Content Section */
        if (submission.domain.contains("self.androiddev")) {
            if (!submission.selfTextHtml.isNullOrBlank()) {
                Box(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) {
                    HtmlView(
                        content = submission.selfTextHtml ?: "(no selfText)",
                        strip = true,
                        truncation = truncation
                    ) {
                        onClick(submission)
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(4.dp),
                backgroundColor = MaterialTheme.colors.Neutral01
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(60.dp)
                        .clickable {
                            onLinkClick(submission)
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colors.Primary08)
                            .width(2.dp)
                            .fillMaxHeight()
                    )
                    Image(
                        modifier = Modifier
                            .padding(18.dp)
                            .size(20.dp),
                        imageVector = ImageVector.vectorResource(
                            id = R.drawable.ic_link
                        ),
                        contentDescription = "Link",
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.Neutral10)
                    )
//                                Icon(Icons.Outlined.Info, contentDescription = "Link")
                    Column(modifier = Modifier) {
                        Text(
                            style = MaterialTheme.typography.body2,
                            text = submission.domain,
                            color = MaterialTheme.colors.Neutral10
                        )
                        Text(
                            style = MaterialTheme.typography.caption,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = submission.url,
                            color = MaterialTheme.colors.Neutral10
                        )
                    }
                }
            }
        }
        Text(
            style = MaterialTheme.typography.caption,
            text = submission.score.toString() + " points " + submission.upvoteRatioPercentage
        )
        Text(
            style = MaterialTheme.typography.caption,
            text = submission.numComments.toString() + " comments"
        )
    }
}