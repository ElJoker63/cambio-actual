package com.aewaredev.cambioactual.data.api

import android.content.Context
import com.aewaredev.cambioactual.BuildConfig
import com.aewaredev.cambioactual.data.preferences.TokenManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    private const val PRODUCTION_BACKEND_URL = "https://backend.cambioactual.site/" // Reemplaza con tu dominio real
    private const val LOCAL_BACKEND_URL = "http://192.168.12.107:8000/"

    // Usamos LOCAL si estamos en DEBUG, de lo contrario PRODUCTION
    val BACKEND_URL = if (BuildConfig.DEBUG) LOCAL_BACKEND_URL else PRODUCTION_BACKEND_URL

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    private val baseOkHttpClientBuilder = OkHttpClient.Builder()
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

    private val udyatOkHttpClient = baseOkHttpClientBuilder.build()

    val udyatApi: UdyatApiService by lazy {
        Retrofit.Builder()
            .baseUrl(UdyatApiService.BASE_URL)
            .client(udyatOkHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(UdyatApiService::class.java)
    }

    val updateApi: UpdateApiService by lazy {
        Retrofit.Builder()
            .baseUrl(UpdateApiService.BASE_URL)
            .client(udyatOkHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(UpdateApiService::class.java)
    }

    fun provideBackendApi(context: Context): BackendApiService {
        val tokenManager = TokenManager(context)
        val client = udyatOkHttpClient.newBuilder()
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
