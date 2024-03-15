package com.example.pszzapp.data.util

import androidx.compose.ui.graphics.vector.ImageVector

data class DropdownMenuItemData(
    val icon: ImageVector,
    val text: String,
    val onClick: () -> Unit
)

