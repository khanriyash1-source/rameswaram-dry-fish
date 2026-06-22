package com.rameswaram.dryfish.presentation.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.pager.*
import com.rameswaram.dryfish.domain.model.Product
import com.rameswaram.dryfish.presentation.common.LoadingScreen
import com.rameswaram.dryfish.presentation.common.ProductCard
import com.rameswaram.dryfish.ui.theme.*
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onViewAllFeatured: () -> Unit,
    onViewAllBestsellers: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        LoadingScreen()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            color = DeepNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Rameswaram Dry Fish",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Fresh from the coast",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Carousel
            if (uiState.featuredProducts.isNotEmpty()) {
                FeaturedCarousel(products = uiState.featuredProducts, onProductClick = onProductClick)
            }

            // Categories
            CategoriesSection(
                categories = uiState.categories,
                onCategoryClick = onCategoryClick
            )

            // Featured Products Row
            FeaturedRow(
                title = "Featured",
                products = uiState.featuredProducts,
                onProductClick = onProductClick,
                onViewAll = onViewAllFeatured
            )

            // Best Sellers Grid
            BestsellersGrid(
                products = uiState.bestsellerProducts,
                onProductClick = onProductClick,
                onViewAll = onViewAllBestsellers
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeaturedCarousel(
    products: List<Product>,
    onProductClick: (String) -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { products.size }
    )

    LaunchedEffect(products) {
        if (products.isEmpty()) return@LaunchedEffect
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % products.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            val product = products[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onProductClick(product.slug) }
            ) {
                AsyncImage(
                    model = product.images.firstOrNull(),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(alpha = 0.3f)
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${product.skus.firstOrNull()?.price ?: 0}",
                        style = MaterialTheme.typography.titleMedium,
                        color = SunsetOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
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
                        .background(if (pagerState.currentPage == index) SunsetOrange else Color.White.copy(alpha = 0.5f))
                )
            }
        }
    }
}

@Composable
fun CategoriesSection(
    categories: List<com.rameswaram.dryfish.domain.model.Category>,
    onCategoryClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val displayCategories = if (categories.isEmpty()) {
            listOf(
                com.rameswaram.dryfish.domain.model.Category("1", "Fish", "மீன்", "fish", null, 0),
                com.rameswaram.dryfish.domain.model.Category("2", "Prawns", "இறால்", "prawns", null, 0),
                com.rameswaram.dryfish.domain.model.Category("3", "Squid", "கணவாய்", "squid", null, 0),
                com.rameswaram.dryfish.domain.model.Category("4", "Crab", "நண்டு", "crab", null, 0),
                com.rameswaram.dryfish.domain.model.Category("5", "Combos", "கூட்டு", "combos", null, 0)
            )
        } else categories

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(displayCategories) { category ->
                CategoryChip(
                    name = category.name,
                    nameTamil = category.nameTamil,
                    onClick = { onCategoryClick(category.slug) }
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    nameTamil: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = OceanBlueLight
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = OceanBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = OceanBlue,
                textAlign = TextAlign.Center
            )
            if (!nameTamil.isNullOrEmpty()) {
                Text(
                    text = nameTamil,
                    style = MaterialTheme.typography.labelSmall,
                    color = OceanBlue.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun FeaturedRow(
    title: String,
    products: List<Product>,
    onProductClick: (String) -> Unit,
    onViewAll: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onViewAll) {
                Text("View All", color = OceanBlue)
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products.take(8)) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.slug) },
                    modifier = Modifier.width(180.dp)
                )
            }
        }
    }
}

@Composable
fun BestsellersGrid(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    onViewAll: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Best Sellers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onViewAll) {
                Text("View All", color = OceanBlue)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height((products.take(4).size * 300).dp)
        ) {
            items(products.take(4).size) { i ->
                val product = products.take(4)[i]
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.slug) }
                )
            }
        }
    }
}
