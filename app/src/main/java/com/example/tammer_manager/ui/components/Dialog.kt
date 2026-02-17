package com.example.tammer_manager.ui.components


import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

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
            Text("Ok")
        }},
        text = { Text(errorText) },
        icon = { Icon(imageVector = Icons.Default.Info, contentDescription = "Error") }
    )
}

@Composable
fun ConfirmDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    confirmButtonText: String = "OK",
    dismissButtonText : String = "Cancel",
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
        icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = "Confirmation") }
    )
}