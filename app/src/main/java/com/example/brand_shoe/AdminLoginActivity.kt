package com.example.brand_shoe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.brand_shoe.ViewModel.UserViewModel
import com.example.brand_shoe.ViewModel.UserViewModelFactory
import com.example.brand_shoe.repo.UserRepoImpl
import com.example.brand_shoe.ui.theme.Brand_ShoeTheme
import com.google.firebase.auth.FirebaseAuth

class AdminLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Brand_ShoeTheme {
                AdminLoginContent(
                    onAdminLoginSuccess = {
                        startActivity(Intent(this, AdminDashboardActivity::class.java))
                        finish()
                    },
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@Composable
fun AdminLoginContent(onAdminLoginSuccess: () -> Unit, onBackClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(UserRepoImpl()))

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Admin Login", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Staff access only", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Admin Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                shape = CircleShape
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                singleLine = true,
                shape = CircleShape
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    viewModel.login(email.trim(), password) { success, message ->
                        if (success) {
                            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            UserRepoImpl().getUserId(uid) { _, _, profile ->
                                isLoading = false
                                if (profile?.role == "admin") {
                                    onAdminLoginSuccess()
                                } else {
                                    Toast.makeText(context, "This account does not have admin access", Toast.LENGTH_LONG).show()
                                    FirebaseAuth.getInstance().signOut()
                                }
                            }
                        } else {
                            isLoading = false
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = CircleShape
            ) {
                Text(if (isLoading) "CHECKING..." else "LOGIN AS ADMIN", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onBackClick) {
                Text("Back to customer login", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminLoginPreview() {
    Brand_ShoeTheme { AdminLoginContent(onAdminLoginSuccess = {}, onBackClick = {}) }
}