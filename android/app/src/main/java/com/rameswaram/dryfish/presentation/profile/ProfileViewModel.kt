package com.rameswaram.dryfish.presentation.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rameswaram.dryfish.data.repository.AuthRepository
import com.rameswaram.dryfish.data.repository.FirestoreRepository
import com.rameswaram.dryfish.utils.Constants
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userAvatar: String = "",
    val isLoggedIn: Boolean = false,
    val isTamilLanguage: Boolean = false,
    val ordersCount: Int = 0,
    val totalSpent: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(
    private val context: Context,
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadFromCache()
        loadFromFirestore()
    }

    private fun loadFromCache() {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val uid = prefs.getString(Constants.KEY_USER_ID, null)
        if (uid != null) {
            _uiState.value = ProfileUiState(
                userName = prefs.getString(Constants.KEY_USER_NAME, "") ?: "",
                userEmail = prefs.getString(Constants.KEY_USER_EMAIL, "") ?: "",
                userPhone = prefs.getString(Constants.KEY_USER_PHONE, "") ?: "",
                userAvatar = prefs.getString(Constants.KEY_USER_AVATAR, "") ?: "",
                isLoggedIn = true,
                isTamilLanguage = prefs.getBoolean("isTamilLanguage", false),
                isLoading = false
            )
        }
    }

    private fun loadFromFirestore() {
        viewModelScope.launch {
            val uid = authRepository.getSavedUid() ?: return@launch

            // Load user profile fields (name, email, phone, avatar, language)
            when (val result = firestoreRepository.getUser(uid)) {
                is Resource.Success -> {
                    val user = result.data
                    if (user != null) {
                        _uiState.value = _uiState.value.copy(
                            userName = user.name,
                            userEmail = user.email,
                            userPhone = user.phone ?: "",
                            userAvatar = user.avatar ?: "",
                            isTamilLanguage = user.isTamilLanguage
                        )
                    }
                }
                else -> {}
            }

            // Load order count and total spent from actual orders (source of truth)
            when (val summary = firestoreRepository.getOrderSpendingSummary(uid)) {
                is Resource.Success -> {
                    val (count, total) = summary.data
                    _uiState.value = _uiState.value.copy(
                        ordersCount = count,
                        totalSpent = total
                    )
                }
                else -> {}
            }
        }
    }

    fun reload() {
        loadFromFirestore()
    }

    fun toggleLanguage() {
        val newValue = !_uiState.value.isTamilLanguage
        _uiState.value = _uiState.value.copy(isTamilLanguage = newValue)
    }

    fun syncLanguageToFirestore() {
        viewModelScope.launch {
            val uid = authRepository.getSavedUid() ?: return@launch
            firestoreRepository.updateLanguage(uid, _uiState.value.isTamilLanguage)
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = ProfileUiState()
    }
}
