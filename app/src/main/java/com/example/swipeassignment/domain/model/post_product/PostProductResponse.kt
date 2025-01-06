package com.example.swipeassignment.domain.model.post_product

data class PostProductResponse(
    val message: String,
    val product_details: ProductDetails,
    val product_id: Int,
    val success: Boolean
)