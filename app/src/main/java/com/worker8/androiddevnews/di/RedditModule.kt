package com.worker8.androiddevnews.di

import android.content.Context
import com.kirkbushman.araw.helpers.AuthUserlessHelper
import com.worker8.androiddevnews.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RedditModule {
    @Provides
    fun provideUserlessAuth(@ApplicationContext context: Context): AuthUserlessHelper {
        return AuthUserlessHelper(
            context = context,
            clientId = BuildConfig.RedditClientId,
            deviceId = null,
            logging = true
        )
    }
}