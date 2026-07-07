package com.example.brand_shoe

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.brand_shoe.repo.UserRepoImpl
import com.example.brand_shoe.ui.theme.Brand_ShoeTheme

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Brand_ShoeTheme {
                AdminDashboardScreen(
                    onManageProducts = { startActivity(Intent(this, AdminProductActivity::class.java)) },
                    onManageOrders = { startActivity(Intent(this, AdminOrderActivity::class.java)) },
                    onManageUsers = { startActivity(Intent(this, AdminUserActivity::class.java)) },
                    onMyProfile = { startActivity(Intent(this, AdminProfileActivity::class.java)) },
                    onLogout = {
                        CartManager.clearCart()
                        UserRepoImpl().logout { _, _ -> }
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onManageProducts: () -> Unit,
    onManageOrders: () -> Unit,
    onManageUsers: () -> Unit,
    onMyProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("Admin Dashboard", fontWeight = FontWeight.ExtraBold)
                        Text("Manage your store", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = onMyProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "My Profile")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AdminMenuCard("Manage Products", "Add, edit or remove shoes", Icons.Default.Inventory, onManageProducts)
            AdminMenuCard("Manage Orders", "View and update order status", Icons.Default.ReceiptLong, onManageOrders)
            AdminMenuCard("Manage Users", "View and remove customer accounts", Icons.Default.People, onManageUsers)
        }
    }
}

@Composable
fun AdminMenuCard(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(52.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardPreview() {
    Brand_ShoeTheme { AdminDashboardScreen({}, {}, {}, {}, {}) }
}