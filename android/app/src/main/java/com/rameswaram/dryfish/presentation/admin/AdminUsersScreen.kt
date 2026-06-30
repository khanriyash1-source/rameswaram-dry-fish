package com.rameswaram.dryfish.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.rameswaram.dryfish.domain.model.User
import com.rameswaram.dryfish.utils.toRupees

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
        viewModel.loadOrders()
    }

    val searchedUsers = remember(uiState.users, searchQuery) {
        if (searchQuery.isBlank()) uiState.users
        else uiState.users.filter { user ->
            user.name.contains(searchQuery, ignoreCase = true) ||
            user.email.contains(searchQuery, ignoreCase = true) ||
            (user.phone?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Users") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadUsers(); viewModel.loadOrders() }) {
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, email, phone...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty())
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (uiState.isLoading && uiState.users.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (searchedUsers.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (searchQuery.isNotBlank()) "No users match \"$searchQuery\""
                        else "No users found.",
                        color = Color.Gray, modifier = Modifier.padding(32.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(searchedUsers, key = { it.id }) { user ->
                        UserCard(
                            user = user,
                            onClick = { selectedUser = user }
                        )
                    }
                }
            }
        }
    }

    selectedUser?.let { user ->
        val userOrders = uiState.orders.filter { it.userId == user.id }
        UserOrdersDialog(
            user = user,
            orders = userOrders,
            onDismiss = { selectedUser = null }
        )
    }
}

@Composable
private fun UserCard(user: User, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name.ifEmpty { "No Name" }, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(user.email, fontSize = 13.sp, color = Color.Gray)
                if (!user.phone.isNullOrEmpty()) {
                    Text(user.phone, fontSize = 12.sp, color = Color.Gray)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("Spent: ${user.totalSpent.toRupees()}", fontSize = 12.sp, color = Color(0xFF2E7D32))
                    Text("Orders: ${user.orderCount}", fontSize = 12.sp, color = Color(0xFF1565C0))
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserOrdersDialog(
    user: User,
    orders: List<Order>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(user.name.ifEmpty { "User" }, fontWeight = FontWeight.Bold)
                Text("${orders.size} order${if (orders.size != 1) "s" else ""}", fontSize = 14.sp, color = Color.Gray)
            }
        },
        text = {
            if (orders.isEmpty()) {
                Text("No orders yet.", color = Color.Gray)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(orders, key = { it.id }) { order ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = statusColor(order.status).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            order.status.displayName(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = statusColor(order.status),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    order.createdAt.take(10),
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    order.items.joinToString(", ") { "${it.productName} x${it.quantity}" },
                                    fontSize = 12.sp,
                                    maxLines = 2,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Total: ${order.total.toRupees()}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                    Text(
                                        order.paymentMethod.displayName(),
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

private fun statusColor(status: com.rameswaram.dryfish.domain.model.OrderStatus): Color = when (status) {
    com.rameswaram.dryfish.domain.model.OrderStatus.PENDING -> Color(0xFFF57C00)
    com.rameswaram.dryfish.domain.model.OrderStatus.CONFIRMED -> Color(0xFF1565C0)
    com.rameswaram.dryfish.domain.model.OrderStatus.PROCESSING -> Color(0xFF7B1FA2)
    com.rameswaram.dryfish.domain.model.OrderStatus.SHIPPED -> Color(0xFF00897B)
    com.rameswaram.dryfish.domain.model.OrderStatus.DELIVERED -> Color(0xFF2E7D32)
    com.rameswaram.dryfish.domain.model.OrderStatus.CANCELLED -> Color(0xFFC62828)
    com.rameswaram.dryfish.domain.model.OrderStatus.RETURNED -> Color(0xFF6D4C41)
}
