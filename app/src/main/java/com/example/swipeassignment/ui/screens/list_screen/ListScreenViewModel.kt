package com.example.swipeassignment.ui.screens.list_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipeassignment.data.MainRepository
import com.example.swipeassignment.data.network.Response
import com.example.swipeassignment.domain.model.get_list.ProductResponseItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//  ViewModel For the ListScreen
class ListScreenViewModel(
    private val repository: MainRepository,
) : ViewModel() {


    private val _state: MutableStateFlow<ListScreenState> = MutableStateFlow(ListScreenState())
    val state: StateFlow<ListScreenState> = _state.asStateFlow()

//    Event function to handle all events in ListScreen
    fun onEvent(event: ListScreenEvents) {
        when (event) {

            is ListScreenEvents.SearchBarVisibilityChanged -> {
                _state.update {
                    it.copy(
                        isSearchbarVisible = event.isVisible
                    )
                }
            }

            is ListScreenEvents.SearchQueryChanged -> {
                _state.update {
                    it.copy(
                        searchQuery = event.searchQuery
                    )
                }
            }

            ListScreenEvents.OnSearchClick -> {
                val searchQuery = _state.value.searchQuery
                val searchedProductsList = _state.value.productsList.filter {
                    it.product_name.contains(searchQuery, ignoreCase = true) ||
                            it.product_type.contains(searchQuery, ignoreCase = true) ||
                            it.tax.toString().contains(searchQuery, ignoreCase = true) ||
                            it.price.toString().contains(searchQuery, ignoreCase = true)
                }

                _state.update {
                    it.copy(
                        productsList = searchedProductsList
                    )
                }
            }
        }
    }


    //    State flow with a read only state flow to hold the states of network response and
    //          hold the data and show it in the ListScreen
    private val _products: MutableStateFlow<Response<List<ProductResponseItem>>> =
        MutableStateFlow(Response.Loading())
    val products: StateFlow<Response<List<ProductResponseItem>>> = _products.asStateFlow()

    //    Function to get the products and update the UI
    fun getProducts() {
        viewModelScope.launch {
            try {
                _products.value = Response.Loading()
                val response = repository.getProductList()
                _products.value = Response.Success(response)
                _state.update {
                    it.copy(
                        productsList = response
                    )
                }
            } catch (e: Exception) {
                _products.value = Response.Error(e.localizedMessage ?: "Unknown error")

            }
        }
    }



}