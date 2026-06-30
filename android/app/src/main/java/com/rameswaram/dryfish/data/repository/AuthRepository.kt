package com.rameswaram.dryfish.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.rameswaram.dryfish.data.api.ApiService
import com.rameswaram.dryfish.domain.model.User
import com.rameswaram.dryfish.utils.Constants
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val context: Context,
    private val apiService: ApiService,
    private val firebaseAuth: FirebaseAuth,
    private val firestoreRepository: FirestoreRepository
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
        val savedUid = prefs.getString(Constants.KEY_USER_ID, null)
        val cachedName = prefs.getString(Constants.KEY_USER_NAME, "") ?: ""
        if (cachedName == "Test User") {
            prefs.edit().clear().apply()
            _isLoggedIn.value = false
        } else {
            _isLoggedIn.value = savedUid != null
            if (savedUid != null) {
                _currentUser.value = User(
                    id = savedUid,
                    name = cachedName,
                    email = prefs.getString(Constants.KEY_USER_EMAIL, "") ?: "",
                    phone = prefs.getString(Constants.KEY_USER_PHONE, null),
                    avatar = prefs.getString(Constants.KEY_USER_AVATAR, null)
                )
            }
        }
    }

    suspend fun signInWithGoogle(
        idToken: String,
        googleUserId: String? = null,
        displayName: String? = null,
        email: String? = null,
        photoUrl: String? = null
    ): Resource<User> {
        val localUserId = googleUserId ?: idToken.hashCode().toString()
        val user = User(
            id = localUserId,
            name = displayName ?: "User",
            email = email ?: "",
            phone = null,
            avatar = photoUrl
        )

        saveLocalCache(user)
        _currentUser.value = user
        _isLoggedIn.value = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.googleLogin(mapOf("token" to idToken))
                if (response.isSuccessful && response.body()?.success == true) {
                    val backendUser = response.body()?.data
                    if (backendUser != null) {
                        val merged = user.copy(
                            name = user.name.ifEmpty { backendUser.name },
                            email = user.email.ifEmpty { backendUser.email },
                            phone = backendUser.phone,
                            avatar = user.avatar ?: backendUser.avatar
                        )
                        saveLocalCache(merged)
                        _currentUser.value = merged
                    }
                }
            } catch (e: Exception) {
                Log.e("DryFishAuth", "Backend login failed: ${e.message}", e)
            }
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuth.signInWithCredential(credential).await()
                Log.d("DryFishAuth", "Firebase Auth sign-in: SUCCESS")
            } catch (e: Exception) {
                Log.e("DryFishAuth", "Firebase Auth sign-in: FAILED: ${e.message}", e)
            }
            val firestoreResult = firestoreRepository.saveUser(user)
            if (firestoreResult is Resource.Error) {
                Log.e("DryFishAuth", "Firestore saveUser: FAILED: ${firestoreResult.message}")
            } else {
                Log.d("DryFishAuth", "Firestore saveUser: SUCCESS")
            }
        }

        return Resource.Success(user)
    }

    fun logout() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
        prefs.edit().clear().apply()
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    fun saveProfile(name: String, phone: String, avatar: String) {
        prefs.edit()
            .putString(Constants.KEY_USER_NAME, name)
            .putString(Constants.KEY_USER_PHONE, phone)
            .putString(Constants.KEY_USER_AVATAR, avatar)
            .apply()
        _currentUser.value = _currentUser.value?.copy(name = name, phone = phone, avatar = avatar)
    }

    private fun saveLocalCache(user: User) {
        prefs.edit()
            .putString(Constants.KEY_USER_ID, user.id)
            .putString(Constants.KEY_USER_NAME, user.name)
            .putString(Constants.KEY_USER_EMAIL, user.email)
            .putString(Constants.KEY_USER_PHONE, user.phone)
            .putString(Constants.KEY_USER_AVATAR, user.avatar)
            .apply()
    }

    fun getSavedUserId(): String? = prefs.getString(Constants.KEY_USER_ID, null)
    fun getSavedUid(): String? = prefs.getString(Constants.KEY_USER_ID, null)
}
