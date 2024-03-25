package com.example.pszzapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pszzapp.ui.theme.Typography

@Composable
fun LoadingDialog(
    text: String
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.LightGray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = Color.Yellow,
                strokeWidth = 6.dp
            )

            Text(
                text,
                modifier = Modifier.padding(top = 32.dp),
                color = Color.White,
                style = Typography.titleMedium
            )
        }
    }
}