package com.sparkmembership.sparkowner.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.sparkmembership.sparkfitness.data.repository.LocalRepositoryImpl
import com.sparkmembership.sparkowner.config.AppConfig
import com.sparkmembership.sparkowner.constant.KEY_PREFERENCE
import com.sparkmembership.sparkowner.data.remote.APIService
import com.sparkmembership.sparkowner.data.remote.AuthAPIService
import com.sparkmembership.sparkowner.data.repository.AuthRepositoryImpl
import com.sparkmembership.sparkowner.data.repository.MainRepositoryImp
import com.sparkmembership.sparkowner.domain.repository.AuthRepository
import com.sparkmembership.sparkowner.domain.repository.LocalRepository
import com.sparkmembership.sparkowner.domain.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providerRepositoryImpl(apiService: APIService):MainRepository{
        return MainRepositoryImp(apiService)
    }

    @Provides
    @Singleton
    fun providerAuthRepositoryImpl(authAPIService: AuthAPIService):AuthRepository{
        return AuthRepositoryImpl(authAPIService)
    }


    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ), produceFile = { context.preferencesDataStoreFile(KEY_PREFERENCE) }
        )
    }

    @Singleton
    @Provides
    fun provideLocalRepository(dataStore: DataStore<Preferences>): LocalRepository = LocalRepositoryImpl(dataStore)

    @Provides
    @Singleton
    fun provideAppConfig(localRepository: LocalRepository): AppConfig {
        return AppConfig(localRepository)
    }
}