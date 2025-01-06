package com.example.swipeassignment.ui.screens.add_product_screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.swipeassignment.R
import com.example.swipeassignment.ui.utils.SuccessDialog
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    modifier: Modifier = Modifier,
    viewModel: AddProductViewModel = koinViewModel(),
    onDismiss: () -> Unit,
) {


    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

//   Launcher to get images
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.onEvent(AddProductEvents.ImageUriChanged(uri))
        }
    }


//     In case of success in uploading data to server or to database, show a dialog.
    if (state.isSuccess) {
        SuccessDialog(
            isAddedToDb = state.isProductAddedToDb,
            productName = state.productName,
            productType = state.productType,
            productImage = state.imageUri.toString(),
            price = state.price,
            tax = state.tax,
            onDismiss = {
                viewModel.onEvent(AddProductEvents.OnDismiss)
                onDismiss()
            }
        )

    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.add_products)) })
        }
    ) { paddingValues ->


        ModalBottomSheet(
            onDismissRequest = {
                viewModel.onEvent(AddProductEvents.OnDismiss)
                onDismiss()
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
//                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )


                TextFieldWithValidation(
                    value = state.productName,
                    onValueChange = {
                        if (state.productTypeError || state.productName.isNotBlank()) {
                            viewModel.onEvent(AddProductEvents.ProductNameErrorChanged(false))
                        }
                        viewModel.onEvent(AddProductEvents.ProductNameChanged(it))
                    },
                    label = stringResource(R.string.product_name),
                    isError = state.productNameError,
                    errorMessage = stringResource(R.string.product_name_error_msg),
                    keyboardOptions = KeyboardOptions.Default,
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown for Product Type Selection
                ExposedDropdownMenuBox(
                    expanded = state.isDropdownMenuExpanded,
                    onExpandedChange = {
                        viewModel.onEvent(AddProductEvents.DropdownMenuExpandedChanged(it))
                    }
                ) {
                    OutlinedTextField(
                        value = state.productType,
                        onValueChange = {},
                        readOnly = true,
                        isError = state.productTypeError,
                        supportingText = {
                            if (state.productTypeError) {
                                Text(
                                    text = stringResource(R.string.please_select_product_type),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        label = { Text(text = stringResource(R.string.select_product_type)) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = stringResource(R.string.dropdown_for_product_type)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = state.isDropdownMenuExpanded,
                        onDismissRequest = {
                            viewModel.onEvent(AddProductEvents.DropdownMenuExpandedChanged(false))
                        }
                    ) {
                        state.productTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    viewModel.onEvent(AddProductEvents.ProductTypeChanged(type))
                                    if (state.productType != "Select Product Type") {
                                        viewModel.onEvent(
                                            AddProductEvents.ProductTypeErrorChanged(
                                                false
                                            )
                                        )
                                    }
                                    viewModel.onEvent(
                                        AddProductEvents.DropdownMenuExpandedChanged(
                                            false
                                        )
                                    )
                                }
                            )
                        }
                    }
                }


                TextFieldWithValidation(
                    value = state.price,
                    onValueChange = {

                        if (state.price.isNotBlank() || state.priceError) {
                            viewModel.onEvent(AddProductEvents.PriceErrorChanged(false))
                        }

                        if (it.all { char -> char.isDigit() || char == '.' }) {
                            viewModel.onEvent(AddProductEvents.PriceChanged(it))
                        }
                    },
                    label = stringResource(R.string.price),
                    isError = state.priceError,
                    errorMessage = stringResource(R.string.price_cannot_be_empty),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )


                TextFieldWithValidation(
                    value = state.tax,
                    onValueChange = {

                        if (state.tax.isNotBlank() && state.tax.toDouble() > 100) {
                            viewModel.onEvent(AddProductEvents.TaxErrorChanged(false))
                        }

                        if (it.all { char -> char.isDigit() || char == '.' }) {
                            viewModel.onEvent(AddProductEvents.TaxChanged(it))
                        }
                    },
                    label = stringResource(R.string.tax),
                    isError = state.taxError,

                    errorMessage = if (state.tax.isNotBlank() && state.tax.toDouble() > 100) stringResource(
                        R.string.tax_cannot_be_greater_than_100
                    )
                    else stringResource(R.string.tax_cannot_be_empty),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )


                if (state.imageUri != null) {


                    AsyncImage(
                        model = state.imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .padding(top = 8.dp),
                        contentScale = ContentScale.Crop
                    )

                }


                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.add_image))
                }



                Spacer(modifier = Modifier.height(16.dp))

                val color = when {
                    state.isLoading -> {
                        Color.DarkGray.copy(alpha = 0.5f)
                    }

                    state.isSuccess -> {
                        Color.Green
                    }

                    else -> {
                        MaterialTheme.colorScheme.primary
                    }
                }

                Button(
                    onClick = {
                        viewModel.onEvent(AddProductEvents.SubmitClicked(context))

                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color
                    )
                ) {
                    when {
                        state.isSuccess -> {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Completed",
                                tint = Color.White
                            )
                        }

                        state.isError -> {
                            Text(stringResource(R.string.error_occurred_try_later))
                        }

                        state.isLoading -> {
                            CircularProgressIndicator()
                        }

                        else -> {
                            Text(stringResource(R.string.submit_product))
                        }

                    }

                }


            }

        }


    }


}

// Composable function to use textfield and also validate it.
@Composable
fun TextFieldWithValidation(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorMessage: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {

    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = { Text(label) },
        isError = isError,
        supportingText = {
            if (isError) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
        },
        keyboardOptions = keyboardOptions,
        modifier = modifier
    )

}