package com.rameswaram.dryfish.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rameswaram.dryfish.data.repository.DashboardStats
import com.rameswaram.dryfish.utils.toRupees

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onNavigateToProducts: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onLogout: () -> Unit,
    onResetData: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showResetConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset All Data?") },
            text = { Text("This will delete all orders and cart data from Firestore. Products and user accounts will not be affected. This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetConfirm = false
                        onResetData()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFC62828))
                ) { Text("Reset Everything") }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    TextButton(onClick = { showResetConfirm = true }) {
                        Icon(
                            Icons.Default.DeleteSweep,
                            contentDescription = "Reset Data",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            "Reset",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 12.sp
                        )
                    }
                    TextButton(onClick = onLogout) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Logout",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                uiState.error?.let {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Text(it, modifier = Modifier.padding(16.dp), color = Color(0xFFC62828))
                    }
                }

                uiState.stats?.let { stats ->
                    StatCard("Total Products", stats.productsCount.toString(), Icons.Default.Inventory, Color(0xFF1565C0))
                    StatCard("Total Orders", stats.ordersCount.toString(), Icons.Default.ShoppingBag, Color(0xFF2E7D32))
                    StatCard("Total Users", stats.usersCount.toString(), Icons.Default.People, Color(0xFF6A1B9A))
                    StatCard("Total Revenue", stats.totalRevenue.toRupees(), Icons.Default.AttachMoney, Color(0xFFE65100))

                    Spacer(Modifier.height(8.dp))
                    Text("Quick Actions", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    Button(
                        onClick = onNavigateToProducts,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                    ) {
                        Icon(Icons.Default.Inventory, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Manage Products")
                    }

                    Button(
                        onClick = onNavigateToOrders,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("View Orders")
                    }

                    Button(
                        onClick = onNavigateToUsers,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
                    ) {
                        Icon(Icons.Default.People, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("View Users")
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))
                    Text("Danger Zone", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFC62828))

                    OutlinedButton(
                        onClick = { showResetConfirm = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828))
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Reset All Orders & Cart Data")
                    }
                }

                if (uiState.stats == null && !uiState.isLoading) {
                    Text("No data available", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(36.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontSize = 14.sp, color = Color.Gray)
                Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}
