package com.example.brand_shoe

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.brand_shoe.Model.ProductModel
import com.example.brand_shoe.ViewModel.NotificationViewModel
import com.example.brand_shoe.ViewModel.NotificationViewModelFactory
import com.example.brand_shoe.ViewModel.ProductViewModel
import com.example.brand_shoe.ViewModel.ProductViewModelFactory
import com.example.brand_shoe.repo.ImageRepoImpl
import com.example.brand_shoe.repo.NotificationRepoImpl
import com.example.brand_shoe.repo.ProductRepoImpl
import com.example.brand_shoe.ui.theme.Brand_ShoeTheme
import com.google.firebase.auth.FirebaseAuth

class HomePageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Brand_ShoeTheme {
                HomeDashboard(
                    onProductClick = { product ->
                        val intent = Intent(this, ProductDetailActivity::class.java)
                        intent.putExtra("productId", product.id)
                        intent.putExtra("productName", product.name)
                        intent.putExtra("productPrice", product.price)
                        intent.putExtra("productDescription", product.description)
                        intent.putExtra("productImageUrl", product.imageUrl)
                        startActivity(intent)
                    },
                    onCartClick = { startActivity(Intent(this, CartActivity::class.java)) },
                    onProfileClick = { startActivity(Intent(this, UserProfileActivity::class.java)) },
                    onNotificationsClick = { startActivity(Intent(this, NotificationActivity::class.java)) }
                )
            }
        }
    }
}

private val sortOptions = listOf("Newest", "Price: Low to High", "Price: High to Low", "In Stock Only")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboard(
    onProductClick: (ProductModel) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    val viewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(ProductRepoImpl(), ImageRepoImpl())
    )
    val notificationViewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(NotificationRepoImpl())
    )
    var products by remember { mutableStateOf<List<ProductModel?>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf("Newest") }
    var unreadCount by remember { mutableStateOf(0) }
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        viewModel.getAllProducts { _, _, data -> products = data }
        if (uid.isNotBlank()) {
            notificationViewModel.getNotificationsForUser(uid) { _, _, data ->
                unreadCount = data.count { it?.read == false }
            }
        }
    }
    val filteredProducts = remember(products, searchQuery, selectedSort) {
        ProductFilterHelper.filterAndSort(products, searchQuery, selectedSort)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp).clip(CircleShape).border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("BRAND SHOE", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        BadgedBox(badge = {
                            if (unreadCount > 0) Badge { Text("$unreadCount") }
                        }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onCartClick,
                    icon = {
                        BadgedBox(badge = {
                            if (CartManager.cartCount > 0) Badge { Text("${CartManager.cartCount}") }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    },
                    label = { Text("Cart") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onProfileClick,
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Step into your\nnext favorite pair",
                style = MaterialTheme.typography.headlineSmall,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search shoes...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                sortOptions.forEach { option ->
                    FilterChip(
                        selected = selectedSort == option,
                        onClick = { selectedSort = if (selectedSort == option) "Newest" else option },
                        label = { Text(option) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (searchQuery.isBlank()) "New Arrivals" else "Results for \"$searchQuery\"",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text("${filteredProducts.size} items", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (searchQuery.isBlank()) "No products yet — check back soon!" else "No shoes match \"$searchQuery\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredProducts) { product ->
                        product?.let { ProductGridItem(it, onClick = { onProductClick(it) }) }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductGridItem(product: ProductModel, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            ProductImage(
                imageUrl = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(product.name, style = MaterialTheme.typography.titleMedium, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "$${"%.2f".format(product.price)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeDashboardPreview() {
    Brand_ShoeTheme { HomeDashboard(onProductClick = {}, onCartClick = {}, onProfileClick = {}, onNotificationsClick = {}) }
}