package com.example.swipeassignment.data.network

import com.example.swipeassignment.domain.model.get_list.ProductResponseItem
import com.example.swipeassignment.domain.model.post_product.PostProductResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

//  Interface ApiService which uses retrofit to make network calls to GET, POST data.

interface ApiService {

    //    Get function to get the list of products which are to be shown in the ListScreen.
    @GET(NetworkConstants.GET_PRODUCT_URL)
    suspend fun getProductList(): List<ProductResponseItem>


    //    Post function to upload data to server.
    @Multipart
    @POST(NetworkConstants.POST_PRODUCT_URL)
    suspend fun postProduct(
        @Part("product_name") productName: RequestBody,
        @Part("product_type") productType: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tax") tax: RequestBody,
        @Part imageUri: MultipartBody.Part?,
    ): Response<PostProductResponse>

}