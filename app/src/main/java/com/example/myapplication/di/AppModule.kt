package com.example.myapplication.di

import android.app.Application
import androidx.room.Room
import com.example.myapplication.data.SautiService
import com.example.myapplication.data.db.SautiDb
import com.example.myapplication.data.db.SummonDao
import com.example.myapplication.utils.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideSautiService(): SautiService {
        return Retrofit.Builder()
            .baseUrl("https://emisomo.xyz/sauti/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(SautiService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): SautiDb {
        return Room
            .databaseBuilder(app, SautiDb::class.java, "sauti.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideSummonDao(db: SautiDb): SummonDao {
        return db.summonDao()
    }

}
