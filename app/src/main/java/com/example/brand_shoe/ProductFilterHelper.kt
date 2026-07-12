package com.example.brand_shoe

import com.example.brand_shoe.Model.ProductModel

object ProductFilterHelper {

    /**
     * Filters products by search query, then applies the selected sort.
     * Extracted from HomeDashboard's `remember(products, searchQuery, selectedSort)`
     * block so it can be unit tested without needing Compose or an emulator.
     */
    fun filterAndSort(
        products: List<ProductModel?>,
        searchQuery: String,
        selectedSort: String
    ): List<ProductModel?> {
        var result = if (searchQuery.isBlank()) {
            products
        } else {
            products.filter { it?.name?.contains(searchQuery, ignoreCase = true) == true }
        }

        result = when (selectedSort) {
            "Price: Low to High" -> result.sortedBy { it?.price ?: 0.0 }
            "Price: High to Low" -> result.sortedByDescending { it?.price ?: 0.0 }
            "In Stock Only" -> result.filter { (it?.stock ?: 0) > 0 }
            else -> result
        }
        return result
    }
}