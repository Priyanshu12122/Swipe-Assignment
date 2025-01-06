package com.example.swipeassignment.ui.screens.list_screen


//  Wrapper class for all events that could be performed in the ListScreen
sealed class ListScreenEvents {

    data class SearchQueryChanged(val searchQuery: String) : ListScreenEvents()
    data class SearchBarVisibilityChanged(val isVisible: Boolean) : ListScreenEvents()
    data object OnSearchClick: ListScreenEvents()
}