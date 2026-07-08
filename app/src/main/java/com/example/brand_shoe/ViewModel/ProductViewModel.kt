package com.example.brand_shoe.ViewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.brand_shoe.Model.ProductModel
import com.example.brand_shoe.repo.ImageRepo
import com.example.brand_shoe.repo.ProductRepo

class ProductViewModel(
    private val repo: ProductRepo,
    private val imageRepo: ImageRepo
) : ViewModel() {

    fun addProduct(model: ProductModel, callback: (Boolean, String) -> Unit) =
        repo.addProduct(model, callback)

    fun updateProduct(id: String, model: ProductModel, callback: (Boolean, String) -> Unit) =
        repo.updateProduct(id, model, callback)

    fun deleteProduct(id: String, callback: (Boolean, String) -> Unit) =
        repo.deleteProduct(id, callback)

    fun getAllProducts(callback: (Boolean, String, List<ProductModel?>) -> Unit) =
        repo.getAllProducts(callback)

    fun uploadImage(context: Context, uri: Uri, callback: (Boolean, String) -> Unit) {
        imageRepo.uploadImage(context, uri) { url ->
            if (url != null) {
                callback(true, url)
            } else {
                callback(false, "Upload failed")
            }
        }
    }
}