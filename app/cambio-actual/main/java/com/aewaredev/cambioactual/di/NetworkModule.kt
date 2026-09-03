package com.aewaredev.cambioactual.di

import android.content.Context
import com.aewaredev.cambioactual.BuildConfig
import com.aewaredev.cambioactual.data.api.AuthInterceptor
import com.aewaredev.cambioactual.data.api.BackendApiService
import com.aewaredev.cambioactual.data.api.UdyatApiService
import com.aewaredev.cambioactual.data.api.UpdateApiService
import com.aewaredev.cambioactual.data.preferences.TokenManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val PRODUCTION_BACKEND_URL = "https://backend.cambioactual.site/"
    private const val LOCAL_BACKEND_URL = "http://192.168.12.107:8000/"

    val BACKEND_URL = if (BuildConfig.DEBUG) LOCAL_BACKEND_URL else PRODUCTION_BACKEND_URL

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "CambioActual-App")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideUdyatApi(okHttpClient: OkHttpClient, moshi: Moshi): UdyatApiService {
        return Retrofit.Builder()
            .baseUrl(UdyatApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(UdyatApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUpdateApi(okHttpClient: OkHttpClient, moshi: Moshi): UpdateApiService {
        return Retrofit.Builder()
            .baseUrl(UpdateApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(UpdateApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBackendApi(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): BackendApiService {
        val tokenManager = TokenManager(context)
        val client = okHttpClient.newBuilder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .build()

        return Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(BackendApiService::class.java)
    }
}
