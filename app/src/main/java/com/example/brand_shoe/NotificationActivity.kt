package com.example.brand_shoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.brand_shoe.Model.NotificationModel
import com.example.brand_shoe.ViewModel.NotificationViewModel
import com.example.brand_shoe.ViewModel.NotificationViewModelFactory
import com.example.brand_shoe.repo.NotificationRepoImpl
import com.example.brand_shoe.ui.theme.Brand_ShoeTheme
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.TimeUnit

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Brand_ShoeTheme { NotificationScreen(onBackClick = { finish() }) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(onBackClick: () -> Unit) {
    val viewModel: NotificationViewModel = viewModel(factory = NotificationViewModelFactory(NotificationRepoImpl()))
    var notifications by remember { mutableStateOf<List<NotificationModel?>>(emptyList()) }
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        if (uid.isNotBlank()) {
            viewModel.getNotificationsForUser(uid) { _, _, data -> notifications = data }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    if (notifications.any { it?.read == false }) {
                        IconButton(onClick = { viewModel.markAllAsRead(uid) { _, _ -> } }) {
                            Icon(Icons.Default.DoneAll, contentDescription = "Mark all as read")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (notifications.isEmpty()) {
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No notifications yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications) { notification ->
                    notification?.let {
                        NotificationRow(
                            notification = it,
                            onClick = {
                                if (!it.read) viewModel.markAsRead(uid, it.id) { _, _ -> }
                            },
                            onDelete = {
                                viewModel.deleteNotification(uid, it.id) { _, _ -> }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationRow(notification: NotificationModel, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.read) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            if (!notification.read) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp, end = 10.dp)
                        .size(8.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            } else {
                Spacer(modifier = Modifier.width(18.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(notification.title, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(notification.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    timeAgo(notification.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
            }
        }
    }
}

private fun timeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m ago"
        diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
        else -> "${TimeUnit.MILLISECONDS.toDays(diff)}d ago"
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationPreview() {
    Brand_ShoeTheme { NotificationScreen(onBackClick = {}) }
}