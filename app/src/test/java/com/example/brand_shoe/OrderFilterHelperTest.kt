package com.example.brand_shoe

import com.example.brand_shoe.Model.OrderModel
import org.junit.Assert.assertEquals
import org.junit.Test

class OrderFilterHelperTest {

    private val sampleOrders = listOf(
        OrderModel(id = "1", productName = "Nike Air Max", userName = "Alice", status = "Pending"),
        OrderModel(id = "2", productName = "Adidas Ultraboost", userName = "Bob", status = "Shipped"),
        OrderModel(id = "3", productName = "Nike Zoom", userName = "Charlie", status = "Delivered")
    )

    @Test
    fun filterAll_returnsAllOrders() {
        val result = OrderFilterHelper.filter(sampleOrders, "", "All")
        assertEquals(3, result.size)
    }

    @Test
    fun filterByStatus_returnsOnlyMatching() {
        val result = OrderFilterHelper.filter(sampleOrders, "", "Shipped")
        assertEquals(1, result.size)
        assertEquals("Adidas Ultraboost", result.first()?.productName)
    }

    @Test
    fun searchByProductName_filtersCorrectly() {
        val result = OrderFilterHelper.filter(sampleOrders, "nike", "All")
        assertEquals(2, result.size)
    }

    @Test
    fun searchByCustomerName_filtersCorrectly() {
        val result = OrderFilterHelper.filter(sampleOrders, "bob", "All")
        assertEquals(1, result.size)
        assertEquals("Bob", result.first()?.userName)
    }

    @Test
    fun searchAndStatusCombined_appliesBoth() {
        val result = OrderFilterHelper.filter(sampleOrders, "nike", "Pending")
        assertEquals(1, result.size)
        assertEquals("Nike Air Max", result.first()?.productName)
    }

    @Test
    fun noMatch_returnsEmptyList() {
        val result = OrderFilterHelper.filter(sampleOrders, "puma", "All")
        assertEquals(0, result.size)
    }
}