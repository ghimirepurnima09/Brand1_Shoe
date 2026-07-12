package com.example.brand_shoe

import com.example.brand_shoe.Model.ProductModel
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductFilterHelperTest {

    private val sampleProducts = listOf(
        ProductModel(id = "1", name = "Nike Air Max", price = 120.0, stock = 5),
        ProductModel(id = "2", name = "Adidas Ultraboost", price = 90.0, stock = 0),
        ProductModel(id = "3", name = "Nike Zoom", price = 150.0, stock = 3)
    )

    @Test
    fun blankQuery_returnsAllProducts() {
        val result = ProductFilterHelper.filterAndSort(sampleProducts, "", "Newest")
        assertEquals(3, result.size)
    }

    @Test
    fun searchQuery_filtersByName() {
        val result = ProductFilterHelper.filterAndSort(sampleProducts, "nike", "Newest")
        assertEquals(2, result.size)
    }

    @Test
    fun searchQuery_noMatch_returnsEmpty() {
        val result = ProductFilterHelper.filterAndSort(sampleProducts, "puma", "Newest")
        assertEquals(0, result.size)
    }

    @Test
    fun sortLowToHigh_ordersByPriceAscending() {
        val result = ProductFilterHelper.filterAndSort(sampleProducts, "", "Price: Low to High")
        assertEquals("Adidas Ultraboost", result.first()?.name)
        assertEquals("Nike Zoom", result.last()?.name)
    }

    @Test
    fun sortHighToLow_ordersByPriceDescending() {
        val result = ProductFilterHelper.filterAndSort(sampleProducts, "", "Price: High to Low")
        assertEquals("Nike Zoom", result.first()?.name)
        assertEquals("Adidas Ultraboost", result.last()?.name)
    }

    @Test
    fun inStockOnly_excludesZeroStockItems() {
        val result = ProductFilterHelper.filterAndSort(sampleProducts, "", "In Stock Only")
        assertEquals(2, result.size)
        assertEquals(false, result.any { it?.stock == 0 })
    }
}