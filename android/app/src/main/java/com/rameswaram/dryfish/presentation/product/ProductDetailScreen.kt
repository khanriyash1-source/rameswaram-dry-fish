package com.rameswaram.dryfish.presentation.product

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.pager.*
import com.rameswaram.dryfish.domain.model.SKU
import com.rameswaram.dryfish.presentation.common.*
import com.rameswaram.dryfish.ui.theme.*
import com.rameswaram.dryfish.utils.toRupees
import org.koin.androidx.compose.koinViewModel
import org.koin.java.KoinJavaComponent.get

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProductDetailScreen(
    slug: String,
    onBack: () -> Unit,
    onAddToCart: () -> Unit,
    viewModel: ProductDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(slug) {
        viewModel.loadProduct(slug)
    }

    LaunchedEffect(uiState.isAddedToCart) {
        if (uiState.isAddedToCart) {
            snackbarHostState.showSnackbar(
                message = "Added to cart!",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(uiState.product?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (uiState.isInWishlist) SunsetOrange else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (uiState.product != null) {
                Surface(
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.addToCart(get(com.rameswaram.dryfish.data.repository.CartRepository::class.java))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, OceanBlue)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = OceanBlue)
                            Spacer(Modifier.width(8.dp))
                            Text("Add to Cart", color = OceanBlue)
                        }

                        Button(
                            onClick = {
                                viewModel.addToCart(get(com.rameswaram.dryfish.data.repository.CartRepository::class.java))
                                onAddToCart()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Buy Now")
                        }
                    }
                }
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingScreen(modifier = Modifier.padding(padding))
            uiState.error != null -> ErrorScreen(
                message = uiState.error ?: "",
                onRetry = { viewModel.loadProduct(slug) },
                modifier = Modifier.padding(padding)
            )
            uiState.product != null -> {
                val product = uiState.product!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Image Pager
                    if (product.images.isNotEmpty()) {
                        val pagerState = rememberPagerState(
                            pageCount = { product.images.size }
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                            ) { page ->
                                AsyncImage(
                                    model = product.images[page],
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Row(
                                Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                repeat(pagerState.pageCount) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (pagerState.currentPage == index) OceanBlue else Color.Gray.copy(alpha = 0.5f))
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        // Name
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        if (!product.nameTamil.isNullOrEmpty()) {
                            Text(
                                text = product.nameTamil,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Rating
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StarRating(rating = product.rating, reviewCount = product.reviewCount)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Weight Selector
                        Text(
                            text = "Select Weight",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(product.skus) { sku ->
                                WeightChip(
                                    sku = sku,
                                    isSelected = uiState.selectedSku?.id == sku.id,
                                    onClick = { viewModel.selectSku(sku) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Price
                        val selectedSku = uiState.selectedSku
                        if (selectedSku != null) {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = selectedSku.price.toRupees(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OceanBlue
                                )

                                if (selectedSku.mrp > selectedSku.price) {
                                    Text(
                                        text = selectedSku.mrp.toRupees(),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textDecoration = TextDecoration.LineThrough
                                    )

                                    Text(
                                        text = "${selectedSku.discountPercent}% off",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = SunsetOrange,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quantity Selector
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Quantity",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            QuantityStepper(
                                quantity = uiState.quantity,
                                onIncrement = { viewModel.incrementQuantity() },
                                onDecrement = { viewModel.decrementQuantity() }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Description
                        if (!product.shortDesc.isNullOrBlank() || !product.description.isNullOrBlank()) {
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = product.description ?: product.shortDesc ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Reviews Section
                        if (product.reviewCount > 0) {
                            Text(
                                text = "Reviews (${product.reviewCount})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = product.rating.toString(),
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SunsetOrange
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    StarRating(rating = product.rating, showCount = false)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${product.reviewCount} reviews",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun WeightChip(
    sku: SKU,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .widthIn(min = 80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        border = if (isSelected) BorderStroke(2.dp, OceanBlue) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) OceanBlueLight else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = sku.weight,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) OceanBlue else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = sku.price.toRupees(),
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) OceanBlue else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
