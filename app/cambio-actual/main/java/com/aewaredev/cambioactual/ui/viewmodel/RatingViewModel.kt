package com.aewaredev.cambioactual.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aewaredev.cambioactual.data.model.Rating
import com.aewaredev.cambioactual.data.model.RatingRequest
import com.aewaredev.cambioactual.data.model.UiState
import com.aewaredev.cambioactual.data.repository.RatingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RatingViewModel(private val repository: RatingRepository) : ViewModel() {

    private val _ratingsState = MutableStateFlow<UiState<List<Rating>>>(UiState.Idle)
    val ratingsState: StateFlow<UiState<List<Rating>>> = _ratingsState

    private val _createRatingState = MutableStateFlow<UiState<Rating>>(UiState.Idle)
    val createRatingState: StateFlow<UiState<Rating>> = _createRatingState

    fun fetchUserRatings(userId: Int) {
        viewModelScope.launch {
            _ratingsState.value = UiState.Loading
            val result = repository.getUserRatings(userId)
            result.fold(
                onSuccess = { _ratingsState.value = UiState.Success(it) },
                onFailure = { _ratingsState.value = UiState.Error(it.message ?: "Error fetching ratings") }
            )
        }
    }

    fun fetchMyRatings() {
        viewModelScope.launch {
            _ratingsState.value = UiState.Loading
            val result = repository.getMyRatings()
            result.fold(
                onSuccess = { _ratingsState.value = UiState.Success(it) },
                onFailure = { _ratingsState.value = UiState.Error(it.message ?: "Error fetching my ratings") }
            )
        }
    }

    fun createRating(toUserId: Int, stars: Int, comment: String?) {
        viewModelScope.launch {
            _createRatingState.value = UiState.Loading
            val result = repository.createRating(RatingRequest(toUserId, stars, comment))
            result.fold(
                onSuccess = { _createRatingState.value = UiState.Success(it) },
                onFailure = { _createRatingState.value = UiState.Error(it.message ?: "Error creating rating") }
            )
        }
    }

    fun resetCreateRatingState() {
        _createRatingState.value = UiState.Idle
    }
}
