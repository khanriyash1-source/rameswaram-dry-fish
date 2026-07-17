package com.rameswaram.dryfish.presentation.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.rameswaram.dryfish.domain.model.Product
import com.rameswaram.dryfish.domain.model.SKU
import com.rameswaram.dryfish.presentation.common.shimmerLoadingAnimation
import java.util.UUID

private data class SkuEntry(val weight: String, val price: String, val stock: String, val id: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = remember(uiState.products, searchQuery) {
        if (searchQuery.isBlank()) uiState.products
        else uiState.products.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.nameTamil?.contains(searchQuery, ignoreCase = true) == true ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Products") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadProducts() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(
                        onClick = { viewModel.syncProducts() },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = "Sync from JSON")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingProduct = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Product")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name, Tamil name, or category...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                if (uiState.isLoading && uiState.products.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(32.dp))
                } else if (filteredProducts.isEmpty()) {
                    Text(
                        if (searchQuery.isNotBlank()) "No products match \"$searchQuery\""
                        else "No products yet. Tap + to add one.",
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(32.dp),
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProducts, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                onEdit = {
                                    editingProduct = product
                                    showDialog = true
                                },
                                onDelete = { viewModel.deleteProduct(product.id) },
                                onToggleEnabled = { enabled ->
                                    viewModel.saveProduct(product.copy(isEnabled = enabled))
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        ProductFormDialog(
            product = editingProduct,
            viewModel = viewModel,
            onDismiss = { showDialog = false },
            onSave = { product ->
                viewModel.saveProduct(product)
                showDialog = false
            }
        )
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (product.isEnabled)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(product.category, color = Color.Gray, fontSize = 13.sp)
                Text(
                    "${product.skus.size} variants | ₹${product.skus.firstOrNull()?.price?.let { "%.2f".format(it / 100.0) } ?: "0.00"}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Switch(
                checked = product.isEnabled,
                onCheckedChange = onToggleEnabled
            )
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF1565C0))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFC62828))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProductFormDialog(
    product: Product?,
    viewModel: AdminViewModel,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var nameTamil by remember { mutableStateOf(product?.nameTamil ?: "") }
    var slug by remember { mutableStateOf(product?.slug ?: "") }
    var category by remember { mutableStateOf(product?.category ?: "Fish") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var shortDesc by remember { mutableStateOf(product?.shortDesc ?: "") }
    val images = remember {
        mutableStateListOf<String>().also { it.addAll(product?.images ?: emptyList()) }
    }
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            isUploading = true
            viewModel.uploadImage(it) { url ->
                isUploading = false
                if (url != null) images.add(url)
            }
        }
    }

    val initialSkus = remember {
        (product?.skus ?: listOf(SKU(id = "", weight = "100g", price = 0.0, mrp = 0.0, stock = 10, weightInGrams = 100, isAvailable = true)))
            .map { SkuEntry(it.weight, "%.0f".format(it.price / 100.0), it.stock.toString(), it.id) }
            .toMutableList()
    }
    var skus by remember { mutableStateOf(initialSkus) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Add Product" else "Edit Product") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it; if (slug == product?.slug ?: "") slug = it.lowercase().replace(" ", "-") }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = nameTamil, onValueChange = { nameTamil = it }, label = { Text("Name (Tamil)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = slug, onValueChange = { slug = it }, label = { Text("Slug") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = shortDesc, onValueChange = { shortDesc = it }, label = { Text("Short Description") }, modifier = Modifier.fillMaxWidth(), maxLines = 2, singleLine = true)
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
                Text("Images", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (images.isEmpty() && !isUploading) {
                    Text("No images yet. Tap below to add.", color = Color.Gray, fontSize = 12.sp)
                }
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    images.forEachIndexed { index, url ->
                        Box(modifier = Modifier.size(80.dp)) {
                            ImageThumbnail(url = url)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red.copy(alpha = 0.8f))
                                    .clickable { images.removeAt(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { launcher.launch("image/*") },
                        enabled = !isUploading,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (isUploading) "Uploading..." else "Add Image", fontSize = 13.sp)
                    }
                    OutlinedTextField(
                        value = "",
                        onValueChange = { url ->
                            if (url.isNotBlank()) images.add(url.trim())
                        },
                        label = { Text("Or paste URL") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text("Variants (prices in ₹)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                skus.forEachIndexed { index, sku ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = sku.weight,
                            onValueChange = { v -> skus = skus.toMutableList().apply { this[index] = this[index].copy(weight = v) } },
                            label = { Text("Wt") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = sku.price,
                            onValueChange = { v -> skus = skus.toMutableList().apply { this[index] = this[index].copy(price = v) } },
                            label = { Text("₹") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = sku.stock,
                            onValueChange = { v -> skus = skus.toMutableList().apply { this[index] = this[index].copy(stock = v) } },
                            label = { Text("Stock") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        if (skus.size > 1) {
                            IconButton(
                                onClick = { skus = skus.toMutableList().also { it.removeAt(index) } },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                TextButton(onClick = { skus = skus.toMutableList().apply { add(SkuEntry("", "", "10", "")) } }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Variant", fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val id = product?.id ?: UUID.randomUUID().toString()
                    val skuList = skus.filter { it.weight.isNotBlank() }.mapIndexed { i, s ->
                        val pricePaise = (s.price.toDoubleOrNull() ?: 0.0) * 100.0
                        val skuId = if (s.id.isNotBlank()) s.id else UUID.randomUUID().toString()
                        SKU(
                            id = skuId,
                            weight = s.weight,
                            weightInGrams = s.weight.filter { it.isDigit() }.toIntOrNull() ?: 100,
                            price = pricePaise,
                            mrp = pricePaise,
                            stock = s.stock.toIntOrNull() ?: 10,
                            isAvailable = true
                        )
                    }
                    val newProduct = Product(
                        id = id,
                        name = name,
                        nameTamil = nameTamil.ifEmpty { null },
                        slug = slug.ifEmpty { name.lowercase().replace(" ", "-") },
                        description = description.ifEmpty { null },
                        shortDesc = shortDesc.ifEmpty { null },
                        category = category,
                        images = images.toList(),
                        skus = skuList,
                        tags = emptyList(),
                        isFeatured = false,
                        isBestseller = false,
                        rating = 0.0,
                        reviewCount = 0,
                        isEnabled = product?.isEnabled ?: true
                    )
                    onSave(newProduct)
                },
                enabled = name.isNotBlank() && skus.any { it.weight.isNotBlank() }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ImageThumbnail(url: String) {
    val model = url.replace("http://10.0.2.2:4000/images/", "file:///android_asset/images/").replace(".png", ".jpg")
    SubcomposeAsyncImage(
        model = model,
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop,
        loading = {
            Box(
                Modifier
                    .fillMaxSize()
                    .shimmerLoadingAnimation()
            )
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.BrokenImage,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
}
