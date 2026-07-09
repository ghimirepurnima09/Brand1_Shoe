package com.example.brand_shoe.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.brand_shoe.repo.NotificationRepo

class NotificationViewModelFactory(private val repo: NotificationRepo) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            return NotificationViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}