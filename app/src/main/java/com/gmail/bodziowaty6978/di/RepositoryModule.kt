package com.gmail.bodziowaty6978.di

import com.gmail.bodziowaty6978.repository.MainRepository
import com.gmail.bodziowaty6978.room.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMainRepository(roomDatabase:AppDatabase,firebase:FirebaseFirestore): MainRepository {
        return MainRepository(roomDatabase,firebase)
    }
}