package com.example.pszzapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    sheetState: SheetState,
    showBottomSheet: Boolean,
    setShowBottomSheet: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                setShowBottomSheet(false)
            },
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier.padding(bottom = 64.dp, start = 12.dp, end = 12.dp)
            ) {
                content()
            }
        }
    }
}