package com.rameswaram.dryfish.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rameswaram.dryfish.data.repository.CartRepository
import com.rameswaram.dryfish.data.repository.ProductRepository
import com.rameswaram.dryfish.data.repository.WishlistRepository
import com.rameswaram.dryfish.domain.model.CartItem
import com.rameswaram.dryfish.domain.model.Product
import com.rameswaram.dryfish.domain.model.SKU
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val selectedSku: SKU? = null,
    val quantity: Int = 1,
    val isAddedToCart: Boolean = false,
    val isInWishlist: Boolean = false,
    val error: String? = null
)

class ProductDetailViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(slug: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = productRepository.getProductBySlug(slug)) {
                is Resource.Success -> {
                    val product = result.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        product = product,
                        selectedSku = product.skus.firstOrNull { it.isAvailable } ?: product.skus.firstOrNull()
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

    fun selectSku(sku: SKU) {
        _uiState.value = _uiState.value.copy(
            selectedSku = sku,
            quantity = 1
        )
    }

    fun incrementQuantity() {
        val current = _uiState.value.quantity
        if (current < 10) {
            _uiState.value = _uiState.value.copy(quantity = current + 1)
        }
    }

    fun decrementQuantity() {
        val current = _uiState.value.quantity
        if (current > 1) {
            _uiState.value = _uiState.value.copy(quantity = current - 1)
        }
    }

    fun addToCart(cartRepository: CartRepository) {
        val state = _uiState.value
        val product = state.product ?: return
        val sku = state.selectedSku ?: return

        viewModelScope.launch {
            val cartItem = CartItem(
                id = "${product.id}_${sku.id}",
                productId = product.id,
                productName = product.name,
                productImage = product.images.firstOrNull() ?: "",
                selectedSkuId = sku.id,
                weight = sku.weight,
                price = sku.price,
                mrp = sku.mrp,
                quantity = state.quantity,
                maxQuantity = 10,
                productSlug = product.slug
            )

            when (cartRepository.addToCart(cartItem)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isAddedToCart = true)
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }
}
