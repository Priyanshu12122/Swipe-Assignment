package com.example.swipeassignment.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.swipeassignment.ui.screens.add_product_screen.AddProductScreen
import com.example.swipeassignment.ui.screens.list_screen.ListScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier
) {

    val navController = rememberNavController()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->


//        Navigation graph
        NavHost(
            modifier = modifier.padding(innerPadding),
            navController = navController,
            startDestination = NavRoutes.ListScreenRoute
        ) {

//            ListScreen
            composable<NavRoutes.ListScreenRoute> {
                ListScreen(
                    onAddProductClick = {
                        navController.navigate(NavRoutes.AddProductScreenRoute)
                    }
                )
            }

//            AddProduct screen
            composable<NavRoutes.AddProductScreenRoute> {
                AddProductScreen(
                    onDismiss = {
                        navController.navigateUp()
                    }
                )
            }

        }

    }

}