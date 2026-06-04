package com.aewaredev.cambioactual.data.api

import com.aewaredev.cambioactual.data.preferences.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()

        // Only add token to backend API calls
        // Assume backend URL will be provided later, for now we can check if it contains "api.udyat.site" or "github"
        if (url.contains("api.udyat.site") || url.contains("github.com")) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking {
            tokenManager.accessToken.firstOrNull()
        }

        val requestBuilder = originalRequest.newBuilder()
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
