package com.rameswaram.dryfish.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.rameswaram.dryfish.data.repository.AuthRepository
import com.rameswaram.dryfish.data.repository.CartRepository
import com.rameswaram.dryfish.ui.theme.RameswaramTheme
import org.koin.java.KoinJavaComponent.get

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authRepository: AuthRepository = get(AuthRepository::class.java)
        val cartRepository: CartRepository = get(CartRepository::class.java)

        setContent {
            RameswaramTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(
                        authRepository = authRepository,
                        cartRepository = cartRepository
                    )
                }
            }
        }
    }
}
