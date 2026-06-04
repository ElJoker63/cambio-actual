package com.aewaredev.cambioactual.data.api

import com.aewaredev.cambioactual.data.model.GitHubRelease
import retrofit2.Response
import retrofit2.http.GET

interface UpdateApiService {
    @GET("repos/ElJoker63/cambio-actual-app/releases/latest")
    suspend fun getLatestRelease(): Response<GitHubRelease>

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }
}
