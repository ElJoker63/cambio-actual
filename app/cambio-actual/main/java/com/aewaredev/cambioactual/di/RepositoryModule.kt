package com.aewaredev.cambioactual.di

import com.aewaredev.cambioactual.data.api.BackendApiService
import com.aewaredev.cambioactual.data.api.UdyatApiService
import com.aewaredev.cambioactual.data.api.UpdateApiService
import com.aewaredev.cambioactual.data.local.RateDao
import com.aewaredev.cambioactual.data.local.SmsDao
import com.aewaredev.cambioactual.data.preferences.TokenManager
import com.aewaredev.cambioactual.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideExchangeRepository(
        udyatApi: UdyatApiService,
        updateApi: UpdateApiService,
        rateDao: RateDao,
        smsDao: SmsDao
    ): ExchangeRepository {
        return ExchangeRepositoryImpl(udyatApi, updateApi, rateDao, smsDao)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        backendApi: BackendApiService,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(backendApi, tokenManager)
    }

    @Provides
    @Singleton
    fun provideMarketplaceRepository(backendApi: BackendApiService): MarketplaceRepository {
        return MarketplaceRepositoryImpl(backendApi)
    }

    @Provides
    @Singleton
    fun provideUserRepository(backendApi: BackendApiService): UserRepository {
        return UserRepositoryImpl(backendApi)
    }

    @Provides
    @Singleton
    fun provideRatingRepository(backendApi: BackendApiService): RatingRepository {
        return RatingRepositoryImpl(backendApi)
    }

    @Provides
    @Singleton
    fun provideVerificationRepository(backendApi: BackendApiService): VerificationRepository {
        return VerificationRepositoryImpl(backendApi)
    }
}
