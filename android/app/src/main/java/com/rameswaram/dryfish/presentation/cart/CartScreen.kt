package com.rameswaram.dryfish.presentation.cart

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.rameswaram.dryfish.domain.model.CartItem
import com.rameswaram.dryfish.presentation.common.EmptyState
import com.rameswaram.dryfish.presentation.common.shimmerLoadingAnimation
import com.rameswaram.dryfish.ui.theme.*
import com.rameswaram.dryfish.utils.Constants
import com.rameswaram.dryfish.utils.toRupees
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onCheckout: () -> Unit,
    onProductClick: (String) -> Unit,
    onNavigateToShop: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    viewModel: CartViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Shopping Cart",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        if (uiState.itemCount > 0) {
                            Text(
                                "${uiState.itemCount} ${if (uiState.itemCount == 1) "item" else "items"}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    if (uiState.items.isNotEmpty()) {
                        TextButton(
                            onClick = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.clearCart() 
                            }
                        ) {
                            Text(
                                "Clear All",
                                color = SunsetOrange,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (uiState.items.isNotEmpty()) {
                ModernCartBottomBar(
                    subtotal = uiState.subtotal,
                    deliveryCharge = if (uiState.isFreeDelivery) 0.0 else uiState.deliveryCharge,
                    total = uiState.total,
                    isFreeDelivery = uiState.isFreeDelivery,
                    remainingForFree = ((Constants.FREE_DELIVERY_MIN.toInt() - uiState.subtotal.toInt()).coerceAtLeast(0)) / 100,
                    onCheckout = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onCheckout()
                    }
                )
            }
        }
    ) { padding ->
        when {
            uiState.items.isEmpty() -> {
                EmptyState(
                    icon = Icons.Default.ShoppingBag,
                    title = "Your cart is empty",
                    description = "Add some fresh dry fish to get started!",
                    actionText = "Browse Products",
                    onAction = onNavigateToShop,
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Free delivery progress
                    if (!uiState.isFreeDelivery) {
                        item {
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically()
                            ) {
                                FreeDeliveryCard(
                                    subtotal = uiState.subtotal,
                                    remaining = ((Constants.FREE_DELIVERY_MIN.toInt() - uiState.subtotal.toInt()).coerceAtLeast(0)) / 100
                                )
                            }
                        }
                    } else {
                        item {
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically()
                            ) {
                                FreeDeliveryAppliedCard()
                            }
                        }
                    }
                    
                    items(
                        items = uiState.items,
                        key = { it.id }
                    ) { item ->
                        SwipeableCartItem(
                            item = item,
                            onClick = { onProductClick(item.productSlug) },
                            onQuantityChange = { quantity ->
                                viewModel.updateQuantity(item.id, quantity)
                            },
                            onRemove = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.removeItem(item.id)
                            }
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun FreeDeliveryCard(subtotal: Double, remaining: Int) {
    val progress = (subtotal / Constants.FREE_DELIVERY_MIN).coerceIn(0.0, 1.0).toFloat()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = OceanBlue.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(OceanBlue.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = OceanBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Free Delivery",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (remaining > 0) "Add ₹$remaining more" else "You qualify for free delivery!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(3.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(
                            OceanBlue,
                            RoundedCornerShape(3.dp)
                        )
                        .animateContentSize()
                )
            }
        }
    }
}

@Composable
private fun FreeDeliveryAppliedCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Seafoam.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Seafoam.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Seafoam,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = "Free Delivery Applied!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Seafoam
                )
                Text(
                    text = "Your order will be delivered for free",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SwipeableCartItem(
    item: CartItem,
    onClick: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    
    // Swipe threshold
    val swipeThreshold = -200f
    
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Background (revealed when swiping)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentAlignment = Alignment.CenterEnd
        ) {
            // Delete action background
            val swipeProgress = (offsetX / swipeThreshold).coerceIn(0f, 1f)
            
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(swipeProgress)
                    .background(
                        SunsetOrange.copy(alpha = swipeProgress * 0.9f),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (offsetX < -50) {
                    Row(
                        modifier = Modifier.padding(end = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Remove",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        // Foreground (cart item card)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX < swipeThreshold * 0.6f) {
                                // Swiped far enough - delete
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onRemove()
                            } else {
                                // Snap back
                                scope.launch {
                                    animate(
                                        initialValue = offsetX,
                                        targetValue = 0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        )
                                    ) { value, _ ->
                                        offsetX = value
                                    }
                                }
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val newOffset = offsetX + dragAmount
                            offsetX = newOffset.coerceIn(swipeThreshold, 0f)
                            
                            // Haptic feedback when threshold reached
                            if (offsetX < swipeThreshold * 0.8f && offsetX > swipeThreshold * 0.85f) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                    )
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (offsetX == 0f) 1.dp else 4.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Product Image
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    SubcomposeAsyncImage(
                        model = item.productImage.replace("http://10.0.2.2:4000/images/", "file:///android_asset/images/"),
                        contentDescription = item.productName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .shimmerLoadingAnimation()
                            )
                        }
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Product Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.productName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = item.weight,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Price row
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.subtotal.toRupees(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = OceanBlue
                        )
                        
                        if (item.mrp > item.price) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.totalMrp.toRupees(),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                            )
                            
                            Spacer(modifier = Modifier.width(6.dp))
                            
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Seafoam.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "-${((1 - item.price / item.mrp) * 100).toInt()}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Seafoam,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Quantity controls
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Increment
                    IconButton(
                        onClick = { 
                            if (item.quantity < item.maxQuantity) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onQuantityChange(item.quantity + 1)
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            modifier = Modifier.size(18.dp),
                            tint = OceanBlue
                        )
                    }
                    
                    // Quantity display
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = item.quantity.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    
                    // Decrement
                    IconButton(
                        onClick = { 
                            if (item.quantity > 1) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onQuantityChange(item.quantity - 1)
                            }
                        },
                        enabled = item.quantity > 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            modifier = Modifier.size(18.dp),
                            tint = if (item.quantity > 1) OceanBlue else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernCartBottomBar(
    subtotal: Double,
    deliveryCharge: Double,
    total: Double,
    isFreeDelivery: Boolean,
    remainingForFree: Int,
    onCheckout: () -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Price breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = subtotal.toRupees(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Delivery",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isFreeDelivery) {
                    Text(
                        text = "FREE",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Seafoam
                    )
                } else {
                    Text(
                        text = deliveryCharge.toRupees(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            
            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "(incl. taxes)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = total.toRupees(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OceanBlue
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Checkout button with gradient
            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SunsetOrange
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Proceed to Checkout",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}