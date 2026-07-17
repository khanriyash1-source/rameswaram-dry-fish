package com.rameswaram.dryfish.presentation.product

import android.content.Context
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.rameswaram.dryfish.domain.model.Product
import com.rameswaram.dryfish.domain.model.SKU
import com.rameswaram.dryfish.presentation.common.StarRating
import com.rameswaram.dryfish.presentation.common.shimmerLoadingAnimation
import com.rameswaram.dryfish.ui.theme.*
import com.rameswaram.dryfish.utils.toRupees
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun ProductDetailScreen(
    slug: String,
    onBack: () -> Unit,
    onAddToCart: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: ProductDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()
    var viewerImageIndex by remember { mutableIntStateOf(-1) }

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

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        if (scrollState.value > 300) {
                            Text(
                                text = uiState.product?.name ?: "",
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    },
                    navigationIcon = {
                        FilledIconButton(
                            onClick = onBack,
                            modifier = Modifier.padding(start = 8.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = GlassBlack
                            )
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.toggleWishlist() },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (uiState.isInWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (uiState.isInWishlist) SunsetOrange else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                // Removed
            }
        ) { padding ->
            when {
                uiState.isLoading -> ShimmerProductDetail()
                uiState.error != null -> ErrorProductDetail(
                    message = uiState.error ?: "",
                    onRetry = { viewModel.loadProduct(slug) }
                )
                uiState.product != null -> {
                    val product = uiState.product!!
                
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .verticalScroll(scrollState)
                    ) {
                        // Ken Burns Hero Image
                        KenBurnsHero(
                            images = product.images,
                            productName = product.name,
                            onImageClick = { index -> viewerImageIndex = index }
                        )
                    
                        // Content Card overlapping image
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-30).dp),
                            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                            ) {
                                // Product Names
                                AnimatedContent(
                                    targetState = product,
                                    transitionSpec = { fadeIn(tween(400)) with fadeOut(tween(200)) }
                                ) { prod ->
                                    Column {
                                        // Tamil name (large)
                                        Text(
                                            text = prod.nameTa,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    
                                        // English name (smaller, muted)
                                        Text(
                                            text = prod.nameEn,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            
                                Spacer(modifier = Modifier.height(12.dp))
                            
                                // Rating & Reviews
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { 
                                        // Scroll to reviews section
                                    }
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Amber.copy(alpha = 0.15f)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = Amber,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${product.rating ?: 4.5}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DeepOcean
                                            )
                                        }
                                    }
                                
                                    Spacer(modifier = Modifier.width(8.dp))
                                
                                    Text(
                                        text = "(${product.reviewCount ?: 0} reviews)",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                
                                    Spacer(modifier = Modifier.width(4.dp))
                                
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            
                                Spacer(modifier = Modifier.height(20.dp))
                            
                                // Weight Selector
                                Text(
                                    text = "Select Size",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            
                                Spacer(modifier = Modifier.height(12.dp))
                            
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(product.skus) { sku ->
                                        ModernWeightChip(
                                            sku = sku,
                                            isSelected = uiState.selectedSku?.id == sku.id,
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                viewModel.selectSku(sku)
                                            }
                                        )
                                    }
                                }
                            
                                Spacer(modifier = Modifier.height(24.dp))
                            
                                // Price Section
                                uiState.selectedSku?.let { sku ->
                                    PriceSection(sku = sku)
                                }
                            
                                Spacer(modifier = Modifier.height(24.dp))
                            
                                // Quantity
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Quantity",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f)
                                    )
                                
                                    ModernQuantityStepper(
                                        quantity = uiState.quantity,
                                        onIncrement = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            viewModel.incrementQuantity()
                                        },
                                        onDecrement = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            viewModel.decrementQuantity()
                                        }
                                    )
                                }
                            
                                Spacer(modifier = Modifier.height(24.dp))
                            
                                // Description
                                ExpandableDescription(
                                    shortDesc = product.shortDesc,
                                    description = product.description
                                )
                            
                                Spacer(modifier = Modifier.height(32.dp))
                            
                                // Review Section
                                ReviewSection(
                                    product = product,
                                    onSubmitReview = { rating, review ->
                                        viewModel.submitReview(rating, review)
                                    }
                                )
                            
                                Spacer(modifier = Modifier.height(32.dp))
                            
                                // Related Products
                                if (uiState.relatedProducts.isNotEmpty()) {
                                    Text(
                                        text = "You may also like",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                
                                    Spacer(modifier = Modifier.height(16.dp))
                                
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        contentPadding = PaddingValues(end = 20.dp)
                                    ) {
                                        items(uiState.relatedProducts) { relatedProduct ->
                                            RelatedProductCard(
                                                product = relatedProduct,
                                                onClick = { onProductClick(relatedProduct.slug) }
                                            )
                                        }
                                    }
                                }
                            
                                Spacer(modifier = Modifier.height(100.dp))
                            }
                        }
                    }
                }
            }
        }
    if (uiState.product != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            ModernBottomBar(
                selectedSku = uiState.selectedSku,
                quantity = uiState.quantity,
                onAddToCart = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.addToCart()
                },
                onBuyNow = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.addToCart()
                    onAddToCart()
                }
            )
        }
    }
    }
    
    if (viewerImageIndex >= 0) {
        Dialog(
            onDismissRequest = { viewerImageIndex = -1 },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            ImageZoomViewer(
                images = uiState.product?.images ?: emptyList(),
                initialIndex = viewerImageIndex,
                onDismiss = { viewerImageIndex = -1 }
            )
        }
    }
}

// Ken Burns Effect Hero
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KenBurnsHero(
    images: List<String>,
    productName: String,
    onImageClick: (Int) -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { images.size.coerceAtLeast(1) })
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
    ) {
        if (images.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                KenBurnsImage(
                    imageUrl = images[page],
                    contentDescription = "$productName - Image ${page + 1}",
                    onClick = { onImageClick(page) }
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
        
        // Gradient overlay at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )
        
        // Page indicators
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(images.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "indicator"
                    )
                    
                    Box(
                        modifier = Modifier
                            .width(width)
                            .height(8.dp)
                            .background(
                                color = if (isSelected) OceanBlue else Color.White.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun KenBurnsImage(
    imageUrl: String,
    contentDescription: String,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "kenburns")
    
    // Subtle zoom animation
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    // Subtle pan animation
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pan"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            model = imageUrl.replace("http://10.0.2.2:4000/images/", "file:///android_asset/images/").replace(".png", ".jpg"),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                }
        )
    }
}

@Composable
private fun ModernWeightChip(
    sku: SKU,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val hasDiscount = sku.mrp > sku.price

    Card(
        onClick = onClick,
        modifier = Modifier.widthIn(min = 90.dp),
        shape = RoundedCornerShape(14.dp),
        border = if (isSelected) {
            BorderStroke(3.dp, OceanBlue)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) OceanBlueLight else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = sku.weight,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) OceanBlue else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            if (hasDiscount) {
                Text(
                    text = sku.mrp.toRupees(),
                    fontSize = 10.sp,
                    color = if (isSelected) OceanBlue.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    textDecoration = TextDecoration.LineThrough
                )
                Text(
                    text = sku.price.toRupees(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) OceanBlue else SunsetOrange
                )
            } else {
                Text(
                    text = sku.price.toRupees(),
                    fontSize = 12.sp,
                    color = if (isSelected) OceanBlue else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PriceSection(sku: SKU) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = sku.price.toRupees(),
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = OceanBlue
        )
        
        if (sku.mrp > sku.price) {
            Text(
                text = sku.mrp.toRupees(),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textDecoration = TextDecoration.LineThrough
            )
            
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = SunsetOrange.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "${sku.discountPercent}% OFF",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SunsetOrange,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ModernQuantityStepper(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(4.dp)
        ) {
            IconButton(
                onClick = onDecrement,
                enabled = quantity > 1,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease"
                )
            }
            
            Text(
                text = quantity.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
            
            IconButton(
                onClick = onIncrement,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase"
                )
            }
        }
    }
}

@Composable
private fun ModernBottomBar(
    selectedSku: SKU?,
    quantity: Int,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit
) {
    val context = LocalContext.current
    val navMode = Settings.Secure.getString(context.contentResolver, "navigation_mode")
    val density = LocalDensity.current
    val bottomPadding = when (navMode) {
        "2" -> (-18).dp
        else -> 15.dp
    }

    val offsetY = with(density) { -bottomPadding.toPx().toInt() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .offset(x = 0.dp, y = offsetY.dp)
    ) {
        Surface(
            shadowElevation = 8.dp,
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onAddToCart,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, OceanBlue),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = OceanBlue
                    )
                ) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Add to Cart", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onBuyNow,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Buy Now", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ExpandableDescription(
    shortDesc: String?,
    description: String?
) {
    var isExpanded by remember { mutableStateOf(false) }
    val text = description ?: shortDesc ?: ""
    
    if (text.isNotBlank()) {
        Column {
            Text(
                text = "Description",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = text,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                overflow = if (isExpanded) TextOverflow.Clip else TextOverflow.Ellipsis
            )
            
            if (text.length > 100) {
                TextButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text(
                        text = if (isExpanded) "Show Less" else "Read More",
                        color = OceanBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReviewSection(
    product: Product,
    onSubmitReview: (Int, String) -> Unit
) {
    var showReviewDialog by remember { mutableStateOf(false) }
    var rating by remember { mutableIntStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Customer Reviews",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            
            TextButton(
                onClick = { showReviewDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("Write a Review")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Rating Summary Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${product.rating ?: 4.5}",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanBlue
                    )
                    StarRating(rating = (product.rating ?: 4.5), starSize = 20)
                    Text(
                        text = "${product.reviewCount ?: 0} reviews",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(60.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )
                
                // Rating bars
                Column {
                    listOf(5, 4, 3, 2, 1).forEach { star ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "$star",
                                fontSize = 12.sp,
                                modifier = Modifier.width(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(4.dp)
                                    .background(
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                        RoundedCornerShape(2.dp)
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width((100 * when(star) {
                                            5 -> 0.6f
                                            4 -> 0.3f
                                            else -> 0.1f
                                        }).dp)
                                        .background(Amber, RoundedCornerShape(2.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Review Dialog
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Write a Review") },
            text = {
                Column {
                    Text(
                        text = "Rate this product",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Star rating selector
                    Row {
                        repeat(5) { index ->
                            IconButton(
                                onClick = { rating = index + 1 }
                            ) {
                                Icon(
                                    imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = null,
                                    tint = if (index < rating) Amber else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text("Your Review") },
                        placeholder = { Text("Share your experience...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (rating > 0) {
                            onSubmitReview(rating, reviewText)
                            showReviewDialog = false
                            rating = 0
                            reviewText = ""
                        }
                    },
                    enabled = rating > 0
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

}
 
@Composable
private fun RelatedProductCard(product: Product, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                if (product.images.isNotEmpty()) {
                    SubcomposeAsyncImage(
                        model = product.images.first().replace("http://10.0.2.2:4000/images/", "file:///android_asset/images/").replace(".png", ".jpg"),
                        contentDescription = product.nameEn,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.nameTa,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.price.toRupees(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OceanBlue
                )
            }
        }
    }
}

@Composable
private fun ShimmerProductDetail() {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .shimmerLoadingAnimation()
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        repeat(4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 20.dp)
                    .shimmerLoadingAnimation()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ErrorProductDetail(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = SunsetOrange
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

@Composable
private fun ImageZoomViewer(
    images: List<String>,
    initialIndex: Int,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(initialIndex) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    var lastDist = 0f
                    do {
                        val event = awaitPointerEvent()
                        val pressed = event.changes.filter { it.pressed }
                        if (pressed.size >= 2) {
                            val dist = (pressed[0].position - pressed[1].position).getDistance()
                            if (lastDist > 0f) {
                                scale = (scale * dist / lastDist).coerceIn(1f, 4f)
                            }
                            lastDist = dist
                        } else if (pressed.size == 1) {
                            val c = pressed[0]
                            val delta = c.position - c.previousPosition
                            if (scale > 1f) {
                                offsetX += delta.x
                                offsetY += delta.y
                            }
                        } else {
                            lastDist = 0f
                        }
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        SubcomposeAsyncImage(
            model = images.getOrNull(currentIndex)
                ?.replace("http://10.0.2.2:4000/images/", "file:///android_asset/images/")?.replace(".png", ".jpg")
                ?: "",
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                }
        )

        // Close button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }

        // Navigation arrows
        if (images.size > 1 && scale == 1f) {
            if (currentIndex > 0) {
                IconButton(
                    onClick = { currentIndex--; scale = 1f; offsetX = 0f; offsetY = 0f },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous",
                        tint = Color.White
                    )
                }
            }
            if (currentIndex < images.size - 1) {
                IconButton(
                    onClick = { currentIndex++; scale = 1f; offsetX = 0f; offsetY = 0f },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
            }
        }

        // Page indicator
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(images.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (currentIndex == index) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (currentIndex == index) Color.White
                                else Color.White.copy(alpha = 0.4f)
                            )
                    )
                }
            }
        }
    }
}