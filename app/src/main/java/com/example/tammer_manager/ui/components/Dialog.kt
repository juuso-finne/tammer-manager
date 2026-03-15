package com.example.tammer_manager.ui.components


import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.tammer_manager.R

@Composable
fun ErrorDialog(
    onDismissRequest: () -> Unit,
    errorText: String
){
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = { TextButton(
            onClick = { onDismissRequest() }
        ) {
            Text(stringResource(R.string.ok))
        }},
        text = { Text(errorText) },
        icon = { Icon(imageVector = Icons.Default.Info, contentDescription = stringResource(R.string.error)) }
    )
}

@Composable
fun ConfirmDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    confirmButtonText: String = stringResource(R.string.ok),
    dismissButtonText : String = stringResource(R.string.cancel),
    dialogText: String
){
    AlertDialog(
        onDismissRequest = onDismissRequest,

        confirmButton = { TextButton(
            onClick = { onConfirmRequest() }
        ) {
            Text(confirmButtonText)
        }},

        dismissButton = { TextButton(
            onClick = { onDismissRequest() }
        ) {
         Text(dismissButtonText)
        }},

        text = { Text(dialogText) },
        icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = stringResource(R.string.confirmation)) }
    )
}