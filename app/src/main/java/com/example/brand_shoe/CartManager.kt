package com.example.brand_shoe

import androidx.compose.runtime.mutableStateOf

object CartManager {
    val cartCount = mutableStateOf(0)

    fun addToCart() {
        cartCount.value += 1
    }

    fun clearCart() {
        cartCount.value = 0
    }
}