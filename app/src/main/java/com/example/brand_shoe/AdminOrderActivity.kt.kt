package com.example.brand_shoe

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.brand_shoe.Model.OrderModel
import com.example.brand_shoe.ViewModel.OrderViewModel
import com.example.brand_shoe.ViewModel.OrderViewModelFactory
import com.example.brand_shoe.repo.OrderRepoImpl
import com.example.brand_shoe.ui.theme.Brand_ShoeTheme

class AdminOrderActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Brand_ShoeTheme { AdminOrderScreen(onBackClick = { finish() }) }
        }
    }
}

val orderStatuses = listOf("Pending", "Shipped", "Delivered", "Cancelled")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val viewModel: OrderViewModel = viewModel(factory = OrderViewModelFactory(OrderRepoImpl()))
    var orders by remember { mutableStateOf<List<OrderModel?>>(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel.getAllOrders { _, _, data -> orders = data }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Orders") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { innerPadding ->
        if (orders.isEmpty()) {
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No orders yet", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    order?.let {
                        OrderAdminRow(order = it, onStatusChange = { newStatus ->
                            viewModel.updateOrderStatus(it.id, newStatus) { _, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderAdminRow(order: OrderModel, onStatusChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(order.productName, fontWeight = FontWeight.Bold)
            Text("Customer: ${order.userName}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text("Qty: ${order.quantity}  ·  $${"%.2f".format(order.price)}  ·  ${order.paymentMethod}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = order.status,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    orderStatuses.forEach { status ->
                        DropdownMenuItem(text = { Text(status) }, onClick = { expanded = false; onStatusChange(status) })
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminOrderPreview() {
    Brand_ShoeTheme { AdminOrderScreen(onBackClick = {}) }
}