package com.example.brand_shoe

import com.example.brand_shoe.Model.OrderModel

object OrderFilterHelper {

    /**
     * Filters orders by status and search query.
     * Extracted from AdminOrderScreen's `filteredOrders` remember block
     * so it can be unit tested without Compose or an emulator.
     */
    fun filter(
        orders: List<OrderModel?>,
        searchQuery: String,
        selectedFilter: String
    ): List<OrderModel?> {
        return orders.filter { order ->
            order != null &&
                    (selectedFilter == "All" || order.status.equals(selectedFilter, ignoreCase = true)) &&
                    (searchQuery.isBlank() ||
                            order.productName.contains(searchQuery, ignoreCase = true) ||
                            order.userName.contains(searchQuery, ignoreCase = true))
        }
    }
}