package com.aewaredev.cambioactual.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aewaredev.cambioactual.data.model.MarketplacePost
import com.aewaredev.cambioactual.data.model.MarketplacePostCreate
import com.aewaredev.cambioactual.data.model.UiState
import com.aewaredev.cambioactual.data.repository.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MarketplaceViewModel(private val repository: MarketplaceRepository) : ViewModel() {

    private val _postsState = MutableStateFlow<UiState<List<MarketplacePost>>>(UiState.Idle)
    val postsState: StateFlow<UiState<List<MarketplacePost>>> = _postsState

    private val _postDetailState = MutableStateFlow<UiState<MarketplacePost>>(UiState.Idle)
    val postDetailState: StateFlow<UiState<MarketplacePost>> = _postDetailState

    private val _createPostState = MutableStateFlow<UiState<MarketplacePost>>(UiState.Idle)
    val createPostState: StateFlow<UiState<MarketplacePost>> = _createPostState

    fun fetchPosts(
        page: Int = 1,
        type: String? = null,
        category: String? = null,
        query: String? = null,
        currency: String? = null,
        location: String? = null
    ) {
        viewModelScope.launch {
            _postsState.value = UiState.Loading
            val result = repository.getPosts(page, 20, type, category, query, currency, location)
            result.fold(
                onSuccess = { 
                    if (it.isEmpty()) _postsState.value = UiState.Empty
                    else _postsState.value = UiState.Success(it)
                },
                onFailure = { _postsState.value = UiState.Error(it.message ?: "Error fetching posts") }
            )
        }
    }

    fun fetchPostDetail(postId: Int) {
        viewModelScope.launch {
            _postDetailState.value = UiState.Loading
            val result = repository.getPostById(postId)
            result.fold(
                onSuccess = { _postDetailState.value = UiState.Success(it) },
                onFailure = { _postDetailState.value = UiState.Error(it.message ?: "Error fetching post detail") }
            )
        }
    }

    fun createPost(post: MarketplacePostCreate) {
        viewModelScope.launch {
            _createPostState.value = UiState.Loading
            val result = repository.createPost(post)
            result.fold(
                onSuccess = { _createPostState.value = UiState.Success(it) },
                onFailure = { _createPostState.value = UiState.Error(it.message ?: "Error creating post") }
            )
        }
    }

    fun resetCreatePostState() {
        _createPostState.value = UiState.Idle
    }
}
