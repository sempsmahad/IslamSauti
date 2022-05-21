/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.myapplication.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.source.DefaultSummonsRepository
import com.example.myapplication.data.source.local.SautiDb
import com.example.myapplication.data.source.SummonsDataSource
import com.example.myapplication.data.source.impl.SummonsRepository
import com.example.myapplication.data.source.local.SummonsLocalDataSource
import com.example.myapplication.data.source.remote.SummonsRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * Module to tell Hilt how to provide instances of types that cannot be constructor-injected.
 *
 * As these types are scoped to the application lifecycle using @Singleton, they're installed
 * in Hilt's ApplicationComponent.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Qualifier
    @Retention(RUNTIME)
    annotation class RemoteSummonsDataSource

    @Qualifier
    @Retention(RUNTIME)
    annotation class LocalSummonsDataSource

    @Singleton
    @RemoteSummonsDataSource
    @Provides
    fun provideSummonsRemoteDataSource(): SummonsDataSource {
        return SummonsRemoteDataSource
    }

    @Singleton
    @LocalSummonsDataSource
    @Provides
    fun provideSummonsLocalDataSource(
        database: SautiDb,
        ioDispatcher: CoroutineDispatcher
    ): SummonsDataSource {
        return SummonsLocalDataSource(
            database.summonDao(), ioDispatcher
        )
    }

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): SautiDb {
        return Room.databaseBuilder(
            context.applicationContext,
            SautiDb::class.java,
            "Sauti.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}

/**
 * The binding for SummonsRepository is on its own module so that we can replace it easily in tests.
 */
@Module
@InstallIn(SingletonComponent::class)
object SummonsRepositoryModule {

    @Singleton
    @Provides
    fun provideSummonsRepository(
        @AppModule.RemoteSummonsDataSource remoteSummonsDataSource: SummonsDataSource,
        @AppModule.LocalSummonsDataSource localSummonsDataSource: SummonsDataSource,
        ioDispatcher: CoroutineDispatcher
    ): SummonsRepository {
        return DefaultSummonsRepository(
            remoteSummonsDataSource, localSummonsDataSource, ioDispatcher
        )
    }
}
