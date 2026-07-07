package com.example.brand_shoe.Model

data class OrderModel(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val productId: String = "",
    val productName: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val status: String = "Pending",
    val paymentMethod: String = "Cash on Delivery",
    val timestamp: Long = System.currentTimeMillis()
)