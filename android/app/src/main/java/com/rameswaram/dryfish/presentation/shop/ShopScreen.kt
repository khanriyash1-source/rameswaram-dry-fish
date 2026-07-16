package com.rameswaram.dryfish.presentation.shop

import androidx.compose.foundation.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset
import coil.compose.SubcomposeAsyncImage
import com.rameswaram.dryfish.R
import com.rameswaram.dryfish.domain.model.Product
import com.rameswaram.dryfish.presentation.common.shimmerLoadingAnimation
import com.rameswaram.dryfish.ui.theme.*
import com.rameswaram.dryfish.utils.toRupees
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    cartItemCount: Int = 0,
    viewModel: ShopViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState.isLoading && uiState.products.isEmpty()
    var searchQuery by remember { mutableStateOf("") }
    val filteredProducts = remember(searchQuery, uiState.products) {
        if (searchQuery.isBlank()) uiState.products
        else uiState.products.filter {
            it.nameEn.contains(searchQuery, ignoreCase = true) ||
            it.nameTa.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = CoastalTeal,
                shadowElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    // Main header row - Logo centered
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    ) {
                        // Menu icon - left
                        IconButton(
                            onClick = onMenuClick,
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.menu),
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Logo - centered
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .height(100.dp)
                                .width(258.dp)
                                .align(Alignment.TopCenter)
                        )

                        // Cart icon - right
                        IconButton(
                            onClick = onCartClick,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            if (cartItemCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = Color.White,
                                            contentColor = CoastalPurple
                                        ) {
                                            Text("$cartItemCount", fontSize = 10.sp)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Cart",
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }

                    // Search bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 0.dp)
                            .offset { IntOffset(0, (-12).dp.roundToPx()) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    textStyle = TextStyle(fontSize = 13.sp, color = Color.Black),
                                    decorationBox = @Composable { innerTextField ->
                                        Box(modifier = Modifier.fillMaxWidth()) {
                                            if (searchQuery.isEmpty()) {
                                                Text(
                                                    text = stringResource(R.string.search_dry_fish_products),
                                                    fontSize = 13.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                            innerTextField()
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Location bar - Chennai only
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CoastalPurple)
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.deliver_to_tamilnadu),
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Product grid
            when {
                isLoading -> {
                    ShimmerGrid()
                }
                filteredProducts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.no_products_found), color = Color.Gray, fontSize = 16.sp)
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(
                            items = filteredProducts,
                            key = { _, p -> p.id }
                        ) { index, product ->
                            ProductCard(
                                product = product,
                                isWishlisted = product.id in uiState.wishlistedIds,
                                onClick = { onProductClick(product.slug) },
                                onToggleWishlist = { viewModel.toggleWishlist(product) }
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun ProductCard(
    product: Product,
    isWishlisted: Boolean = false,
    onClick: () -> Unit,
    onToggleWishlist: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val firstSku = product.skus.firstOrNull()

    Card(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFFF0F0F0))
            ) {
                if (product.images.isNotEmpty()) {
                    SubcomposeAsyncImage(
                        model = product.images.first().replace("http://10.0.2.2:4000/images/", "file:///android_asset/images/"),
                        contentDescription = product.nameEn,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(Modifier.fillMaxSize().shimmerLoadingAnimation())
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Image, null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                    }
                }

                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isWishlisted) SunsetOrange else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.nameTa,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )
                Text(
                    text = product.nameEn,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = firstSku?.price?.toRupees() ?: "",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoastalPurple
                )
            }
        }
    }
}
 
@Composable
private fun ShimmerGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(6) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .shimmerLoadingAnimation()
            )
        }
    }
}
