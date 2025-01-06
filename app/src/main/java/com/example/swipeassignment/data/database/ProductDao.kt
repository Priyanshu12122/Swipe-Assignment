package com.example.swipeassignment.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow


//  Dao class for saving products in local database when internet is not available.

@Dao
interface ProductDao {

    @Upsert
    suspend fun insertProductEntity(productEntity: ProductEntity)

    @Query("DELETE FROM ProductEntity where id =:id")
    suspend fun deleteProductEntity(id: Int)


    @Query("SELECT * FROM ProductEntity")
    fun getAllProductEntity(): Flow<List<ProductEntity>>

}