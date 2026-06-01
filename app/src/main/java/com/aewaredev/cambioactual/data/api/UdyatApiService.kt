package com.aewaredev.cambioactual.data.api

import com.aewaredev.cambioactual.data.model.UdyatCoinRate
import com.aewaredev.cambioactual.data.model.UdyatHistoricoResponse
import com.aewaredev.cambioactual.data.model.UdyatMsgResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UdyatApiService {
    @GET("historial")
    suspend fun getHistorial(): Response<List<UdyatCoinRate>>

    @GET("msg")
    suspend fun getMessages(): Response<UdyatMsgResponse>

    @GET("historico")
    suspend fun getHistorico(
        @Query("coin") coin: String
    ): Response<UdyatHistoricoResponse>

    companion object {
        const val BASE_URL = "https://api.udyat.site/"
    }
}
