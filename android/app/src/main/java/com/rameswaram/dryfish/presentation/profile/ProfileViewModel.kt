package com.rameswaram.dryfish.presentation.profile

import androidx.lifecycle.ViewModel
import com.rameswaram.dryfish.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
    val userName: String? = null,
    val userEmail: String? = null,
    val isLoggedIn: Boolean = false,
    val isDarkMode: Boolean = false,
    val isTamilLanguage: Boolean = false
)

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = ProfileUiState(
            userName = authRepository.getSavedUserName(),
            userEmail = authRepository.getSavedUserEmail(),
            isLoggedIn = authRepository.isLoggedIn.value
        )
    }

    fun toggleDarkMode() {
        _uiState.value = _uiState.value.copy(isDarkMode = !_uiState.value.isDarkMode)
    }

    fun toggleLanguage() {
        _uiState.value = _uiState.value.copy(isTamilLanguage = !_uiState.value.isTamilLanguage)
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = ProfileUiState()
    }
}
