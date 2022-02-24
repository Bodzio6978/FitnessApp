package com.gmail.bodziowaty6978.di

import android.content.Context
import androidx.room.Room
import com.gmail.bodziowaty6978.interfaces.DispatcherProvider
import com.gmail.bodziowaty6978.other.StandardDispatchers
import com.gmail.bodziowaty6978.room.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Singleton
    @Provides
    fun provideFirebaseFirestore():FirebaseFirestore{
        return Firebase.firestore
    }

    @Singleton
    @Provides
    fun provideApplicationDatabase(@ApplicationContext context: Context):AppDatabase{
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()
    }

}