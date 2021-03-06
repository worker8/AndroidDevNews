package com.worker8.androiddevnews.di

import android.content.Context
import com.google.android.exoplayer2.SimpleExoPlayer
import com.prof.rssparser.Parser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.nio.charset.Charset
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PodcastModule {
    @Provides
    fun provideRssParser(@ApplicationContext context: Context) = Parser.Builder()
        .context(context)
        .charset(Charset.forName("ISO-8859-7"))
        .cacheExpirationMillis(500) // one day
//        .cacheExpirationMillis(24L * 60L * 60L * 100L) // one day
        .build()

    @Singleton
    @Provides
    fun providesExoPlayer(@ApplicationContext context: Context) =
        SimpleExoPlayer.Builder(context).build()

}