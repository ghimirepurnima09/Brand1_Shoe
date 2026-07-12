package com.example.brand_shoe

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.brand_shoe.Model.NotificationModel
import com.example.brand_shoe.Model.OrderModel
import com.example.brand_shoe.ViewModel.NotificationViewModel
import com.example.brand_shoe.ViewModel.NotificationViewModelFactory
import com.example.brand_shoe.ViewModel.OrderViewModel
import com.example.brand_shoe.ViewModel.OrderViewModelFactory
import com.example.brand_shoe.repo.NotificationRepoImpl
import com.example.brand_shoe.repo.OrderRepoImpl
import com.example.brand_shoe.ui.theme.Brand_ShoeTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
private val filterOptions = listOf("All") + orderStatuses

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val viewModel: OrderViewModel = viewModel(factory = OrderViewModelFactory(OrderRepoImpl()))
    val notificationViewModel: NotificationViewModel = viewModel(factory = NotificationViewModelFactory(NotificationRepoImpl()))
    var orders by remember { mutableStateOf<List<OrderModel?>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedOrder by remember { mutableStateOf<OrderModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getAllOrders { _, _, data -> orders = data }
    }

    val filteredOrders = remember(orders, searchQuery, selectedFilter) {
        orders.filter { order ->
            order != null &&
                    (selectedFilter == "All" || order.status.equals(selectedFilter, ignoreCase = true)) &&
                    (searchQuery.isBlank() ||
                            order.productName.contains(searchQuery, ignoreCase = true) ||
                            order.userName.contains(searchQuery, ignoreCase = true))
        }
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
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by customer or product...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    filterOptions.forEach { option ->
                        FilterChip(
                            selected = selectedFilter == option,
                            onClick = { selectedFilter = option },
                            label = { Text(option) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }

            if (filteredOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (orders.isEmpty()) "No orders yet" else "No orders match your search",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders) { order ->
                        order?.let {
                            OrderAdminRow(
                                order = it,
                                onStatusChange = { newStatus ->
                                    viewModel.updateOrderStatus(it.id, newStatus) { _, message ->
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        notificationViewModel.addNotification(
                                            NotificationModel(
                                                userId = it.userId,
                                                title = "Order status updated",
                                                message = "Your order for ${it.productName} is now $newStatus"
                                            )
                                        ) { _, _ -> }
                                    }
                                },
                                onDetailsClick = { selectedOrder = it }
                            )
                        }
                    }
                }
            }
        }
    }

    selectedOrder?.let { order ->
        OrderDetailDialog(order = order, onDismiss = { selectedOrder = null })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderAdminRow(order: OrderModel, onStatusChange: (String) -> Unit, onDetailsClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onDetailsClick),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(order.productName, fontWeight = FontWeight.Bold)
                    Text("Customer: ${order.userName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Qty: ${order.quantity}  ·  $${"%.2f".format(order.price)}  ·  ${order.paymentMethod}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable(onClick = onDetailsClick)
            )
            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = order.status,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Update status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    orderStatuses.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(statusColor(status), CircleShape)
                                )
                            },
                            onClick = { expanded = false; onStatusChange(status) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderDetailDialog(order: OrderModel, onDismiss: () -> Unit) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Order Details", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                DetailRow("Order ID", order.id.ifBlank { "—" })
                DetailRow("Product", order.productName)
                DetailRow("Customer", order.userName)
                DetailRow("Quantity", "${order.quantity}")
                DetailRow("Price", "$${"%.2f".format(order.price)}")
                DetailRow("Total", "$${"%.2f".format(order.price * order.quantity)}")
                DetailRow("Payment", order.paymentMethod)
                DetailRow("Status", order.status)
                DetailRow("Placed on", dateFormatter.format(Date(order.timestamp)))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        Text(value, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
fun AdminOrderPreview() {
    Brand_ShoeTheme { AdminOrderScreen(onBackClick = {}) }
}