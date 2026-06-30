package com.rameswaram.dryfish.presentation.admin

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rameswaram.dryfish.domain.model.Order
import com.rameswaram.dryfish.domain.model.OrderStatus
import com.rameswaram.dryfish.utils.toRupees

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }

    var selectedFilter by remember { mutableStateOf<OrderStatus?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredOrders = if (selectedFilter == null) uiState.orders
    else uiState.orders.filter { it.status == selectedFilter }

    val searchedOrders = remember(filteredOrders, searchQuery) {
        if (searchQuery.isBlank()) filteredOrders
        else filteredOrders.filter { order ->
            order.shippingAddress.name.contains(searchQuery, ignoreCase = true) ||
            order.shippingAddress.phone.contains(searchQuery, ignoreCase = true) ||
            order.orderNumber.contains(searchQuery, ignoreCase = true) ||
            order.shippingAddress.city.contains(searchQuery, ignoreCase = true) ||
            order.items.any { it.productName.contains(searchQuery, ignoreCase = true) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orders") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadOrders() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Filter chips (horizontally scrollable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All", fontSize = 12.sp) }
                )
                OrderStatus.entries.forEach { status ->
                    FilterChip(
                        selected = selectedFilter == status,
                        onClick = { selectedFilter = status },
                        label = { Text(status.name, fontSize = 12.sp) }
                    )
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, phone, order#...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty())
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            if (uiState.isLoading && uiState.orders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (searchedOrders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (selectedFilter == null) "No orders yet."
                        else "No ${selectedFilter?.name?.lowercase()} orders.",
                        modifier = Modifier.padding(32.dp),
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(searchedOrders, key = { it.id }) { order ->
                        OrderCard(
                            order = order,
                            onStatusChange = { status ->
                                viewModel.updateOrderStatus(order.id, status)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: Order,
    onStatusChange: (OrderStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                OrderStatusChip(order.status)
            }

            Spacer(Modifier.height(8.dp))
            Text("Total: ${order.total.toRupees()}", fontSize = 14.sp)
            val itemsText = if (order.items.isEmpty()) "Items data not available" else "Items: ${order.items.size}"
            Text(itemsText, fontSize = 13.sp, color = Color.Gray)
            Text("Payment: ${order.paymentMethod.name}", fontSize = 13.sp, color = Color.Gray)
            Text(order.createdAt, fontSize = 12.sp, color = Color.Gray)

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Hide Items" else "Show Items")
                }

                Box {
                    TextButton(onClick = { showStatusMenu = true }) {
                        Text("Update Status", color = Color(0xFF1565C0))
                    }
                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        OrderStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.name) },
                                onClick = {
                                    onStatusChange(status)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }

            if (expanded) {
                Text("Items", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.productName} x${item.quantity}", fontSize = 13.sp)
                        Text((item.price * item.quantity).toRupees(), fontSize = 13.sp)
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("Shipping Address", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Text(order.shippingAddress.formattedForDisplay, fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun OrderStatusChip(status: OrderStatus) {
    val color = when (status) {
        OrderStatus.PENDING -> Color(0xFFF57F17)
        OrderStatus.CONFIRMED -> Color(0xFF1565C0)
        OrderStatus.PROCESSING -> Color(0xFF6A1B9A)
        OrderStatus.SHIPPED -> Color(0xFF2E7D32)
        OrderStatus.DELIVERED -> Color(0xFF1B5E20)
        OrderStatus.CANCELLED -> Color(0xFFC62828)
        OrderStatus.RETURNED -> Color(0xFFE65100)
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            status.name,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
