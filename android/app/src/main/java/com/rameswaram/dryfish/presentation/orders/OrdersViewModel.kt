package com.rameswaram.dryfish.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rameswaram.dryfish.data.repository.OrderRepository
import com.rameswaram.dryfish.domain.model.Order
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrdersUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val orders: List<Order> = emptyList(),
    val expandedOrderId: String? = null,
    val error: String? = null
)

class OrdersViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = orderRepository.getOrders()) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        orders = result.data
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        loadOrders()
    }

    fun toggleExpanded(orderId: String) {
        _uiState.value = _uiState.value.copy(
            expandedOrderId = if (_uiState.value.expandedOrderId == orderId) null else orderId
        )
    }

    fun syncFromCloud() {
        viewModelScope.launch {
            when (val result = orderRepository.getOrders()) {
                is Resource.Success -> {
                    if (result.data.isNotEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            orders = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    if (_uiState.value.orders.isEmpty()) {
                        _uiState.value = _uiState.value.copy(error = result.message)
                    }
                }
                else -> {}
            }
        }
    }
}
