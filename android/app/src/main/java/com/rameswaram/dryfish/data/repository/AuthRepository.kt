package com.rameswaram.dryfish.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.rameswaram.dryfish.data.api.ApiService
import com.rameswaram.dryfish.domain.model.User
import com.rameswaram.dryfish.utils.Constants
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthRepository(
    private val context: Context,
    private val apiService: ApiService,
    private val firebaseAuth: FirebaseAuth
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constants.WEB_CLIENT_ID)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    init {
        val token = getToken()
        _isLoggedIn.value = !token.isNullOrEmpty()
    }

    suspend fun signInWithGoogle(idToken: String): Resource<User> {
        return try {
            val response = apiService.googleLogin(mapOf("token" to idToken))

            if (response.isSuccessful && response.body()?.success == true) {
                val user = response.body()!!.data!!
                saveToken(user.id)
                saveUserInfo(user)
                _currentUser.value = user
                _isLoggedIn.value = true
                Resource.Success(user)
            } else {
                Resource.Error(response.body()?.message ?: "Server login failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Google sign-in failed")
        }
    }

    suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val response = apiService.login(mapOf("email" to email, "password" to password))
            if (response.isSuccessful && response.body()?.success == true) {
                val user = response.body()!!.data!!
                saveToken(user.id)
                saveUserInfo(user)
                _currentUser.value = user
                _isLoggedIn.value = true
                Resource.Success(user)
            } else {
                Resource.Error(response.body()?.message ?: "Login failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }

    suspend fun register(name: String, email: String, password: String): Resource<User> {
        return try {
            val response = apiService.register(
                mapOf("name" to name, "email" to email, "password" to password)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                val user = response.body()!!.data!!
                saveToken(user.id)
                saveUserInfo(user)
                _currentUser.value = user
                _isLoggedIn.value = true
                Resource.Success(user)
            } else {
                Resource.Error(response.body()?.message ?: "Registration failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed")
        }
    }

    fun logout() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
        prefs.edit().clear().apply()
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    fun saveToken(token: String) {
        prefs.edit().putString(Constants.KEY_AUTH_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(Constants.KEY_AUTH_TOKEN, null)
    }

    private fun saveUserInfo(user: User) {
        prefs.edit()
            .putString(Constants.KEY_USER_ID, user.id)
            .putString(Constants.KEY_USER_NAME, user.name)
            .putString(Constants.KEY_USER_EMAIL, user.email)
            .apply()
    }

    fun getSavedUserName(): String? = prefs.getString(Constants.KEY_USER_NAME, null)
    fun getSavedUserEmail(): String? = prefs.getString(Constants.KEY_USER_EMAIL, null)
    fun getSavedUserId(): String? = prefs.getString(Constants.KEY_USER_ID, null)
}
