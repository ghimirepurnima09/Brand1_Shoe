package com.example.brand_shoe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

private const val ADMIN_EMAIL = "admin123@gmail.com"
private const val ADMIN_PASSWORD = "admin@123"

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Brand_ShoeTheme {
                LoginContent(
                    onNavigateHome = {
                        startActivity(Intent(this, HomePageActivity::class.java))
                        finish()
                    },
                    onNavigateAdmin = {
                        startActivity(Intent(this, AdminDashboardActivity::class.java))
                        finish()
                    },
                    onRegisterClick = { startActivity(Intent(this, RegistrationActivity::class.java)) },
                    onForgetPasswordClick = { startActivity(Intent(this, ForgetPasswordActivity::class.java)) }
                )
            }
        }
    }
}

@Composable
fun LoginContent(
    onNavigateHome: () -> Unit,
    onNavigateAdmin: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgetPasswordClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isAdminLoading by remember { mutableStateOf(false) }
    var waitingForRole by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(UserRepoImpl()))

    val userProfile by viewModel.users.observeAsState()
    val isViewModelLoading by viewModel.loading.observeAsState(false)

    LaunchedEffect(userProfile, isViewModelLoading) {
        if (waitingForRole && !isViewModelLoading) {
            if (userProfile != null) {
                waitingForRole = false
                isLoading = false
                isAdminLoading = false

                if (userProfile?.role?.lowercase() == "admin") {
                    onNavigateAdmin()
                } else {
                    onNavigateHome()
                }
            } else {
                waitingForRole = false
                isLoading = false
                isAdminLoading = false
                Toast.makeText(context, "Error: User profile not found in database. Please check your UID entry.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun startRoleLookup() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid.isNullOrBlank()) {
            isLoading = false
            isAdminLoading = false
            Toast.makeText(context, "Could not verify user", Toast.LENGTH_SHORT).show()
            return
        }
        waitingForRole = true
        viewModel.getUserId(uid)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to MaterialTheme.colorScheme.primaryContainer,
                    0.45f to MaterialTheme.colorScheme.background,
                    1f to MaterialTheme.colorScheme.background
                )
            )
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.Transparent) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(136.dp)
                        .shadow(16.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(122.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
                Text("Welcome Back", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Sign in to keep stepping into style",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
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
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(onClick = onForgetPasswordClick) {
                        Text("Forgot Password?", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        viewModel.login(email.trim(), password) { success, message ->
                            if (success) {
                                startRoleLookup()
                            } else {
                                isLoading = false
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !isLoading && !isAdminLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (isLoading) "SIGNING IN..." else "LOG IN", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("New here?", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = onRegisterClick) {
                        Text("Create Account", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        isAdminLoading = true
                        viewModel.login(ADMIN_EMAIL, ADMIN_PASSWORD) { success, message ->
                            if (success) {
                                startRoleLookup()
                            } else {
                                isAdminLoading = false
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !isLoading && !isAdminLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isAdminLoading) "SIGNING IN..." else "Login as Admin", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    Brand_ShoeTheme { LoginContent({}, {}, {}, {}) }
}