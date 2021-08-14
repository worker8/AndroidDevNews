package com.worker8.androiddevnews.reddit.detail

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.kirkbushman.araw.models.Submission
import com.kirkbushman.araw.models.base.CommentData
import com.worker8.androiddevnews.SwipeToCloseBox
import com.worker8.androiddevnews.ui.theme.AndroidDevNewsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
                SwipeToCloseBox(onCloseCallback = { finish() }) {
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

    companion object {
        const val SubmissionKey = "SubmissionKey"
    }
}