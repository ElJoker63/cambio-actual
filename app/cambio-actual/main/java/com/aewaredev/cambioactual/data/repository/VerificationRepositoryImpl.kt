package com.aewaredev.cambioactual.data.repository

import com.aewaredev.cambioactual.data.api.BackendApiService
import com.aewaredev.cambioactual.data.model.SimpleResponse
import com.aewaredev.cambioactual.data.model.VerificationStatus
import okhttp3.MultipartBody

class VerificationRepositoryImpl(private val apiService: BackendApiService) : VerificationRepository {
    override suspend fun getVerificationStatus(): Result<VerificationStatus> {
        return try {
            val response = apiService.getVerificationStatus()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error fetching verification status: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun startVerification(): Result<SimpleResponse> {
        return try {
            val response = apiService.startVerification()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error starting verification: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadDocument(document: MultipartBody.Part): Result<SimpleResponse> {
        return try {
            val response = apiService.uploadDocument(document)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error uploading document: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadSelfie(selfie: MultipartBody.Part): Result<SimpleResponse> {
        return try {
            val response = apiService.uploadSelfie(selfie)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error uploading selfie: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitVerification(): Result<SimpleResponse> {
        return try {
            val response = apiService.submitVerification()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error submitting verification: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
