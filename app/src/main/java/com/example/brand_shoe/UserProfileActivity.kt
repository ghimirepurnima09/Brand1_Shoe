package com.example.brand_shoe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
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
import com.example.brand_shoe.Model.UserModel
import com.example.brand_shoe.ViewModel.OrderViewModel
import com.example.brand_shoe.ViewModel.OrderViewModelFactory
import com.example.brand_shoe.ViewModel.UserViewModel
import com.example.brand_shoe.ViewModel.UserViewModelFactory
import com.example.brand_shoe.repo.OrderRepoImpl
import com.example.brand_shoe.repo.UserRepoImpl
import com.example.brand_shoe.ui.theme.Brand_ShoeTheme
import com.google.firebase.auth.FirebaseAuth

class UserProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Brand_ShoeTheme {
                UserProfileScreen(
                    onBackClick = { finish() },
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
fun UserProfileScreen(onBackClick: () -> Unit, onLogout: () -> Unit) {
    val context = LocalContext.current
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(UserRepoImpl()))
    val orderViewModel: OrderViewModel = viewModel(factory = OrderViewModelFactory(OrderRepoImpl()))
    var profile by remember { mutableStateOf<UserModel?>(null) }
    var name by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var myOrders by remember { mutableStateOf<List<OrderModel?>>(emptyList()) }
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        UserRepoImpl().getUserId(uid) { _, _, data ->
            profile = data
            name = data?.fName ?: ""
        }
        orderViewModel.getAllOrders { _, _, data ->
            myOrders = data.filter { it?.userId == uid }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(20.dp)
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(88.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(profile?.fName?.ifBlank { "Your Profile" } ?: "Your Profile", style = MaterialTheme.typography.headlineSmall)
                    Text(profile?.email ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(28.dp))
            }

            item {
                Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Account Details", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = profile?.email ?: "",
                            onValueChange = {},
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            readOnly = true,
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val current = profile
                                if (current == null || name.isBlank()) return@Button
                                isSaving = true
                                val updated = current.copy(fName = name.trim())
                                userViewModel.editProfile(uid, updated) { _, message ->
                                    isSaving = false
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = !isSaving,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = CircleShape
                        ) {
                            Text(if (isSaving) "SAVING..." else "Save Changes", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("My Orders", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (myOrders.isEmpty()) {
                item {
                    Text("You haven't placed any orders yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            } else {
                items(myOrders) { order ->
                    order?.let {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(it.productName, fontWeight = FontWeight.Bold)
                                    Text("Qty: ${it.quantity} · $${"%.2f".format(it.price)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                AssistChip(onClick = {}, label = { Text(it.status) })
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            item {
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfilePreview() {
    Brand_ShoeTheme { UserProfileScreen(onBackClick = {}, onLogout = {}) }
}