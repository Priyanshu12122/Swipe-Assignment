package com.example.swipeassignment.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.swipeassignment.R

@Composable
fun SuccessDialog(
    isAddedToDb: Boolean,
    productImage: String?,
    productType: String,
    productName: String,
    tax: String,
    price: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() },
            ) {
                Text("OK")
            }
        },
        title = {
            Text(
                text = if (isAddedToDb) "Product Added To Database Successfully!" else "Product Added Successfully!",
                fontWeight = FontWeight.Bold
            )
        },
        text = {

            Column(
                modifier = Modifier
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsyncImage(
                    model = if (productImage?.isNotBlank() == true) productImage else painterResource(R.drawable.placeholder_image),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Product Name: $productName",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 19.sp
                )
                Text(
                    "Product Type: $productType",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 19.sp
                )
                Text(
                    "Price: ₹$price",
                    fontWeight = FontWeight.Medium,
                    fontSize = 19.sp
                )
                Text(
                    "Tax: $tax%",
                    fontWeight = FontWeight.Medium,
                    fontSize = 19.sp
                )

                if (isAddedToDb) {
                    Text(
                        text = "As internet connection in not available, so product is added to local database and will get uploaded to server as soon as there is an internet connection",
                        modifier = Modifier.padding(12.dp)
                    )
                }

            }
        }
    )
}
