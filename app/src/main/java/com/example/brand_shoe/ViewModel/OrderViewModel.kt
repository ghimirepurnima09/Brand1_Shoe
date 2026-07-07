package com.example.brand_shoe.ViewModel

import androidx.lifecycle.ViewModel
import com.example.brand_shoe.Model.OrderModel
import com.example.brand_shoe.repo.OrderRepo

class OrderViewModel(val repo: OrderRepo) : ViewModel() {
    fun placeOrder(model: OrderModel, callback: (Boolean, String) -> Unit) = repo.placeOrder(model, callback)
    fun updateOrderStatus(id: String, status: String, callback: (Boolean, String) -> Unit) = repo.updateOrderStatus(id, status, callback)
    fun deleteOrder(id: String, callback: (Boolean, String) -> Unit) = repo.deleteOrder(id, callback)
    fun getAllOrders(callback: (Boolean, String, List<OrderModel?>) -> Unit) = repo.getAllOrders(callback)
}