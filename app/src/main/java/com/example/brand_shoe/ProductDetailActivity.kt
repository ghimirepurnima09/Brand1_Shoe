package com.example.brand_shoe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.brand_shoe.Model.OrderModel
import com.example.brand_shoe.ViewModel.OrderViewModel
import com.example.brand_shoe.ViewModel.OrderViewModelFactory
import com.example.brand_shoe.repo.OrderRepoImpl
import com.example.brand_shoe.ui.theme.Brand_ShoeTheme
import com.google.firebase.auth.FirebaseAuth

class ProductDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val productId = intent.getStringExtra("productId") ?: ""
        val productName = intent.getStringExtra("productName") ?: ""
        val productPrice = intent.getDoubleExtra("productPrice", 0.0)
        val productDescription = intent.getStringExtra("productDescription") ?: ""
        val productImageKey = intent.getStringExtra("productImageKey") ?: "shoe1"

        setContent {
            Brand_ShoeTheme {
                ProductDetailScreen(
                    productId = productId,
                    productName = productName,
                    productPrice = productPrice,
                    productDescription = productDescription,
                    productImageKey = productImageKey,
                    onBackClick = { finish() },
                    onAddToCart = { startActivity(Intent(this, CartActivity::class.java)) },
                    onOrderPlaced = { startActivity(Intent(this, OrderConfirmationActivity::class.java)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    productName: String,
    productPrice: Double,
    productDescription: String,
    productImageKey: String,
    onBackClick: () -> Unit,
    onAddToCart: () -> Unit,
    onOrderPlaced: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: OrderViewModel = viewModel(factory = OrderViewModelFactory(OrderRepoImpl()))
    var isPlacingOrder by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(
                modifier = Modifier.fillMaxWidth().height(280.dp).background(Color(0xFFF1F1F1)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageKeyToDrawable(productImageKey)),
                    contentDescription = productName,
                    modifier = Modifier.size(220.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(productName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("$${"%.2f".format(productPrice)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    productDescription.ifBlank { "No description provided for this product." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(28.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = {
                            CartManager.addToCart(productId, productName, productPrice, productImageKey)
                            onAddToCart()
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Add to Cart", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (isPlacingOrder) return@Button
                            isPlacingOrder = true
                            val user = FirebaseAuth.getInstance().currentUser
                            val order = OrderModel(
                                userId = user?.uid ?: "",
                                userName = user?.email ?: "",
                                productId = productId,
                                productName = productName,
                                price = productPrice,
                                quantity = 1,
                                status = "Pending"
                            )
                            viewModel.placeOrder(order) { success, message ->
                                isPlacingOrder = false
                                if (success) {
                                    showOrderNotification(context, "Your order is confirmed successfully")
                                    onOrderPlaced()
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = !isPlacingOrder,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(if (isPlacingOrder) "PLACING..." else "Buy Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailPreview() {
    Brand_ShoeTheme {
        ProductDetailScreen("1", "Nike Air Max", 120.0, "A great shoe.", "shoe1", onBackClick = {}, onAddToCart = {}, onOrderPlaced = {})
    }
}