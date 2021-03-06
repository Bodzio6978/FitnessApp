package com.gmail.bodziowaty6978.di

import com.gmail.bodziowaty6978.repository.*
import com.gmail.bodziowaty6978.room.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent


@Module
@InstallIn(ActivityRetainedComponent::class)
object RepositoryModule {

    @Provides
    fun provideMeasurementRepository(roomDatabase: AppDatabase) = MeasurementRepository(roomDatabase)

    @Provides
    fun provideMainRepository(roomDatabase:AppDatabase,firebase:FirebaseFirestore): MainRepository {
        return MainRepository(roomDatabase,firebase)
    }

    @Provides
    fun provideAuthRepository(): AuthRepository {
        return AuthRepository()
    }

    @Provides
    fun provideAddRepository(): AddRepository {
        return AddRepository()
    }
    
    @Provides
    fun provideProductRepository(): ProductRepository {
        return ProductRepository()
    }

    @Provides
    fun provideWeightChartRepository(roomDatabase:AppDatabase): WeightChartRepository {
        return WeightChartRepository(roomDatabase)
    }
}