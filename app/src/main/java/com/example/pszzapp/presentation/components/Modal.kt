package com.example.pszzapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pszzapp.R
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography

@Composable
fun Modal(
    options: List<Int>,
    isModalActive: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: (Int) -> Unit,
) {
    if (!isModalActive) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.neutral100.copy(alpha = 0.65f))
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onDismissRequest() })
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .graphicsLayer {
                    shadowElevation = 4.dp.toPx()
                    shape = RoundedCornerShape(8.dp)
                    clip = false
                }
                .clip(RoundedCornerShape(12.dp))
                .background(AppTheme.colors.white)
                .padding(horizontal = 16.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            options.forEachIndexed { index, selectionOption ->
                Text(
                    text = stringResource(selectionOption),
                    style = Typography.p,
                    color = AppTheme.colors.neutral90,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onConfirmation(index) }
                        .background(AppTheme.colors.neutral10)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }
        }
    }
}
