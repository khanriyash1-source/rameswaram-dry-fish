package com.rameswaram.dryfish.presentation.checkout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rameswaram.dryfish.data.repository.AuthRepository
import com.rameswaram.dryfish.data.repository.CartRepository
import com.rameswaram.dryfish.data.repository.FirestoreRepository
import com.rameswaram.dryfish.data.repository.OrderRepository
import com.rameswaram.dryfish.data.repository.RazorpayEvent
import com.rameswaram.dryfish.data.repository.RazorpayPaymentBus
import com.rameswaram.dryfish.domain.model.Address
import com.rameswaram.dryfish.domain.model.CartItem
import com.rameswaram.dryfish.domain.model.Order
import com.rameswaram.dryfish.domain.model.OrderItem
import com.rameswaram.dryfish.domain.model.OrderStatus
import com.rameswaram.dryfish.domain.model.OrderTimeline
import com.rameswaram.dryfish.domain.model.PaymentMethod
import com.rameswaram.dryfish.domain.model.PaymentVerificationRequest
import com.rameswaram.dryfish.domain.model.RazorpayOrderResponse
import com.rameswaram.dryfish.utils.Constants
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.*

data class CheckoutUiState(
    val isLoading: Boolean = false,
    val isPlacingOrder: Boolean = false,
    val addresses: List<Address> = emptyList(),
    val selectedAddress: Address? = null,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.RAZORPAY,
    val subtotal: Double = 0.0,
    val deliveryCharge: Double = Constants.DELIVERY_CHARGE,
    val discount: Double = 0.0,
    val cartItems: List<CartItem> = emptyList(),
    val orderPlaced: Order? = null,
    val showAddAddressSheet: Boolean = false,
    val showPaymentDialog: Boolean = false,
    val pendingBackendOrder: Order? = null,
    val pendingRazorpayOrder: RazorpayOrderResponse? = null,
    val error: String? = null,
    val paymentStep: String = ""
) {
    val total: Double get() = subtotal + deliveryCharge - discount
    val isFreeDelivery: Boolean get() = subtotal >= Constants.FREE_DELIVERY_MIN
}

class CheckoutViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        loadCheckoutData()
    }

    private fun loadCheckoutData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Load cart items (first emission only, then proceed)
            val items = cartRepository.getCartItems().first()
            val total = items.sumOf { it.subtotal }
            _uiState.value = _uiState.value.copy(
                cartItems = items,
                subtotal = total,
                deliveryCharge = if (total >= Constants.FREE_DELIVERY_MIN) 0.0 else Constants.DELIVERY_CHARGE,
            )

            // Load saved addresses from Firestore
            val uid = authRepository.getSavedUid()
            if (uid != null) {
                when (val result = firestoreRepository.loadAddresses(uid)) {
                    is Resource.Success -> {
                        val addresses = result.data
                        _uiState.value = _uiState.value.copy(
                            addresses = addresses,
                            selectedAddress = addresses.find { it.isDefault } ?: addresses.firstOrNull(),
                            isLoading = false
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                    else -> {}
                }
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
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

    fun updateAddress(address: Address) {
        val updatedList = _uiState.value.addresses.map { if (it.id == address.id) address else it }
        _uiState.value = _uiState.value.copy(
            addresses = updatedList,
            selectedAddress = address,
            showAddAddressSheet = false
        )
        viewModelScope.launch {
            val uid = authRepository.getSavedUid() ?: return@launch
            firestoreRepository.saveAddresses(uid, updatedList)
        }
    }

    fun addAddress(address: Address) {
        val updatedList = _uiState.value.addresses + address
        _uiState.value = _uiState.value.copy(
            addresses = updatedList,
            selectedAddress = address,
            showAddAddressSheet = false
        )
        viewModelScope.launch {
            val uid = authRepository.getSavedUid() ?: return@launch
            firestoreRepository.saveAddresses(uid, updatedList)
        }
    }

    private var paymentJob: Job? = null

    // Step 1: Slide to pay — builds local order and shows payment dialog
    fun placeOrder() {
        val state = _uiState.value
        val address = state.selectedAddress ?: run {
            _uiState.value = _uiState.value.copy(error = "Please select a shipping address")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPlacingOrder = true, error = null, paymentStep = "Creating order...")

            val uid = authRepository.getSavedUid() ?: run {
                _uiState.value = _uiState.value.copy(isPlacingOrder = false, error = "Not logged in")
                return@launch
            }

            val orderItems = state.cartItems.map { cartItem ->
                OrderItem(
                    productId = cartItem.productId,
                    productName = cartItem.productName,
                    productImage = cartItem.productImage,
                    weight = cartItem.weight,
                    quantity = cartItem.quantity,
                    price = cartItem.price
                )
            }

            val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(Date())

            val localOrder = Order(
                id = "local_${System.currentTimeMillis()}",
                userId = uid,
                orderNumber = "",
                items = orderItems,
                status = OrderStatus.PENDING,
                timeline = listOf(OrderTimeline(OrderStatus.PENDING, now, "Order placed successfully")),
                shippingAddress = address,
                paymentMethod = state.selectedPaymentMethod,
                paymentStatus = "PENDING",
                subtotal = state.subtotal,
                deliveryCharge = state.deliveryCharge,
                discount = state.discount,
                total = state.total,
                couponApplied = null,
                notes = null,
                createdAt = now,
                updatedAt = now,
                estimatedDelivery = null
            )

            _uiState.value = _uiState.value.copy(paymentStep = "Initiating payment...")
            val totalPaise = state.total.toLong()

            when (val razorpayResult = orderRepository.createRazorpayOrder(totalPaise)) {
                is Resource.Success -> {
                    val razorpayOrder = razorpayResult.data
                    _uiState.value = _uiState.value.copy(
                        isPlacingOrder = false,
                        showPaymentDialog = true,
                        pendingBackendOrder = localOrder,
                        pendingRazorpayOrder = razorpayOrder,
                        paymentStep = ""
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isPlacingOrder = false,
                        paymentStep = "",
                        error = razorpayResult.message
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    // Step 2: User taps "Pay" — opens Razorpay SDK and waits for result
    fun confirmPayment(): RazorpayOrderResponse? {
        // Prevent double-payment: if a payment listener is already active, ignore
        if (paymentJob?.isActive == true) return null

        val state = _uiState.value
        val razorpayOrder = state.pendingRazorpayOrder ?: return null

        _uiState.value = _uiState.value.copy(
            showPaymentDialog = false,
            isPlacingOrder = true,
            paymentStep = ""
        )

        paymentJob?.cancel()
        RazorpayPaymentBus.clear()
        paymentJob = viewModelScope.launch {
            val event = try {
                withTimeout(30_000L) { RazorpayPaymentBus.events.first() }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                RazorpayEvent.Error(-1, "Payment timed out")
            }
            when (event) {
                is RazorpayEvent.Success -> onPaymentSuccess(
                    orderId = razorpayOrder.orderId,
                    paymentId = event.paymentId,
                    backendOrder = state.pendingBackendOrder
                )
                is RazorpayEvent.Error -> onPaymentError(event)
            }
        }

        return razorpayOrder
    }

    private fun onPaymentSuccess(orderId: String, paymentId: String, backendOrder: Order?) {
        Log.d("DryFishCheckout", "onPaymentSuccess: orderId=$orderId paymentId=$paymentId")
        if (paymentId.isBlank()) {
            Log.e("DryFishCheckout", "onPaymentSuccess: EMPTY paymentId — rejecting")
            _uiState.value = _uiState.value.copy(
                isPlacingOrder = false,
                paymentStep = "",
                pendingBackendOrder = null,
                pendingRazorpayOrder = null,
                error = "Payment callback missing payment ID. Please try again."
            )
            return
        }
        viewModelScope.launch {
            doVerifyAndFinalize(orderId, paymentId, backendOrder)
        }
    }

    private suspend fun doVerifyAndFinalize(orderId: String, paymentId: String, backendOrder: Order?) {
        // Retry with delay — Razorpay UPI status can be 'pending' immediately after
        // the UPI app shows a result. Wait up to 15 seconds for the status to settle.
        var attempt = 0
        val maxAttempts = 5
        var verifiedPaymentId: String? = null

        while (attempt < maxAttempts && verifiedPaymentId == null) {
            attempt++
            Log.d("DryFishCheckout", "verify attempt $attempt/$maxAttempts")
            val verificationRequest = PaymentVerificationRequest(
                razorpay_order_id = orderId,
                razorpay_payment_id = paymentId,
                razorpay_signature = ""
            )

            when (val result = orderRepository.verifyPayment(verificationRequest)) {
                is Resource.Success -> {
                    verifiedPaymentId = result.data
                    Log.d("DryFishCheckout", "verifyPayment SUCCESS on attempt $attempt")
                }
                is Resource.Error -> {
                    if (result.message.contains("pending", ignoreCase = true)) {
                        Log.d("DryFishCheckout", "verifyPayment pending — retrying in 3s")
                        kotlinx.coroutines.delay(3000L)
                    } else {
                        Log.e("DryFishCheckout", "verifyPayment FAILED: ${result.message}")
                        _uiState.value = _uiState.value.copy(
                            isPlacingOrder = false,
                            paymentStep = "",
                            error = "Payment verification failed: ${result.message}"
                        )
                        return
                    }
                }
                is Resource.Loading -> {}
            }
        }

        if (verifiedPaymentId == null) {
            _uiState.value = _uiState.value.copy(
                isPlacingOrder = false,
                paymentStep = "",
                error = "Payment could not be verified after multiple attempts. Please try again."
            )
            return
        }

        val enrichedOrder = backendOrder?.copy(
            items = uiState.value.cartItems.map { cartItem ->
                OrderItem(
                    productId = cartItem.productId,
                    productName = cartItem.productName,
                    productImage = cartItem.productImage,
                    weight = cartItem.weight,
                    quantity = cartItem.quantity,
                    price = cartItem.price
                )
            },
            shippingAddress = uiState.value.selectedAddress ?: backendOrder.shippingAddress,
            paymentStatus = "PAID",
            paymentId = verifiedPaymentId
        )

        if (enrichedOrder == null) {
            _uiState.value = _uiState.value.copy(
                isPlacingOrder = false,
                paymentStep = "",
                error = "Order data missing. Please try again."
            )
            return
        }

        when (orderRepository.finalizeOrder(enrichedOrder)) {
            is Resource.Success -> {
                cartRepository.clearCart()
                _uiState.value = _uiState.value.copy(
                    isPlacingOrder = false,
                    paymentStep = "",
                    pendingBackendOrder = null,
                    pendingRazorpayOrder = null,
                    orderPlaced = enrichedOrder
                )
            }
            is Resource.Error -> {
                Log.e("DryFishCheckout", "finalizeOrder FAILED")
                _uiState.value = _uiState.value.copy(
                    isPlacingOrder = false,
                    paymentStep = "",
                    error = "Failed to save order. Please contact support."
                )
            }
            is Resource.Loading -> {}
        }
    }

    private fun onPaymentError(event: RazorpayEvent.Error) {
        Log.e("DryFishCheckout", "onPaymentError: code=${event.code} response=${event.response}")
        // NEVER try to recover from an error — Razorpay SDK callbacks are unreliable.
        // If the payment was actually successful, Razorpay would have fired onPaymentSuccess.
        showPaymentError(event)
    }

    private fun showPaymentError(event: RazorpayEvent.Error) {
        val message = when (event.code) {
            0 -> "Network error. Please check your internet and try again."
            -1, 2 -> "Payment cancelled."
            1 -> "Payment failed. Please try again."
            4 -> "Payment timeout. Please try again."
            else -> event.response
                .replace("[", "").replace("]", "")
                .replace("\"", "")
                .take(120)
        }
        _uiState.value = _uiState.value.copy(
            isPlacingOrder = false,
            paymentStep = "",
            pendingBackendOrder = null,
            pendingRazorpayOrder = null,
            error = message
        )
    }

    fun dismissPaymentDialog() {
        _uiState.value = _uiState.value.copy(
            showPaymentDialog = false,
            pendingBackendOrder = null,
            pendingRazorpayOrder = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            error = null,
            pendingBackendOrder = null,
            pendingRazorpayOrder = null
        )
    }

    override fun onCleared() {
        super.onCleared()
        paymentJob?.cancel()
    }
}
