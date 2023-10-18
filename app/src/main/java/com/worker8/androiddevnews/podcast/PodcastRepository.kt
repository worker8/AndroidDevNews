package com.worker8.androiddevnews.podcast

import com.icosillion.podengine.models.Podcast
import java.net.URL
import java.util.TreeSet
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PodcastRepository {
    private val comparator =
        Comparator<PodcastContract.EpisodePair> { a, b ->
            if (a.episode.pubDate > b.episode.pubDate) {
                -1
            } else {
                1
            }
        }
    private val podcastList = listOf(
        // URL("https://blog.jetbrains.com/feed/"),
        URL("https://feeds.simplecast.com/LpAGSLnY"), // fragmented podcast
        URL("https://adbackstage.libsyn.com/rss"), // android backstage
        URL("https://nowinandroid.libsyn.com/rss") // now in android
    )

    fun getAllPodcasts() = TreeSet(comparator).apply {
        podcastList.forEach { url ->
            val podcast = Podcast(url)
            podcast.episodes.forEach {
                add(
                    PodcastContract.EpisodePair(
                        episode = it,
                        podcastImageUrl = podcast.imageURL.toString(),
                        podcastTitle = podcast.title
                    )
                )
            }
        }
    }
}
