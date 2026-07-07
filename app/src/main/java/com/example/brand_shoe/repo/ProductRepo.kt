package com.example.brand_shoe.repo

import com.example.brand_shoe.Model.ProductModel

interface ProductRepo {
    fun addProduct(model: ProductModel, callback: (Boolean, String) -> Unit)
    fun updateProduct(id: String, model: ProductModel, callback: (Boolean, String) -> Unit)
    fun deleteProduct(id: String, callback: (Boolean, String) -> Unit)
    fun getAllProducts(callback: (Boolean, String, List<ProductModel?>) -> Unit)
}