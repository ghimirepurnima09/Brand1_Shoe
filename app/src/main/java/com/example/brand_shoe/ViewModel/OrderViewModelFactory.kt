package com.example.brand_shoe.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.brand_shoe.repo.OrderRepo

class OrderViewModelFactory(private val repo: OrderRepo) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) return OrderViewModel(repo) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}