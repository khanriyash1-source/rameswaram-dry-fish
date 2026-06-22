package com.rameswaram.dryfish.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rameswaram.dryfish.data.repository.CartRepository
import com.rameswaram.dryfish.domain.model.CartItem
import com.rameswaram.dryfish.utils.Constants
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val itemCount: Int = 0,
    val deliveryCharge: Double = Constants.DELIVERY_CHARGE,
    val isFreeDelivery: Boolean = false,
    val couponCode: String = "",
    val discount: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val total: Double get() = subtotal + deliveryCharge - discount
    val savings: Double get() = items.sumOf { it.savings }
}

class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            cartRepository.getCartItems().collect { items ->
                val subtotal = items.sumOf { it.subtotal }
                val isFreeDelivery = subtotal >= Constants.FREE_DELIVERY_MIN
                _uiState.value = _uiState.value.copy(
                    items = items,
                    subtotal = subtotal,
                    itemCount = items.size,
                    isFreeDelivery = isFreeDelivery,
                    deliveryCharge = if (isFreeDelivery) 0.0 else Constants.DELIVERY_CHARGE
                )
            }
        }
    }

    fun updateQuantity(cartItemId: String, quantity: Int) {
        viewModelScope.launch {
            when (cartRepository.updateQuantity(cartItemId, quantity)) {
                is Resource.Success -> {}
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(error = "Failed to update quantity")
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun removeItem(cartItemId: String) {
        viewModelScope.launch {
            when (cartRepository.removeFromCart(cartItemId)) {
                is Resource.Success -> {}
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(error = "Failed to remove item")
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            when (cartRepository.clearCart()) {
                is Resource.Success -> {}
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(error = "Failed to clear cart")
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun updateCoupon(code: String) {
        _uiState.value = _uiState.value.copy(couponCode = code)
    }

    fun applyCoupon() {
        if (_uiState.value.couponCode.isNotBlank()) {
            _uiState.value = _uiState.value.copy(discount = 50.0)
        }
    }

    fun syncFromCloud() {
        viewModelScope.launch {
            when (val result = cartRepository.loadFromFirestore()) {
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                else -> {}
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
