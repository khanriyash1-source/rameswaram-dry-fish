package com.rameswaram.dryfish.presentation.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rameswaram.dryfish.data.repository.ProductRepository
import com.rameswaram.dryfish.data.repository.WishlistRepository
import com.rameswaram.dryfish.domain.model.Product
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ShopUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val wishlistedIds: Set<String> = emptySet(),
    val error: String? = null
)

class ShopViewModel(
    private val productRepository: ProductRepository,
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
        refreshWishlist()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = productRepository.getProducts()) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        products = result.data
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

    fun refreshWishlist() {
        viewModelScope.launch {
            when (val result = wishlistRepository.getWishlist()) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        wishlistedIds = result.data.map { it.productId }.toSet()
                    )
                }
                else -> {}
            }
        }
    }

    fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            val sku = product.skus.firstOrNull() ?: return@launch
            val isIn = product.id in _uiState.value.wishlistedIds
            _uiState.value = _uiState.value.copy(
                wishlistedIds = if (isIn) _uiState.value.wishlistedIds - product.id
                               else _uiState.value.wishlistedIds + product.id
            )
            val result = if (isIn) {
                wishlistRepository.removeFromWishlist(product.id)
            } else {
                wishlistRepository.addToWishlist(
                    productId = product.id,
                    productName = product.name,
                    productImage = product.images.firstOrNull() ?: "",
                    productSlug = product.slug,
                    price = sku.price,
                    mrp = sku.mrp,
                    weight = sku.weight
                )
            }
            if (result is Resource.Error) {
                _uiState.value = _uiState.value.copy(
                    wishlistedIds = if (isIn) _uiState.value.wishlistedIds + product.id
                                   else _uiState.value.wishlistedIds - product.id
                )
            }
        }
    }
}
