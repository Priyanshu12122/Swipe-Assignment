package com.example.swipeassignment.ui.screens.list_screen

import com.example.swipeassignment.domain.model.get_list.ProductResponseItem


//  Screen state for ListScreen to hold UI state
data class ListScreenState(
    val searchQuery: String = "",
    val isSearchbarVisible: Boolean = false,
    val productsList: List<ProductResponseItem> = emptyList(),
)
