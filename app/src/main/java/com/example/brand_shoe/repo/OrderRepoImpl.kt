package com.example.brand_shoe.repo

import com.example.brand_shoe.Model.OrderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderRepoImpl : OrderRepo {
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("orders")

    override fun placeOrder(model: OrderModel, callback: (Boolean, String) -> Unit) {
        val id = ref.push().key
        if (id == null) {
            callback(false, "Could not generate order id")
            return
        }
        ref.child(id).setValue(model.copy(id = id)).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Order placed successfully")
            else callback(false, "${it.exception?.message}")
        }
    }

    override fun updateOrderStatus(id: String, status: String, callback: (Boolean, String) -> Unit) {
        ref.child(id).child("status").setValue(status).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Order status updated")
            else callback(false, "${it.exception?.message}")
        }
    }

    override fun deleteOrder(id: String, callback: (Boolean, String) -> Unit) {
        ref.child(id).removeValue().addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Order deleted")
            else callback(false, "${it.exception?.message}")
        }
    }

    override fun getAllOrders(callback: (Boolean, String, List<OrderModel?>) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val all = mutableListOf<OrderModel?>()
                for (item in snapshot.children) {
                    item.getValue(OrderModel::class.java)?.let { all.add(it) }
                }
                callback(true, "Orders fetched", all.sortedByDescending { it?.timestamp })
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message ?: "Failed to load orders", emptyList())
            }
        })
    }
}