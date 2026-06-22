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
    val relatedProducts: List<Product> = emptyList(),
    val reviews: List<ProductReview> = emptyList(),
    val error: String? = null
)

data class ProductReview(
    val id: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val date: String,
    val verified: Boolean = false
)

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val wishlistRepository: WishlistRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(slug: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = productRepository.getProductBySlug(slug)) {
                is Resource.Success -> {
                    val product = result.data
                    // Load related products (products from same category, excluding current)
                    val related = productRepository.getProducts().let { productsResult ->
                        when (productsResult) {
                            is Resource.Success -> {
                    productsResult.data
                        .filter { it.id != product.id && it.category == product.category }
                        .take(5)
                            }
                            else -> emptyList()
                        }
                    }

                    // Check if product is in wishlist
                    var inWishlist = false
                    when (val wl = wishlistRepository.getWishlist()) {
                        is Resource.Success -> inWishlist = wl.data.any { it.productId == product.id }
                        else -> {}
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        product = product,
                        selectedSku = product.skus.firstOrNull { it.isAvailable } ?: product.skus.firstOrNull(),
                        relatedProducts = related,
                        isInWishlist = inWishlist
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

    fun addToCart() {
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

            when (val addResult = cartRepository.addToCart(cartItem)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isAddedToCart = true)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(error = addResult.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun toggleWishlist() {
        val product = _uiState.value.product ?: return
        val sku = _uiState.value.selectedSku ?: product.skus.firstOrNull() ?: return

        viewModelScope.launch {
            val current = _uiState.value.isInWishlist
            _uiState.value = _uiState.value.copy(isInWishlist = !current)
            val result = if (current) {
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
                _uiState.value = _uiState.value.copy(isInWishlist = current)
            }
        }
    }
    
    fun submitReview(rating: Int, comment: String) {
        val product = _uiState.value.product ?: return
        
        viewModelScope.launch {
            // TODO: Implement actual API call to submit review
            val newReview = ProductReview(
                id = System.currentTimeMillis().toString(),
                userName = "You",
                rating = rating,
                comment = comment,
                date = "Just now",
                verified = true
            )
            
            _uiState.value = _uiState.value.copy(
                reviews = listOf(newReview) + _uiState.value.reviews
            )
        }
    }
    
    fun resetAddedToCart() {
        _uiState.value = _uiState.value.copy(isAddedToCart = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}