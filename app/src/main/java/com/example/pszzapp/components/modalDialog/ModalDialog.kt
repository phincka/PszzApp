package com.example.pszzapp.components.modalDialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.example.pszzapp.presentation.auth.base.Button

@Composable
fun ModalDialog(
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
    isModalActive: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit = { _ -> },
    confirmButtonText: String,
    dismissButtonText: String,
) {
    if (!isModalActive) return

    AlertDialog(
        icon = {
            Icon(
                icon,
                contentDescription = null,
            )
        },
        title = {
            Text(
                text = dialogTitle,
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Text(
                text = dialogText,
                textAlign = TextAlign.Center,
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            Button(
                text = confirmButtonText,
                onClick = { onConfirmation("") },
            )
        },
        dismissButton = {
            Button(
                text = dismissButtonText,
                onClick = { onDismissRequest() },
            )
        }
    )
}