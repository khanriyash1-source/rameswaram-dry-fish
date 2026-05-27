package com.rameswaram.dryfish.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rameswaram.dryfish.data.repository.AuthRepository
import com.rameswaram.dryfish.data.repository.CartRepository
import com.rameswaram.dryfish.presentation.auth.LoginScreen
import com.rameswaram.dryfish.presentation.cart.CartScreen
import com.rameswaram.dryfish.presentation.checkout.CheckoutScreen
import com.rameswaram.dryfish.presentation.common.BottomNavBar
import com.rameswaram.dryfish.presentation.orders.OrdersScreen
import com.rameswaram.dryfish.presentation.product.ProductDetailScreen
import com.rameswaram.dryfish.presentation.shop.ShopScreen
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MainScreen(
    authRepository: AuthRepository,
    cartRepository: CartRepository
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isLoggedIn by authRepository.isLoggedIn.collectAsState()
    val cartItemCount by cartRepository.getCartItemCount().collectAsState(initial = 0)

    val showBottomBar = currentRoute in listOf("shop", "cart", "orders")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    navController = navController,
                    cartItemCount = cartItemCount
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "shop" else "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("shop") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("shop") {
                ShopScreen(
                    onProductClick = { slug -> navController.navigate("product/$slug") }
                )
            }

            composable("cart") {
                CartScreen(
                    onCheckout = { navController.navigate("checkout") },
                    onProductClick = { slug -> navController.navigate("product/$slug") }
                )
            }

            composable(
                "product/{slug}",
                arguments = listOf(navArgument("slug") { type = NavType.StringType })
            ) { backStackEntry ->
                val slug = backStackEntry.arguments?.getString("slug") ?: ""
                ProductDetailScreen(
                    slug = slug,
                    onBack = { navController.popBackStack() },
                    onAddToCart = { navController.navigate("cart") }
                )
            }

            composable("checkout") {
                CheckoutScreen(
                    onBack = { navController.popBackStack() },
                    onOrderPlaced = { orderId ->
                        navController.navigate("orders") {
                            popUpTo("shop") { saveState = true }
                        }
                    }
                )
            }

            composable("orders") {
                OrdersScreen()
            }
        }
    }
}
