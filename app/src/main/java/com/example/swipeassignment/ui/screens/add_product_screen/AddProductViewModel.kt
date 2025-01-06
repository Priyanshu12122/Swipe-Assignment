package com.example.swipeassignment.ui.screens.add_product_screen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipeassignment.data.MainRepository
import com.example.swipeassignment.data.NetworkMonitor
import com.example.swipeassignment.data.database.ProductEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddProductViewModel(
    private val repository: MainRepository,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {


    private val _state: MutableStateFlow<AddProductScreenState> = MutableStateFlow(
        AddProductScreenState()
    )
    val state: StateFlow<AddProductScreenState> = _state.asStateFlow()


//    Event function to handle all events in AddProductScreen
    fun onEvent(event: AddProductEvents) {

        when (event) {
            is AddProductEvents.ImageUriChanged -> {
                _state.update {
                    it.copy(
                        imageUri = event.imageUri
                    )
                }
            }

            is AddProductEvents.PriceChanged -> {
                _state.update {
                    it.copy(
                        price = event.price
                    )
                }
            }

            is AddProductEvents.PriceErrorChanged -> {
                _state.update {
                    it.copy(
                        priceError = event.priceError
                    )
                }
            }

            is AddProductEvents.ProductNameChanged -> {
                _state.update {
                    it.copy(
                        productName = event.productName
                    )
                }
            }

            is AddProductEvents.ProductNameErrorChanged -> {
                _state.update {
                    it.copy(
                        productNameError = event.productNameError
                    )
                }
            }

            is AddProductEvents.ProductTypeChanged -> {
                _state.update {
                    it.copy(
                        productType = event.productType
                    )
                }
            }

            is AddProductEvents.ProductTypeErrorChanged -> {
                _state.update {
                    it.copy(
                        productTypeError = event.productTypeError
                    )
                }
            }

            is AddProductEvents.TaxChanged -> {

                if (_state.value.tax.isNotBlank() && _state.value.tax.toDouble() >= 100) {
                    _state.update {
                        it.copy(
                            taxError = true
                        )
                    }
                }

                _state.update {
                    it.copy(
                        tax = event.tax
                    )
                }
            }

            is AddProductEvents.TaxErrorChanged -> {
                _state.update {
                    it.copy(
                        taxError = event.taxError
                    )
                }
            }

            is AddProductEvents.DropdownMenuExpandedChanged -> {
                _state.update {
                    it.copy(
                        isDropdownMenuExpanded = event.isDropdownMenuExpandedChanged
                    )
                }
            }

            is AddProductEvents.SubmitClicked -> {
                val currentState = _state.value

                val productNameError = currentState.productName.isBlank()
                val priceError = currentState.price.isBlank()
                val productTypeError = currentState.productType == "Select Product type"
                val taxError =
                    currentState.tax.isBlank() || _state.value.tax.isNotBlank() && currentState.tax.toDouble() >= 100

                // Updating state with error flags if any value is left empty
                _state.update {
                    it.copy(
                        productNameError = productNameError,
                        priceError = priceError,
                        productTypeError = productTypeError,
                        taxError = taxError
                    )
                }

                // Proceeding with submission only if there are no errors
                if (!productNameError && !priceError && !productTypeError && !taxError) {
                    postToServerOrSaveProductToDb(context = event.context)
                }
            }

            AddProductEvents.OnDismiss -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isError = false,
                        isSuccess = false
                    )
                }
            }
        }
    }

//  Function to post product to server.
    fun postProduct(context: Context) {
        viewModelScope.launch {
            try {

                _state.update {
                    it.copy(
                        isLoading = true
                    )
                }

                Log.d("TAG", "postProduct: imageUri = ${_state.value.imageUri}")

                val response = repository.postProduct(
                    productName = _state.value.productName,
                    productType = _state.value.productType,
                    price = _state.value.price,
                    tax = _state.value.tax,
                    imageUri = _state.value.imageUri,
                    context = context
                )

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true
                            )
                        }
                    }
                } else {
                    saveProductToDb()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = response.message(),
                            isSuccess = false
                        )
                    }
                }
            } catch (e: Exception) {
                saveProductToDb()
                _state.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = e.localizedMessage ?: "Unknown error",
                        isSuccess = false
                    )
                }
            }
        }
    }

//    Function to save product to Database, if internet is not available
    fun saveProductToDb() {
        viewModelScope.launch {

            val image = repository.createLocalCopy(_state.value.imageUri)


            val productEntity = ProductEntity(
                productName = _state.value.productName,
                productType = _state.value.productType,
                price = _state.value.price,
                tax = _state.value.tax,
                imageUri = image?.toString()
            )
            repository.insertProductEntity(productEntity)
            _state.update {
                it.copy(
                    isProductAddedToDb = true,
                    isSuccess = true,

                    )
            }

        }
    }


//    Function to post product to server or save product to database, if internet is not available
    fun postToServerOrSaveProductToDb(context: Context) {
        viewModelScope.launch {
            if (networkMonitor.isInternetAvailable()) {
                postProduct(context)
            } else {
                saveProductToDb()
            }
        }
    }


    val unsyncedProducts = repository.getAllProductEntity().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        observeNetworkAndSync()
    }

//  Function to observe network from NetworkMonitor class and sync unsynced products
//  whenever network becomes available
    private fun observeNetworkAndSync() {
        viewModelScope.launch {
            networkMonitor.observeNetwork().collect { isConnected ->
                if (isConnected) syncUnsyncedProducts()
            }
        }
    }

//    Function to sync unsynced products from database to server.
    private fun syncUnsyncedProducts() {
        viewModelScope.launch {
            unsyncedProducts.collect { products ->
                for (product in products) {
                    try {

                        repository.postProductInRepository(
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
    }


}