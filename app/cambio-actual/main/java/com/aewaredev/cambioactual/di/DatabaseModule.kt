package com.aewaredev.cambioactual.di

import android.content.Context
import com.aewaredev.cambioactual.data.local.AppDatabase
import com.aewaredev.cambioactual.data.local.RateDao
import com.aewaredev.cambioactual.data.local.SmsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideRateDao(database: AppDatabase): RateDao {
        return database.rateDao()
    }

    @Provides
    fun provideSmsDao(database: AppDatabase): SmsDao {
        return database.smsDao()
    }
}
