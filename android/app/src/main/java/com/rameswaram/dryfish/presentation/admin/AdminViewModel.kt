package com.rameswaram.dryfish.presentation.admin

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rameswaram.dryfish.data.repository.AdminRepository
import com.rameswaram.dryfish.data.repository.DashboardStats
import com.rameswaram.dryfish.domain.model.Order
import com.rameswaram.dryfish.domain.model.OrderStatus
import com.rameswaram.dryfish.domain.model.Product
import com.rameswaram.dryfish.domain.model.User
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminUiState(
    val isLoading: Boolean = false,
    val stats: DashboardStats? = null,
    val products: List<Product> = emptyList(),
    val orders: List<Order> = emptyList(),
    val users: List<User> = emptyList(),
    val error: String? = null
)

class AdminViewModel(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    val products get() = _uiState.value.products
    val orders get() = _uiState.value.orders
    val users get() = _uiState.value.users

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = adminRepository.getDashboardStats()) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    stats = result.data, isLoading = false
                )
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    error = result.message, isLoading = false
                )
                else -> {}
            }
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = adminRepository.getProducts()) {
                is Resource.Success -> {
                    Log.d("AdminViewModel", "loadProducts: ${result.data.size} products loaded")
                    _uiState.value = _uiState.value.copy(
                        products = result.data, isLoading = false
                    )
                }
                is Resource.Error -> {
                    Log.e("AdminViewModel", "loadProducts: FAILED: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        error = result.message, isLoading = false
                    )
                }
                else -> {}
            }
        }
    }

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = adminRepository.saveProduct(product)) {
                is Resource.Success -> {
                    loadProducts()
                    loadDashboard()
                }
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    error = result.message, isLoading = false
                )
                else -> {}
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = adminRepository.deleteProduct(productId)) {
                is Resource.Success -> {
                    loadProducts()
                    loadDashboard()
                }
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    error = result.message, isLoading = false
                )
                else -> {}
            }
        }
    }

    fun uploadImage(uri: Uri, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(error = null)
            when (val result = adminRepository.uploadImage(uri)) {
                is Resource.Success -> onResult(result.data)
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                    onResult(null)
                }
                else -> onResult(null)
            }
        }
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = adminRepository.getAllOrders()) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    orders = result.data, isLoading = false
                )
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    error = result.message, isLoading = false
                )
                else -> {}
            }
        }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = adminRepository.updateOrderStatus(orderId, status)) {
                is Resource.Success -> {
                    loadOrders()
                    loadDashboard()
                }
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    error = result.message, isLoading = false
                )
                else -> {}
            }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = adminRepository.getAllUsers()) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    users = result.data, isLoading = false
                )
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    error = result.message, isLoading = false
                )
                else -> {}
            }
        }
    }

    fun syncProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            Log.d("AdminViewModel", "syncProducts: starting...")
            when (val result = adminRepository.syncProductsFromAssets()) {
                is Resource.Success -> {
                    Log.d("AdminViewModel", "syncProducts: synced ${result.data} products, now loading...")
                    loadProducts()
                    loadDashboard()
                }
                is Resource.Error -> {
                    Log.e("AdminViewModel", "syncProducts: FAILED: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        error = result.message, isLoading = false
                    )
                }
                else -> {
                    Log.w("AdminViewModel", "syncProducts: unexpected result")
                }
            }
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            Log.d("AdminViewModel", "resetAllData: starting...")
            when (val result = adminRepository.resetAllData()) {
                is Resource.Success -> {
                    Log.d("AdminViewModel", "resetAllData: ${result.data}")
                    loadDashboard()
                }
                is Resource.Error -> {
                    Log.e("AdminViewModel", "resetAllData: FAILED: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        error = result.message, isLoading = false
                    )
                }
                else -> {}
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
