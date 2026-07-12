package com.example.brand_shoe

object ProductFormValidator {

    /**
     * Validates product form fields before saving.
     * Extracted from ProductFormDialog's confirmButton onClick logic.
     * Returns an error message, or null if all inputs are valid.
     */
    fun validate(name: String, price: String, stock: String): String? {
        if (name.isBlank()) return "Product name is required"

        val parsedPrice = price.toDoubleOrNull()
        if (parsedPrice == null || parsedPrice <= 0.0) return "Enter a valid price"

        val parsedStock = stock.toIntOrNull()
        if (parsedStock == null || parsedStock < 0) return "Enter a valid stock quantity"

        return null
    }
}