package com.gmail.bodziowaty6978.di

import com.gmail.bodziowaty6978.interfaces.DispatcherProvider
import com.gmail.bodziowaty6978.other.StandardDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDispatcherProvider():DispatcherProvider{
        return StandardDispatchers()
    }
}