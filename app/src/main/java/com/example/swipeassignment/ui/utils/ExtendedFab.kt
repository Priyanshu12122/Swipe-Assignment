package com.example.swipeassignment.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.swipeassignment.R


@Composable
fun ExtendedFab(
    modifier: Modifier = Modifier,
    isScrollingDown: Boolean,
    onClick: () -> Unit,
) {

    ExtendedFloatingActionButton(
        onClick = { onClick() },
        expanded = !isScrollingDown,
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add)
            )
        },
        text = {
            Text(stringResource(R.string.add_item))
        },
        modifier = modifier
    )
}