package com.example.brand_shoe.repo

import com.example.brand_shoe.Model.ProductModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductRepoImpl : ProductRepo {
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("products")

    override fun addProduct(model: ProductModel, callback: (Boolean, String) -> Unit) {
        val id = ref.push().key
        if (id == null) {
            callback(false, "Could not generate product id")
            return
        }
        ref.child(id).setValue(model.copy(id = id)).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Product added successfully")
            else callback(false, "${it.exception?.message}")
        }
    }

    override fun updateProduct(id: String, model: ProductModel, callback: (Boolean, String) -> Unit) {
        ref.child(id).updateChildren(model.toMap()).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Product updated successfully")
            else callback(false, "${it.exception?.message}")
        }
    }

    override fun deleteProduct(id: String, callback: (Boolean, String) -> Unit) {
        ref.child(id).removeValue().addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Product deleted successfully")
            else callback(false, "${it.exception?.message}")
        }
    }

    override fun getAllProducts(callback: (Boolean, String, List<ProductModel?>) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val all = mutableListOf<ProductModel?>()
                for (item in snapshot.children) {
                    item.getValue(ProductModel::class.java)?.let { all.add(it) }
                }
                callback(true, "Products fetched", all)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message ?: "Failed to load products", emptyList())
            }
        })
    }
}