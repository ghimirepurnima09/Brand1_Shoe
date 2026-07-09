package com.example.brand_shoe.repo

import com.example.brand_shoe.Model.NotificationModel

interface NotificationRepo {
    fun addNotification(model: NotificationModel, callback: (Boolean, String) -> Unit)
    fun getNotificationsForUser(userId: String, callback: (Boolean, String, List<NotificationModel?>) -> Unit)
    fun markAsRead(userId: String, notificationId: String, callback: (Boolean, String) -> Unit)
    fun markAllAsRead(userId: String, callback: (Boolean, String) -> Unit)
    fun deleteNotification(userId: String, notificationId: String, callback: (Boolean, String) -> Unit)
}