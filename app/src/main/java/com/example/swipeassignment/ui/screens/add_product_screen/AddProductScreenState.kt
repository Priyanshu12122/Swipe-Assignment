package com.example.swipeassignment.ui.screens.add_product_screen

import android.net.Uri


// State class for handling the state of the AddProductScreen
data class AddProductScreenState(

    val productName: String = "",
    val productNameError: Boolean = false,

    val productType: String = "Select Product type",
    val productTypeError: Boolean = false,

    val price: String = "",
    val priceError: Boolean = false,

    val tax: String = "",
    val taxError: Boolean = false,

    val imageUri: Uri? = null,

    val productTypes: List<String> = listOf("Product", "Service", "Digital", "Subscription"),
    val isDropdownMenuExpanded: Boolean = false,


//    Network states
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val isSuccess: Boolean = false,

    val isProductAddedToDb: Boolean = false,
)
