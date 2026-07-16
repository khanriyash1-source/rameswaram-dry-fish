package com.rameswaram.dryfish.presentation.checkout

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rameswaram.dryfish.domain.model.Address
import com.rameswaram.dryfish.domain.model.CartItem
import com.rameswaram.dryfish.domain.model.RazorpayOrderResponse
import com.rameswaram.dryfish.presentation.common.LoadingScreen
import com.rameswaram.dryfish.ui.theme.*
import com.rameswaram.dryfish.utils.toRupees
import com.razorpay.Checkout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onOrderPlaced: (String) -> Unit,
    viewModel: CheckoutViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    // Guard against opening Razorpay twice (e.g. after config change / rotation)
    var razorpayOpened by rememberSaveable { mutableStateOf(false) }

    // Reset flag when a new payment flow begins (dialog shown)
    LaunchedEffect(uiState.showPaymentDialog) {
        if (uiState.showPaymentDialog) razorpayOpened = false
    }

    LaunchedEffect(uiState.orderPlaced) {
        uiState.orderPlaced?.let { order ->
            delay(500)
            onOrderPlaced(order.id)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Checkout",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingScreen(modifier = Modifier.padding(padding))
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Scrollable content
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Delivery Address
                        item {
                            AddressSection(
                                address = uiState.selectedAddress,
                                onAddAddress = { viewModel.showAddAddress() },
                                onChangeAddress = { viewModel.showAddAddress() }
                            )
                        }

                        // Payment Method - ONLINE ONLY
                        item {
                            PaymentSection(
                                selectedMethod = uiState.selectedPaymentMethod,
                                onSelectMethod = { viewModel.selectPaymentMethod(it) }
                            )
                        }

                        // Order Items
                        if (uiState.cartItems.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Order Items (${uiState.cartItems.size})",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            
                            items(uiState.cartItems) { item ->
                                OrderItemCard(item = item)
                            }
                        }

                        // Order Summary
                        item {
                            OrderSummarySection(
                                subtotal = uiState.subtotal,
                                deliveryCharge = uiState.deliveryCharge,
                                discount = uiState.discount,
                                total = uiState.total,
                                isFreeDelivery = uiState.isFreeDelivery
                            )
                        }

                        item {
                            Spacer(Modifier.height(100.dp))
                        }
                    }
                    
                    // Fixed bottom section with Slide-to-Pay
                    if (uiState.selectedAddress != null) {
                        SlideToPaySection(
                            total = uiState.total,
                            isProcessing = uiState.isPlacingOrder,
                            paymentStep = uiState.paymentStep,
                            onSlideComplete = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.placeOrder()
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Address Bottom Sheet
    if (uiState.showAddAddressSheet) {
        AddAddressBottomSheet(
            onDismiss = { viewModel.hideAddAddress() },
            onSave = { address -> viewModel.addAddress(address) }
        )
    }

    // Payment Confirmation Dialog
    if (uiState.showPaymentDialog) {
        PaymentConfirmationDialog(
            total = uiState.total,
            onConfirm = {
                viewModel.confirmPayment()
            },
            onDismiss = { viewModel.dismissPaymentDialog() }
        )
    }

    // Open Razorpay AFTER dialog dismiss animation completes (only once)
    LaunchedEffect(uiState.showPaymentDialog, uiState.isPlacingOrder) {
        if (!uiState.showPaymentDialog && uiState.isPlacingOrder && uiState.pendingRazorpayOrder != null && !razorpayOpened) {
            razorpayOpened = true
            delay(200)
            openRazorpayCheckout(context, uiState.pendingRazorpayOrder!!)
        }
    }

    // Error Dialog
    if (uiState.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            shape = RoundedCornerShape(24.dp),
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(SunsetOrange.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            tint = SunsetOrange,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Payment Failed",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = uiState.error ?: "",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearError() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OceanBlue)
                ) {
                    Text("Try Again", fontWeight = FontWeight.Medium)
                }
            }
        )
    }
}

@Composable
private fun AddressSection(
    address: Address?,
    onAddAddress: () -> Unit,
    onChangeAddress: () -> Unit
) {
    Column {
        Text(
            text = "Delivery Address",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (address != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = OceanBlue.copy(alpha = 0.08f)
                ),
                border = BorderStroke(1.dp, OceanBlue.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(OceanBlue.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = OceanBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = address.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (address.isDefault) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Seafoam.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "DEFAULT",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Seafoam,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = address.formattedForDisplay,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )

                        Text(
                            text = "📱 ${address.phone}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    TextButton(onClick = onChangeAddress) {
                        Text("Change")
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAddAddress),
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
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddLocation,
                        contentDescription = null,
                        tint = OceanBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Add Delivery Address",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = OceanBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentSection(
    selectedMethod: com.rameswaram.dryfish.domain.model.PaymentMethod?,
    onSelectMethod: (com.rameswaram.dryfish.domain.model.PaymentMethod) -> Unit
) {
    Column {
        Text(
            text = "Payment Method",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Online Payment ONLY - No COD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SunsetOrange.copy(alpha = 0.08f)
            ),
            border = BorderStroke(1.dp, SunsetOrange.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(SunsetOrange.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        tint = SunsetOrange,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Online Payment",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Credit/Debit Card, UPI, Net Banking",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Seafoam.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "SECURE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Seafoam,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Note about payment
        Text(
            text = "💳 All payments are processed securely via Razorpay",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, start = 4.dp)
        )
    }
}

@Composable
private fun OrderItemCard(item: CartItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        OceanBlue.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Fastfood,
                    contentDescription = null,
                    tint = OceanBlue,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = item.weight,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "×${item.quantity}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = item.subtotal.toRupees(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OceanBlue
                )
            }
        }
    }
}

@Composable
private fun OrderSummarySection(
    subtotal: Double,
    deliveryCharge: Double,
    discount: Double,
    total: Double,
    isFreeDelivery: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Order Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SummaryRow("Subtotal", subtotal.toRupees())
            
            if (discount > 0) {
                SummaryRow("Discount", "-${discount.toRupees()}", SunsetOrange)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Amount",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = total.toRupees(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OceanBlue
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

@Composable
private fun SlideToPaySection(
    total: Double,
    isProcessing: Boolean,
    paymentStep: String,
    onSlideComplete: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var offsetX by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    
    val maxSlideWidth = 280.dp
    val thumbSize = 64.dp
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        // Background track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            OceanBlue.copy(alpha = 0.1f),
                            SunsetOrange.copy(alpha = 0.1f)
                        )
                    ),
                    RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Animated text
            AnimatedVisibility(
                visible = offsetX < 50f,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = "Slide to Pay ${total.toRupees()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            AnimatedVisibility(
                visible = offsetX >= 50f && !isProcessing,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = "Release to Confirm",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SunsetOrange
                )
            }
        }
        
        // Draggable thumb
        if (!isProcessing) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                    .size(thumbSize)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                val threshold = with(density) { (maxSlideWidth - thumbSize).toPx() * 0.8f }
                                if (offsetX >= threshold) {
                                    // Complete the slide
                                    onSlideComplete()
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
                                val maxOffset = with(density) { (maxSlideWidth - thumbSize).toPx() }
                                val newOffset = offsetX + dragAmount
                                offsetX = newOffset.coerceIn(0f, maxOffset)
                                
                                // Haptic feedback at threshold
                                val threshold = maxOffset * 0.8f
                                if (offsetX in threshold..(threshold + 10f)) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // Thumb background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(OceanBlue, SunsetOrange)
                            ),
                            CircleShape
                        )
                        .shadow(8.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Slide to pay",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        } else {
            // Processing state
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = OceanBlue,
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = paymentStep.ifEmpty { "Processing Payment..." },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = OceanBlue
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAddressBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Address) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                "Add Delivery Address",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = street,
                onValueChange = { street = it },
                label = { Text("Street / Area / Landmark") },
                leadingIcon = { Icon(Icons.Default.Home, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 2,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state,
                    onValueChange = { state = it },
                    label = { Text("State") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = pincode,
                onValueChange = { pincode = it },
                label = { Text("Pincode") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val address = Address(
                        id = System.currentTimeMillis().toString(),
                        name = name,
                        phone = phone,
                        street = street,
                        city = city,
                        state = state,
                        pincode = pincode
                    )
                    onSave(address)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OceanBlue),
                enabled = name.isNotBlank() && phone.isNotBlank() && street.isNotBlank()
            ) {
                Text("Save Address", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun openRazorpayCheckout(context: android.content.Context, order: RazorpayOrderResponse) {
    val activity = context as? androidx.activity.ComponentActivity ?: return
    try {
        val options = JSONObject().apply {
            put("amount", order.amount)
            put("currency", order.currency)
            put("order_id", order.orderId)
            put("name", "Rameswaram Dry Fish")
            put("description", "Order payment")
            put("prefill", JSONObject().apply {
                put("email", "user@example.com")
                put("contact", "9999999999")
            })
            put("theme", JSONObject().apply {
                put("color", "#00B4D8")
            })
        }
        val checkout = Checkout()
        checkout.setKeyID(order.keyId)
        checkout.open(activity, options)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
private fun PaymentConfirmationDialog(
    total: Double,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            SunsetOrange.copy(alpha = 0.15f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        tint = SunsetOrange,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Complete Payment",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pay ${total.toRupees()}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OceanBlue
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "You will be redirected to Razorpay's secure payment gateway",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OceanBlue)
            ) {
                Text(
                    text = "Pay ${total.toRupees()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}