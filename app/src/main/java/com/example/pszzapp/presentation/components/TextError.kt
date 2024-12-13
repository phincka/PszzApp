package com.example.pszzapp.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.AppTypography
import com.example.pszzapp.ui.theme.Typography

@Composable
fun TextError(
    text: String
) {
    Text(
        text = text,
        color = AppTheme.colors.primary50,
        style = Typography.p,
        modifier = Modifier.padding(bottom = 16.dp),
    )
}