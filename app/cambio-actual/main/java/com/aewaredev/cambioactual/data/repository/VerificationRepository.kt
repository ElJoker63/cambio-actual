package com.aewaredev.cambioactual.data.repository

import com.aewaredev.cambioactual.data.model.SimpleResponse
import com.aewaredev.cambioactual.data.model.VerificationStatus
import okhttp3.MultipartBody

interface VerificationRepository {
    suspend fun getVerificationStatus(): Result<VerificationStatus>
    suspend fun startVerification(): Result<SimpleResponse>
    suspend fun uploadDocument(document: MultipartBody.Part): Result<SimpleResponse>
    suspend fun uploadSelfie(selfie: MultipartBody.Part): Result<SimpleResponse>
    suspend fun submitVerification(): Result<SimpleResponse>
}
