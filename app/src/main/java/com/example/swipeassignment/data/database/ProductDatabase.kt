package com.example.swipeassignment.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

// Database class
@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)
abstract class ProductDatabase: RoomDatabase() {
    abstract val dao: ProductDao
}