package com.example.brand_shoe.repo

import com.example.brand_shoe.Model.OrderModel

interface OrderRepo {
    fun placeOrder(model: OrderModel, callback: (Boolean, String) -> Unit)
    fun updateOrderStatus(id: String, status: String, callback: (Boolean, String) -> Unit)
    fun deleteOrder(id: String, callback: (Boolean, String) -> Unit)
    fun getAllOrders(callback: (Boolean, String, List<OrderModel?>) -> Unit)
}