package com.example.brand_shoe

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brand_shoe.ui.theme.Brand_ShoeTheme
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Brand_ShoeTheme {
                SplashScreenContent(
                    onTimeout = {
                        // Navigate to Log In after splash
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun SplashScreenContent(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(4000) // 4 seconds delay
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // circular Logo - removed shoe images as requested
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Brand Shoe Logo",
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "BRAND SHOE",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1A1A),
                letterSpacing = 8.sp
            )
        }
        
        Text(
            text = "Step into Luxury",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            letterSpacing = 2.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    Brand_ShoeTheme {
        SplashScreenContent(onTimeout = {})
    }
}
