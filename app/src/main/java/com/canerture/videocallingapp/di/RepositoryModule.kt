package com.canerture.videocallingapp.di

import android.content.Context
import com.canerture.videocallingapp.data.repository.CallRepositoryImpl
import com.canerture.videocallingapp.data.repository.LoginRepositoryImpl
import com.canerture.videocallingapp.data.repository.MainRepositoryImpl
import com.canerture.videocallingapp.domain.CallRepository
import com.canerture.videocallingapp.domain.LoginRepository
import com.canerture.videocallingapp.domain.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import org.webrtc.EglBase

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideCallRepository(
        @ApplicationContext context: Context,
        eglBase: EglBase
    ): CallRepository =
        CallRepositoryImpl(context, eglBase)

    @Provides
    @ViewModelScoped
    fun provideLoginRepository(@ApplicationContext context: Context): LoginRepository =
        LoginRepositoryImpl(context)

    @Provides
    @ViewModelScoped
    fun provideMainRepository(@ApplicationContext context: Context): MainRepository =
        MainRepositoryImpl(context)
}