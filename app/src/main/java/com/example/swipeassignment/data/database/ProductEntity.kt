package com.example.swipeassignment.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity for saving data in local db when internet is not available.
@Entity
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val productName: String,
    val productType: String,
    val price: String,
    val tax: String,
    val imageUri: String?,
)
