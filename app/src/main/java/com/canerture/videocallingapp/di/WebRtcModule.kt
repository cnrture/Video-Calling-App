package com.canerture.videocallingapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.webrtc.EglBase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class WebRtcModule {

    @Singleton
    @Provides
    fun provideEglBase(): EglBase = EglBase.create()
}