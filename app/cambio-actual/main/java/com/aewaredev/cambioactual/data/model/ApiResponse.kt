package com.aewaredev.cambioactual.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SimpleResponse(
    val message: String,
    val success: Boolean = true
)

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Unauthorized : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}
