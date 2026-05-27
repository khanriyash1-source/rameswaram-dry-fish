package com.rameswaram.dryfish.presentation.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rameswaram.dryfish.data.repository.WishlistRepository
import com.rameswaram.dryfish.domain.model.WishlistItem
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WishlistUiState(
    val isLoading: Boolean = false,
    val items: List<WishlistItem> = emptyList(),
    val error: String? = null
)

class WishlistViewModel(
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WishlistUiState())
    val uiState: StateFlow<WishlistUiState> = _uiState.asStateFlow()

    init {
        loadWishlist()
    }

    fun loadWishlist() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = wishlistRepository.getWishlist()) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        items = result.data
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun removeFromWishlist(productId: String) {
        viewModelScope.launch {
            when (wishlistRepository.removeFromWishlist(productId)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        items = _uiState.value.items.filter { it.productId != productId }
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(error = "Failed to remove")
                }
                is Resource.Loading -> {}
            }
        }
    }
}
