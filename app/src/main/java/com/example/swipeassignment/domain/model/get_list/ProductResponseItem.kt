package com.example.swipeassignment.domain.model.get_list

data class ProductResponseItem(
    val image: String,
    val price: Double,
    val product_name: String,
    val product_type: String,
    val tax: Double
)