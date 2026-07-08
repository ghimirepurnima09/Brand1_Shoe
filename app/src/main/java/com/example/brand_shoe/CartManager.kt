package com.example.brand_shoe

import androidx.compose.runtime.mutableStateListOf

data class CartItem(
    val productId: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    var quantity: Int = 1
)

object CartManager {
    val cartItems = mutableStateListOf<CartItem>()

    val cartCount: Int
        get() = cartItems.sumOf { it.quantity }

    fun addToCart(productId: String, name: String, price: Double, imageUrl: String) {
        val index = cartItems.indexOfFirst { it.productId == productId }
        if (index >= 0) {
            cartItems[index] = cartItems[index].copy(quantity = cartItems[index].quantity + 1)
        } else {
            cartItems.add(CartItem(productId, name, price, imageUrl, 1))
        }
    }

    fun increaseQuantity(productId: String) {
        val index = cartItems.indexOfFirst { it.productId == productId }
        if (index >= 0) {
            cartItems[index] = cartItems[index].copy(quantity = cartItems[index].quantity + 1)
        }
    }

    fun decreaseQuantity(productId: String) {
        val index = cartItems.indexOfFirst { it.productId == productId }
        if (index >= 0) {
            val item = cartItems[index]
            if (item.quantity > 1) {
                cartItems[index] = item.copy(quantity = item.quantity - 1)
            } else {
                cartItems.removeAt(index)
            }
        }
    }

    fun removeFromCart(productId: String) {
        cartItems.removeAll { it.productId == productId }
    }

    fun totalPrice(): Double = cartItems.sumOf { it.price * it.quantity }

    fun clearCart() {
        cartItems.clear()
    }
}