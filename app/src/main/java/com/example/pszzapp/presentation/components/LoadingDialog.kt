package com.example.pszzapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pszzapp.ui.theme.AppTheme

@Composable
fun LoadingDialog() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.white.copy(alpha = 0.75f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = AppTheme.colors.rating,
                strokeWidth = 6.dp
            )
        }
    }
}