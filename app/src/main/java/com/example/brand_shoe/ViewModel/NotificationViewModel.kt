package com.example.brand_shoe.ViewModel

import androidx.lifecycle.ViewModel
import com.example.brand_shoe.Model.NotificationModel
import com.example.brand_shoe.repo.NotificationRepo

class NotificationViewModel(private val repo: NotificationRepo) : ViewModel() {
    fun addNotification(model: NotificationModel, callback: (Boolean, String) -> Unit) =
        repo.addNotification(model, callback)

    fun getNotificationsForUser(userId: String, callback: (Boolean, String, List<NotificationModel?>) -> Unit) =
        repo.getNotificationsForUser(userId, callback)

    fun markAsRead(userId: String, notificationId: String, callback: (Boolean, String) -> Unit) =
        repo.markAsRead(userId, notificationId, callback)

    fun markAllAsRead(userId: String, callback: (Boolean, String) -> Unit) =
        repo.markAllAsRead(userId, callback)

    fun deleteNotification(userId: String, notificationId: String, callback: (Boolean, String) -> Unit) =
        repo.deleteNotification(userId, notificationId, callback)
}