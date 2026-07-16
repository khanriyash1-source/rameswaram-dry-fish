package com.rameswaram.dryfish.presentation.main

import android.content.Context
import android.content.res.Configuration
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
import com.rameswaram.dryfish.data.repository.RazorpayEvent
import com.rameswaram.dryfish.data.repository.RazorpayPaymentBus
import com.rameswaram.dryfish.ui.theme.RameswaramTheme
import com.rameswaram.dryfish.utils.Constants
import com.razorpay.PaymentResultListener
import org.koin.java.KoinJavaComponent.get
import java.util.Locale

class MainActivity : ComponentActivity(), PaymentResultListener {

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val isTamil = prefs.getBoolean("isTamilLanguage", false)
        val locale = if (isTamil) Locale("ta") else Locale("en")
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        val authRepository: AuthRepository = get(AuthRepository::class.java)
        val cartRepository: CartRepository = get(CartRepository::class.java)

        setContent {
            RameswaramTheme(darkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(
                        authRepository = authRepository,
                        cartRepository = cartRepository
                    )
                }
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        RazorpayPaymentBus.emit(RazorpayEvent.Success(
            paymentId = razorpayPaymentId ?: ""
        ))
    }

    override fun onPaymentError(code: Int, response: String?) {
        RazorpayPaymentBus.emit(RazorpayEvent.Error(
            code = code,
            response = response ?: "Unknown error"
        ))
    }
}
