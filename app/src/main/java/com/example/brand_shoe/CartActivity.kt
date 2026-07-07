package com.example.brand_shoe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Brand_ShoeTheme {
                CartScreen(
                    onBackClick = { finish() },
                    onOrderPlaced = {
                        startActivity(Intent(this, OrderConfirmationActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(onBackClick: () -> Unit, onOrderPlaced: () -> Unit) {
    val context = LocalContext.current
    val viewModel: OrderViewModel = viewModel(factory = OrderViewModelFactory(OrderRepoImpl()))
    var isPlacingOrder by remember { mutableStateOf(false) }
    val cartItems = CartManager.cartItems

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("$${"%.2f".format(CartManager.totalPrice())}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (isPlacingOrder) return@Button
                                isPlacingOrder = true
                                val user = FirebaseAuth.getInstance().currentUser
                                val snapshot = cartItems.toList()
                                var remaining = snapshot.size
                                var anyFailed = false
                                snapshot.forEach { cartItem ->
                                    val order = OrderModel(
                                        userId = user?.uid ?: "",
                                        userName = user?.email ?: "",
                                        productId = cartItem.productId,
                                        productName = cartItem.name,
                                        price = cartItem.price,
                                        quantity = cartItem.quantity,
                                        status = "Pending"
                                    )
                                    viewModel.placeOrder(order) { success, _ ->
                                        remaining -= 1
                                        if (!success) anyFailed = true
                                        if (remaining == 0) {
                                            isPlacingOrder = false
                                            if (anyFailed) {
                                                Toast.makeText(context, "Some items could not be ordered", Toast.LENGTH_SHORT).show()
                                            } else {
                                                CartManager.clearCart()
                                                showOrderNotification(context, "Your order is confirmed successfully")
                                                onOrderPlaced()
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !isPlacingOrder,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(if (isPlacingOrder) "PLACING ORDER..." else "Place Order (Cash on Delivery)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems, key = { it.productId }) { item ->
                    CartItemRow(item)
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem) {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF1F1F1)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageKeyToDrawable(item.imageKey)),
                    contentDescription = item.name,
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("$${"%.2f".format(item.price)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = { CartManager.decreaseQuantity(item.productId) }) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }
            Text("${item.quantity}", fontWeight = FontWeight.Bold)
            IconButton(onClick = { CartManager.increaseQuantity(item.productId) }) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
            IconButton(onClick = { CartManager.removeFromCart(item.productId) }) {
                Icon(Icons.Default.Delete, contentDescription = "Remove")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartPreview() {
    Brand_ShoeTheme { CartScreen(onBackClick = {}, onOrderPlaced = {}) }
}