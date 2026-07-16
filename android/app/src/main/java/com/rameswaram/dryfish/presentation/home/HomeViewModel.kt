package com.rameswaram.dryfish.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rameswaram.dryfish.data.repository.ProductRepository
import com.rameswaram.dryfish.domain.model.Category
import com.rameswaram.dryfish.domain.model.Product
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val featuredProducts: List<Product> = emptyList(),
    val bestsellerProducts: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val error: String? = null
)

class HomeViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            productRepository.refreshFromFirestore()
            loadFeatured()
            loadBestsellers()
            loadCategories()

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private suspend fun loadFeatured() {
        when (val result = productRepository.getFeaturedProducts()) {
            is Resource.Success -> {
                _uiState.value = _uiState.value.copy(
                    featuredProducts = result.data
                )
            }
            is Resource.Error -> {
                _uiState.value = _uiState.value.copy(
                    error = result.message
                )
            }
            is Resource.Loading -> {}
        }
    }

    private suspend fun loadBestsellers() {
        when (val result = productRepository.getBestsellers()) {
            is Resource.Success -> {
                _uiState.value = _uiState.value.copy(
                    bestsellerProducts = result.data
                )
            }
            is Resource.Error -> {}
            is Resource.Loading -> {}
        }
    }

    private suspend fun loadCategories() {
        when (val result = productRepository.getCategories()) {
            is Resource.Success -> {
                _uiState.value = _uiState.value.copy(
                    categories = result.data
                )
            }
            is Resource.Error -> {}
            is Resource.Loading -> {}
        }
    }
}
