package com.example.brand_shoe.ViewModel

import androidx.lifecycle.ViewModel
import com.example.brand_shoe.Model.ProductModel
import com.example.brand_shoe.repo.ProductRepo

class ProductViewModel(val repo: ProductRepo) : ViewModel() {
    fun addProduct(model: ProductModel, callback: (Boolean, String) -> Unit) = repo.addProduct(model, callback)
    fun updateProduct(id: String, model: ProductModel, callback: (Boolean, String) -> Unit) = repo.updateProduct(id, model, callback)
    fun deleteProduct(id: String, callback: (Boolean, String) -> Unit) = repo.deleteProduct(id, callback)
    fun getAllProducts(callback: (Boolean, String, List<ProductModel?>) -> Unit) = repo.getAllProducts(callback)
}