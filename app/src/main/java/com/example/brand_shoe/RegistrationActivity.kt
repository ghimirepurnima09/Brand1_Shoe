package com.example.brand_shoe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import com.example.brand_shoe.Model.UserModel
import com.example.brand_shoe.ViewModel.UserViewModel
import com.example.brand_shoe.ViewModel.UserViewModelFactory
import com.example.brand_shoe.repo.UserRepoImpl
import com.example.brand_shoe.ui.theme.Brand_ShoeTheme

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Brand_ShoeTheme {
                RegistrationContent(
                    onRegisterSuccess = {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onLoginClick = {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun RegistrationContent(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(UserRepoImpl()))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to MaterialTheme.colorScheme.primaryContainer,
                    0.35f to MaterialTheme.colorScheme.background,
                    1f to MaterialTheme.colorScheme.background
                )
            )
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.Transparent) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Create Account", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Join us and find your perfect pair",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(28.dp))

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
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
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                                    )
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                when {
                                    name.isBlank() || email.isBlank() || password.isBlank() -> {
                                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }
                                    password != confirmPassword -> {
                                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                    }
                                    password.length < 6 -> {
                                        Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> {
                                        isLoading = true
                                        viewModel.register(email.trim(), password) { success, message, uid ->
                                            if (success) {
                                                val profile = UserModel(
                                                    id = uid,
                                                    fName = name.trim(),
                                                    email = email.trim(),
                                                    role = "customer"
                                                )
                                                viewModel.addUser(uid, profile) { savedOk, savedMsg ->
                                                    isLoading = false
                                                    if (savedOk) {
                                                        Toast.makeText(context, "account create successfully", Toast.LENGTH_LONG).show()
                                                        onRegisterSuccess()
                                                    } else {
                                                        Toast.makeText(context, savedMsg, Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            } else {
                                                isLoading = false
                                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(8.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(if (isLoading) "CREATING..." else "REGISTER", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Already have an account?", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = onLoginClick) {
                        Text("Log In Now", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationPreview() {
    Brand_ShoeTheme { RegistrationContent({}, {}) }
}