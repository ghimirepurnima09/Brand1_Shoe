package com.example.brand_shoe

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brand_shoe.repo.UserRepoImpl
import com.example.brand_shoe.ui.theme.Brand_ShoeTheme
import com.google.firebase.auth.FirebaseAuth
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
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid == null) {
                            startActivity(Intent(this, Onboarding::class.java))
                            finish()
                        } else {
                            UserRepoImpl().getUserId(uid) { _, _, profile ->
                                val target = if (profile?.role == "admin") AdminDashboardActivity::class.java else HomePageActivity::class.java
                                startActivity(Intent(this, target))
                                finish()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SplashScreenContent(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(4000)
        onTimeout()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Image(
            painter = painterResource(id = R.drawable.shoe1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }),
            alpha = 0.6f
        )

        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.5f))
                )
            )
        )

        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("BRAND_SHOE", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = 4.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("STEP INTO STYLE", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.8f), letterSpacing = 6.sp)
        }

        Column(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.width(180.dp).height(1.5.dp).background(Color.White.copy(alpha = 0.7f)))
            Spacer(modifier = Modifier.height(12.dp))
            Text("LOADING PRECISION", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Light, color = Color.White.copy(alpha = 0.5f), letterSpacing = 3.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    Brand_ShoeTheme { SplashScreenContent(onTimeout = {}) }
}