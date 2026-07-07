package com.example.brand_shoe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.brand_shoe.Model.UserModel
import com.example.brand_shoe.ViewModel.UserViewModel
import com.example.brand_shoe.ViewModel.UserViewModelFactory
import com.example.brand_shoe.repo.UserRepoImpl
import com.example.brand_shoe.ui.theme.Brand_ShoeTheme
import com.google.firebase.auth.FirebaseAuth

class AdminProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Brand_ShoeTheme {
                AdminProfileScreen(
                    onBackClick = { finish() },
                    onLogout = {
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
fun AdminProfileScreen(onBackClick: () -> Unit, onLogout: () -> Unit) {
    val context = LocalContext.current
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(UserRepoImpl()))
    var profile by remember { mutableStateOf<UserModel?>(null) }
    var name by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        UserRepoImpl().getUserId(uid) { _, _, data ->
            profile = data
            name = data?.fName ?: ""
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
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(96.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ADMIN",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = CircleShape
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = profile?.email ?: "",
                onValueChange = {},
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = true,
                shape = CircleShape
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val current = profile
                    if (current == null || name.isBlank()) return@Button
                    isSaving = true
                    val updated = current.copy(fName = name.trim())
                    viewModel.editProfile(uid, updated) { _, message ->
                        isSaving = false
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = CircleShape
            ) {
                Text(if (isSaving) "SAVING..." else "Save Changes", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
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

@Preview(showBackground = true)
@Composable
fun AdminProfilePreview() {
    Brand_ShoeTheme { AdminProfileScreen(onBackClick = {}, onLogout = {}) }
}