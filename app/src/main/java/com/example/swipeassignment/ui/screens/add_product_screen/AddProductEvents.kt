package com.example.swipeassignment.ui.screens.add_product_screen

import android.content.Context
import android.net.Uri

// Wrapper class for all the events that could be performed in the AddProductScreen
sealed class AddProductEvents {

    data class ProductNameChanged(val productName: String): AddProductEvents()
    data class ProductNameErrorChanged(val productNameError: Boolean): AddProductEvents()

    data class ProductTypeChanged(val productType: String): AddProductEvents()
    data class ProductTypeErrorChanged(val productTypeError: Boolean): AddProductEvents()

    data class PriceChanged(val price: String): AddProductEvents()
    data class PriceErrorChanged(val priceError: Boolean): AddProductEvents()

    data class TaxChanged(val tax: String): AddProductEvents()
    data class TaxErrorChanged(val taxError: Boolean): AddProductEvents()

    data class ImageUriChanged(val imageUri: Uri): AddProductEvents()
    data class DropdownMenuExpandedChanged(val isDropdownMenuExpandedChanged: Boolean): AddProductEvents()

    data object OnDismiss: AddProductEvents()

    data class SubmitClicked(val context: Context): AddProductEvents()



}