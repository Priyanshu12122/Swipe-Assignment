package com.example.swipeassignment.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.example.swipeassignment.data.database.ProductDao
import com.example.swipeassignment.data.database.ProductEntity
import com.example.swipeassignment.data.network.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


// Repository class to access all function of local databases or from api's , retrofit.
class MainRepository(
    private val api: ApiService,
    private val db: ProductDao,
    private val networkMonitor: NetworkMonitor,
    private val context: Context,
) {

//    Function to fetch the products list for list screen
    suspend fun getProductList() = api.getProductList()

//    function to post the product.
    suspend fun postProduct(
        productName: String,
        productType: String,
        price: String,
        tax: String,
        imageUri: Uri?,
        context: Context,
    ) = api.postProduct(
        productName = productName.toRequestBody("text/plain".toMediaType()),
        productType = productType.toRequestBody("text/plain".toMediaType()),
        price = price.toRequestBody("text/plain".toMediaType()),
        tax = tax.toRequestBody("text/plain".toMediaType()),
        imageUri = imageUri?.let { uri ->
            val file = getFileFromUri(context, uri)
            val requestFile = file.asRequestBody("image/*".toMediaType())
            MultipartBody.Part.createFormData("files[]", file.name, requestFile)
        }
    )


//    function to post the product from workmanager, or whenever the network becomes available,
//    and delete the productEntity from database if it is successful
    suspend fun postProductInRepository(
        id: Int,
        productName: String,
        productType: String,
        price: String,
        tax: String,
        imageUri: String?,
    ) {

        val response = api.postProduct(
            productName = productName.toRequestBody("text/plain".toMediaType()),
            productType = productType.toRequestBody("text/plain".toMediaType()),
            price = price.toRequestBody("text/plain".toMediaType()),
            tax = tax.toRequestBody("text/plain".toMediaType()),
            imageUri = imageUri?.let
            { uri ->
                val file = File(imageUri)
                val requestFile = file.asRequestBody("image/*".toMediaType())
                MultipartBody.Part.createFormData("files[]", file.name, requestFile)
            }
        )
        if (response.isSuccessful) {
            db.deleteProductEntity(id)
        }
    }


//    Function to make a file from image URI so as to upload to the server.
    private fun getFileFromUri(context: Context, uri: Uri): File {
        val fileExtension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(context.contentResolver.getType(uri))
        val fileName = "image_${System.currentTimeMillis()}.$fileExtension"
        val tempFile = File(context.cacheDir, fileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }


// Function to insert the product to local database when internet is not available
    suspend fun insertProductEntity(productEntity: ProductEntity) =
        db.insertProductEntity(productEntity)

//    Function to get all the products in local database to upload them to server, once an
//    internet connection becomes available.
    fun getAllProductEntity() = db.getAllProductEntity()

//    Function to upload the products from database to server.
    suspend fun syncUnsyncedProducts() {
        db.getAllProductEntity().collect { products: List<ProductEntity> ->
            for (product in products) {
                try {
                    postProductInRepository(
                        id = product.id!!,
                        productName = product.productName,
                        productType = product.productType,
                        price = product.price,
                        tax = product.tax,
                        imageUri = product.imageUri,
                    )
                } catch (_: Exception) {
                }
            }
        }
    }


    // Function to create local copy of the image URI so as to save it to database
//    as to avoid security exception when saving full URI to database.

    fun createLocalCopy(uri: Uri?): File? {
        if (uri == null) {
            return null
        }
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "image_${System.currentTimeMillis()}.jpg")

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file
    }



}
