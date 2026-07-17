package com.rameswaram.dryfish.presentation.main

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.rameswaram.dryfish.presentation.common.CustomDrawerLayout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.SubcomposeAsyncImage
import com.rameswaram.dryfish.data.repository.AuthRepository
import com.rameswaram.dryfish.data.repository.CartRepository
import com.rameswaram.dryfish.presentation.admin.AdminDashboardScreen
import com.rameswaram.dryfish.presentation.admin.AdminOrdersScreen
import com.rameswaram.dryfish.presentation.admin.AdminProductsScreen
import com.rameswaram.dryfish.presentation.admin.AdminUsersScreen
import com.rameswaram.dryfish.presentation.admin.AdminViewModel
import com.rameswaram.dryfish.presentation.auth.LoginScreen
import com.rameswaram.dryfish.presentation.cart.CartScreen
import com.rameswaram.dryfish.presentation.checkout.CheckoutScreen
import com.rameswaram.dryfish.presentation.common.AnimatedBottomNavBar
import com.rameswaram.dryfish.presentation.common.AnimatedNavItem
import com.rameswaram.dryfish.presentation.common.defaultNavItems
import com.rameswaram.dryfish.presentation.orders.OrderDetailScreen
import com.rameswaram.dryfish.presentation.orders.OrdersScreen
import com.rameswaram.dryfish.presentation.product.ProductDetailScreen
import com.rameswaram.dryfish.presentation.wishlist.WishlistScreen
import com.rameswaram.dryfish.presentation.profile.EditProfileScreen
import com.rameswaram.dryfish.presentation.profile.ProfileScreen
import com.rameswaram.dryfish.presentation.shop.ShopScreen
import com.rameswaram.dryfish.ui.theme.*
import com.rameswaram.dryfish.utils.Constants
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.sign

private val springSlide = spring<IntOffset>(
    dampingRatio = 0.75f,
    stiffness = 300f
)

private val springSlideExit = spring<IntOffset>(
    dampingRatio = 0.85f,
    stiffness = 400f
)

private fun <T> AnimatedContentTransitionScope<T>.cinematicForwardEnter(): EnterTransition =
    slideInHorizontally(
        animationSpec = springSlide,
        initialOffsetX = { it / 4 }
    ) + fadeIn(animationSpec = tween(220))

private fun <T> AnimatedContentTransitionScope<T>.cinematicForwardExit(): ExitTransition =
    slideOutHorizontally(
        animationSpec = springSlideExit,
        targetOffsetX = { -it / 4 }
    ) + fadeOut(animationSpec = tween(180))

private fun <T> AnimatedContentTransitionScope<T>.cinematicPopEnter(): EnterTransition =
    slideInHorizontally(
        animationSpec = springSlide,
        initialOffsetX = { -it / 3 }
    ) + fadeIn(animationSpec = tween(220))

private fun <T> AnimatedContentTransitionScope<T>.cinematicPopExit(): ExitTransition =
    slideOutHorizontally(
        animationSpec = springSlideExit,
        targetOffsetX = { it / 3 }
    ) + fadeOut(animationSpec = tween(180))

object NavRoutes {
    const val LOGIN = "login"
    const val SHOP = "shop"
    const val CART = "cart"
    const val ORDERS = "orders"
    const val PROFILE = "profile"
    const val PRODUCT = "product/{slug}"
    const val CHECKOUT = "checkout"
    const val WISHLIST = "wishlist"
    const val ORDER_DETAIL = "order/{orderId}"
    const val EDIT_PROFILE = "edit_profile"

    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val ADMIN_PRODUCTS = "admin_products"
    const val ADMIN_ORDERS = "admin_orders"
    const val ADMIN_USERS = "admin_users"

    const val TAB_ROUTES = "$SHOP|$CART|$ORDERS|$PROFILE"
    const val ADMIN_TAB_ROUTES = "$ADMIN_DASHBOARD|$ADMIN_PRODUCTS|$ADMIN_ORDERS|$ADMIN_USERS"

    fun productRoute(slug: String) = "product/$slug"
    fun orderDetailRoute(orderId: String) = "order/$orderId"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    authRepository: AuthRepository,
    cartRepository: CartRepository
) {
    val isLoggedIn by authRepository.isLoggedIn.collectAsState()

    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { /* isLoggedIn state change handles transition */ }
        )
    } else {
        MainAppContent(authRepository, cartRepository)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainAppContent(
    authRepository: AuthRepository,
    cartRepository: CartRepository
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val cartItemCount by cartRepository.getCartItemCount().collectAsState(initial = 0)

    LaunchedEffect(Unit) {
        delay(500)
        cartRepository.loadFromFirestore()
    }

    var isDrawerOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val user by authRepository.currentUser.collectAsState()
    val isAdmin = user?.email in Constants.ADMIN_EMAILS

    val adminViewModel: AdminViewModel = koinViewModel()

    val tabRoutes = if (isAdmin) {
        listOf(
            NavRoutes.ADMIN_DASHBOARD, NavRoutes.ADMIN_PRODUCTS,
            NavRoutes.ADMIN_ORDERS, NavRoutes.ADMIN_USERS
        )
    } else {
        listOf(
            NavRoutes.SHOP, NavRoutes.CART, NavRoutes.ORDERS, NavRoutes.PROFILE, NavRoutes.WISHLIST
        )
    }
    val showBottomBar = currentRoute in tabRoutes

    val context = LocalContext.current
    val isTamil = remember { mutableStateOf(false) }

    LaunchedEffect(currentRoute) {
        isTamil.value = context.getSharedPreferences("rameswaram_dry_fish_prefs", 0)
            .getBoolean("isTamilLanguage", false)
    }

    val adminNavItems = listOf(
        AnimatedNavItem("Dashboard", "டாஷ்போர்டு", NavRoutes.ADMIN_DASHBOARD,
            Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
        AnimatedNavItem("Products", "பொருட்கள்", NavRoutes.ADMIN_PRODUCTS,
            Icons.Filled.Inventory, Icons.Outlined.Inventory),
        AnimatedNavItem("Orders", "ஆர்டர்கள்", NavRoutes.ADMIN_ORDERS,
            Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag),
        AnimatedNavItem("Users", "பயனர்கள்", NavRoutes.ADMIN_USERS,
            Icons.Filled.People, Icons.Outlined.People)
    )

    val scaffoldContent = @Composable {
        Scaffold(
            bottomBar = {
                Box(Modifier.defaultMinSize(minHeight = 56.dp)) {
                    if (showBottomBar) {
                        if (isAdmin) {
                            AnimatedBottomNavBar(
                                navController = navController,
                                isTamil = isTamil.value,
                                items = adminNavItems
                            )
                        } else {
                            AnimatedBottomNavBar(
                                navController = navController,
                                cartItemCount = cartItemCount,
                                isTamil = isTamil.value,
                                items = defaultNavItems(cartItemCount)
                            )
                        }
                    }
                }
            }
        ) { padding ->
            val density = LocalDensity.current
            val navBarBottom = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
            val bottom = (padding.calculateBottomPadding() - navBarBottom).coerceAtLeast(0.dp)

                @OptIn(ExperimentalAnimationApi::class)
                NavHost(
                navController = navController,
                startDestination = if (isAdmin) NavRoutes.ADMIN_DASHBOARD else NavRoutes.SHOP,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = bottom),
                enterTransition = { cinematicForwardEnter<NavBackStackEntry>() },
                exitTransition = { cinematicForwardExit<NavBackStackEntry>() },
                popEnterTransition = { cinematicPopEnter<NavBackStackEntry>() },
                popExitTransition = { cinematicPopExit<NavBackStackEntry>() }
                ) {

                composable(NavRoutes.SHOP) {
                    ShopScreen(
                        onProductClick = { slug ->
                            navController.navigate(NavRoutes.productRoute(slug))
                        },
                        onCartClick = {
                            navController.navigate(NavRoutes.CART)
                        },
                        onMenuClick = {
                            isDrawerOpen = true
                        },
                        cartItemCount = cartItemCount
                    )
                }

                composable(NavRoutes.CART) {
                    CartScreen(
                        onCheckout = { navController.navigate(NavRoutes.CHECKOUT) },
                        onProductClick = { slug -> navController.navigate(NavRoutes.productRoute(slug)) },
                        onNavigateToShop = {
                            navController.navigate(NavRoutes.SHOP) {
                                popUpTo(NavRoutes.SHOP) { inclusive = true }
                            }
                        },
                        onMenuClick = {
                            isDrawerOpen = true
                        }
                    )
                }

                composable(
                    NavRoutes.PRODUCT,
                    arguments = listOf(navArgument("slug") { type = NavType.StringType })
                ) { backStackEntry ->
                    val slug = backStackEntry.arguments?.getString("slug") ?: ""
                    ProductDetailScreen(
                        slug = slug,
                        onBack = { navController.popBackStack() },
                        onAddToCart = {
                            navController.navigate(NavRoutes.CART) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onProductClick = { productSlug ->
                            navController.navigate(NavRoutes.productRoute(productSlug))
                        }
                    )
                }

                composable(NavRoutes.CHECKOUT) {
                    CheckoutScreen(
                        onBack = { navController.popBackStack() },
                        onOrderPlaced = {
                            navController.navigate(NavRoutes.ORDERS) {
                                popUpTo(NavRoutes.SHOP) { saveState = true }
                            }
                        }
                    )
                }

                composable(NavRoutes.WISHLIST) {
                    WishlistScreen(
                        onProductClick = { slug ->
                            navController.navigate(NavRoutes.productRoute(slug))
                        },
                        onMenuClick = {
                            isDrawerOpen = true
                        }
                    )
                }

                composable(NavRoutes.ORDERS) {
                    OrdersScreen(
                        onOrderClick = { orderId ->
                            navController.navigate(NavRoutes.orderDetailRoute(orderId))
                        },
                        onBack = { navController.popBackStack() },
                        onMenuClick = {
                            isDrawerOpen = true
                        }
                    )
                }

                composable(
                    NavRoutes.ORDER_DETAIL,
                    arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                    OrderDetailScreen(
                        orderId = orderId,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(NavRoutes.PROFILE) {
                    ProfileScreen(
                        onEditProfile = {
                            navController.navigate(NavRoutes.EDIT_PROFILE)
                        },
                        onViewOrder = { orderId ->
                            navController.navigate(NavRoutes.orderDetailRoute(orderId))
                        },
                        onLogout = {
                            authRepository.logout()
                        },
                        onMenuClick = {
                            isDrawerOpen = true
                        }
                    )
                }

                composable(NavRoutes.EDIT_PROFILE) {
                    val u by authRepository.currentUser.collectAsState()
                    EditProfileScreen(
                        currentName = u?.name ?: "",
                        currentPhone = u?.phone ?: "",
                        currentAvatar = u?.avatar ?: "",
                        onBack = { navController.popBackStack() },
                        onSaved = { navController.popBackStack() }
                    )
                }

                // Admin routes
                composable(NavRoutes.ADMIN_DASHBOARD) {
                    AdminDashboardScreen(
                        viewModel = adminViewModel,
                        onNavigateToProducts = { navController.navigate(NavRoutes.ADMIN_PRODUCTS) },
                        onNavigateToOrders = { navController.navigate(NavRoutes.ADMIN_ORDERS) },
                        onNavigateToUsers = { navController.navigate(NavRoutes.ADMIN_USERS) },
                        onLogout = {
                            authRepository.logout()
                        },
                        onResetData = { adminViewModel.resetAllData() }
                    )
                }

                composable(NavRoutes.ADMIN_PRODUCTS) {
                    AdminProductsScreen(
                        viewModel = adminViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(NavRoutes.ADMIN_ORDERS) {
                    AdminOrdersScreen(
                        viewModel = adminViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(NavRoutes.ADMIN_USERS) {
                    AdminUsersScreen(
                        viewModel = adminViewModel,
                        onBack = { navController.popBackStack() }
                    )
            }
        } // NavHost
    } // Scaffold
} // scaffoldContent

    CustomDrawerLayout(
        isOpen = isDrawerOpen,
        onOpenChanged = { isDrawerOpen = it },
        gesturesEnabled = true,
        drawerContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Column(Modifier.fillMaxSize()) {
                    DrawerHeader(user?.name ?: "", user?.email ?: "", user?.avatar ?: "")
                    DrawerItem(Icons.Default.Home, stringResource(com.rameswaram.dryfish.R.string.home), NavRoutes.SHOP, currentRoute == NavRoutes.SHOP) {
                        isDrawerOpen = false
                        navController.navigate(NavRoutes.SHOP) {
                            popUpTo(NavRoutes.SHOP) { inclusive = true }
                        }
                    }
                    DrawerItem(Icons.Outlined.ShoppingCart, stringResource(com.rameswaram.dryfish.R.string.cart), NavRoutes.CART, currentRoute == NavRoutes.CART) {
                        isDrawerOpen = false
                        navController.navigate(NavRoutes.CART)
                    }
                    DrawerItem(Icons.Outlined.Inventory, stringResource(com.rameswaram.dryfish.R.string.orders), NavRoutes.ORDERS, currentRoute == NavRoutes.ORDERS) {
                        isDrawerOpen = false
                        navController.navigate(NavRoutes.ORDERS)
                    }
                    DrawerItem(Icons.Outlined.Person, stringResource(com.rameswaram.dryfish.R.string.profile), NavRoutes.PROFILE, currentRoute == NavRoutes.PROFILE) {
                        isDrawerOpen = false
                        navController.navigate(NavRoutes.PROFILE)
                    }
                    DrawerItem(Icons.Outlined.FavoriteBorder, stringResource(com.rameswaram.dryfish.R.string.wishlist), NavRoutes.WISHLIST, currentRoute == NavRoutes.WISHLIST) {
                        isDrawerOpen = false
                        navController.navigate(NavRoutes.WISHLIST) {
                            launchSingleTop = true
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))
                    DrawerItem(Icons.Outlined.Logout, stringResource(com.rameswaram.dryfish.R.string.logout), null, false, tint = Color.Red.copy(alpha = 0.7f)) {
                        isDrawerOpen = false
                        authRepository.logout()
                    }
                    Spacer(Modifier.height(16.dp))
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            }
        }
    ) {
        scaffoldContent()
    }
}

@Composable
private fun DrawerHeader(name: String, email: String, avatar: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(colors = listOf(CoastalTeal, Seafoam))
            )
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (avatar.isNotEmpty()) {
                    SubcomposeAsyncImage(
                        model = avatar,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = name.ifEmpty { "User" },
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            if (email.isNotEmpty()) {
                Text(
                    text = email,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    route: String?,
    isSelected: Boolean,
    tint: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (tint != Color.Unspecified) tint else if (isSelected) CoastalTeal else Color.Gray,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = label,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (tint != Color.Unspecified) tint else if (isSelected) CoastalTeal else Color.DarkGray,
            fontSize = 15.sp
        )
        if (isSelected) {
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(CoastalTeal)
            )
        }
    }
}
