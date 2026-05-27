package com.rameswaram.dryfish.presentation.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rameswaram.dryfish.data.repository.CartRepository
import com.rameswaram.dryfish.data.repository.OrderRepository
import com.rameswaram.dryfish.data.repository.AuthRepository
import com.rameswaram.dryfish.domain.model.Address
import com.rameswaram.dryfish.domain.model.Order
import com.rameswaram.dryfish.domain.model.PaymentMethod
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CheckoutUiState(
    val isLoading: Boolean = false,
    val isPlacingOrder: Boolean = false,
    val addresses: List<Address> = emptyList(),
    val selectedAddress: Address? = null,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.COD,
    val subtotal: Double = 0.0,
    val deliveryCharge: Double = 40.0,
    val discount: Double = 0.0,
    val orderPlaced: Order? = null,
    val showAddAddressSheet: Boolean = false,
    val error: String? = null
) {
    val total: Double get() = subtotal + deliveryCharge - discount
}

class CheckoutViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        loadCheckoutData()
    }

    private fun loadCheckoutData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            cartRepository.getCartTotal().collect { total ->
                _uiState.value = _uiState.value.copy(
                    subtotal = total,
                    deliveryCharge = if (total >= 499.0) 0.0 else 40.0,
                    isLoading = false
                )
            }
        }
    }

    fun selectAddress(address: Address) {
        _uiState.value = _uiState.value.copy(selectedAddress = address)
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        _uiState.value = _uiState.value.copy(selectedPaymentMethod = method)
    }

    fun showAddAddress() {
        _uiState.value = _uiState.value.copy(showAddAddressSheet = true)
    }

    fun hideAddAddress() {
        _uiState.value = _uiState.value.copy(showAddAddressSheet = false)
    }

    fun addAddress(address: Address) {
        _uiState.value = _uiState.value.copy(
            addresses = _uiState.value.addresses + address,
            selectedAddress = address,
            showAddAddressSheet = false
        )
    }

    fun placeOrder(paymentCallback: (String) -> Unit = {}) {
        val state = _uiState.value
        val address = state.selectedAddress ?: run {
            _uiState.value = _uiState.value.copy(error = "Please select a shipping address")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPlacingOrder = true, error = null)

            val orderData = mapOf<String, Any>(
                "addressId" to address.id,
                "paymentMethod" to state.selectedPaymentMethod.name,
                "subtotal" to state.subtotal,
                "deliveryCharge" to state.deliveryCharge,
                "discount" to state.discount,
                "total" to state.total
            )

            when (val result = orderRepository.createOrder(orderData)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isPlacingOrder = false,
                        orderPlaced = result.data
                    )
                    cartRepository.clearCart()

                    if (state.selectedPaymentMethod == PaymentMethod.RAZORPAY) {
                        paymentCallback(result.data.id)
                    }
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isPlacingOrder = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
