package com.example.swipeassignment.ui.screens.list_screen

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.swipeassignment.R
import com.example.swipeassignment.data.network.Response
import com.example.swipeassignment.domain.model.get_list.ProductResponseItem
import com.example.swipeassignment.ui.utils.ExtendedFab
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// The list screen which is used to display list of products
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    onAddProductClick: () -> Unit,
    viewModel: ListScreenViewModel = koinViewModel(),
) {

    // Data will be fetched when the screen is launched
    LaunchedEffect(Unit) {
        viewModel.getProducts()
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    val getProductsState by viewModel.products.collectAsStateWithLifecycle()
    val result = getProductsState

    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()
    val isScrollingDown by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {

            if (state.isSearchbarVisible) {

                SearchBar(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    searchQuery = state.searchQuery,
                    onSearchQueryChanged = {
                        viewModel.onEvent(ListScreenEvents.SearchQueryChanged(it))
                        scope.launch {
                            delay(500)
                            viewModel.onEvent(ListScreenEvents.OnSearchClick)
                        }
                    },
                    onSearch = {
                        viewModel.onEvent(ListScreenEvents.OnSearchClick)
                    },
                    onCrossClick = {
                        viewModel.onEvent(ListScreenEvents.SearchBarVisibilityChanged(false))
                        viewModel.onEvent(ListScreenEvents.SearchQueryChanged(""))
                    }
                )

            } else {

                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.products_list))
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(ListScreenEvents.SearchBarVisibilityChanged(true))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search_icon)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }


        },
        floatingActionButton = {

            ExtendedFab(
                isScrollingDown = isScrollingDown,
                onClick = {
                    onAddProductClick()
                }
            )

        }

    ) {

        AnimatedContent(
            targetState = result,
            modifier = modifier.padding(it),
            label = stringResource(R.string.list_screen_animation)
        ) { targetState ->

            when (targetState) {

//                If in case of error in fetching data from server,
//                show appropriate UI with a retry button to retry the network call
                is Response.Error -> {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text("Error occurred in fetching products, \n Error = ${targetState.msg}, Please retry")

                        Spacer(modifier = Modifier.height(25.dp))

                        OutlinedButton(
                            onClick = {
                                viewModel.getProducts()
                            }
                        ) {
                            Text(stringResource(R.string.retry))
                        }

                    }

                }
//              If loading, show a progress bar, that is, circular progress indicator
                is Response.Loading -> {

                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }

                }

//                In case of success, show the list of products in a lazy column
                is Response.Success -> {

                    val list = if (state.searchQuery.isEmpty()) targetState.data else state.productsList

                    LazyColumn(
                        state = listState,
                    ) {
                        items(list) {
                            ProductItem(it)
                        }
                    }

                }
            }

        }

    }

}


//  UI For each Product item to be displayed in lazy column
@Composable
fun ProductItem(product: ProductResponseItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = if (product.image.isNotBlank()) product.image else R.drawable.placeholder_image,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column {
                Text(
                    text = product.product_name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = product.product_type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "₹${product.price}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Tax: ${product.tax}%",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// Search bar for searching the products list
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onCrossClick: () -> Unit,
) {

    OutlinedTextField(
        modifier = modifier,
        value = searchQuery,
        onValueChange = {
            onSearchQueryChanged(it)
        },
        placeholder = {
            Text(stringResource(R.string.search_for_products))
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
            }
        ),
        shape = RoundedCornerShape(8.dp),
        trailingIcon = {
            IconButton(
                onClick = {
                    onCrossClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_icon)
                )

            }
        }

    )

}

