package com.example.swipeassignment.navigation

import kotlinx.serialization.Serializable


//  All the navigation routes which will help in navigating between the screens
@Serializable
sealed class NavRoutes {

    @Serializable
    data object ListScreenRoute : NavRoutes()

    @Serializable
    data object AddProductScreenRoute : NavRoutes()

}