package com.example.brand_shoe.repo

import com.example.brand_shoe.Model.NotificationModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationRepoImpl : NotificationRepo {
    private val database = FirebaseDatabase.getInstance()
    private val ref = database.getReference("notifications")

    override fun addNotification(model: NotificationModel, callback: (Boolean, String) -> Unit) {
        val userRef = ref.child(model.userId)
        val id = userRef.push().key
        if (id == null) {
            callback(false, "Could not generate notification id")
            return
        }
        userRef.child(id).setValue(model.copy(id = id)).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Notification added")
            else callback(false, "${it.exception?.message}")
        }
    }

    override fun getNotificationsForUser(userId: String, callback: (Boolean, String, List<NotificationModel?>) -> Unit) {
        ref.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val all = mutableListOf<NotificationModel?>()
                for (item in snapshot.children) {
                    item.getValue(NotificationModel::class.java)?.let { all.add(it) }
                }
                callback(true, "Notifications fetched", all.sortedByDescending { it?.timestamp })
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message ?: "Failed to load notifications", emptyList())
            }
        })
    }

    override fun markAsRead(userId: String, notificationId: String, callback: (Boolean, String) -> Unit) {
        ref.child(userId).child(notificationId).child("read").setValue(true).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Marked as read")
            else callback(false, "${it.exception?.message}")
        }
    }

    override fun markAllAsRead(userId: String, callback: (Boolean, String) -> Unit) {
        ref.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updates = mutableMapOf<String, Any>()
                for (item in snapshot.children) {
                    updates["${item.key}/read"] = true
                }
                if (updates.isEmpty()) {
                    callback(true, "No notifications to update")
                    return
                }
                ref.child(userId).updateChildren(updates).addOnCompleteListener {
                    if (it.isSuccessful) callback(true, "All marked as read")
                    else callback(false, "${it.exception?.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message ?: "Failed to update notifications")
            }
        })
    }

    override fun deleteNotification(userId: String, notificationId: String, callback: (Boolean, String) -> Unit) {
        ref.child(userId).child(notificationId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Notification deleted")
            else callback(false, "${it.exception?.message}")
        }
    }
}