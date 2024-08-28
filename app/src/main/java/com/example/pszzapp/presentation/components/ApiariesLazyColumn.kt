package com.example.pszzapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.presentation.auth.base.VerticalSpacer
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun ApiariesLazyColumn(
    apiaries: List<ApiaryModel>,
    navigator : DestinationsNavigator
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            VerticalSpacer(height = 4.dp)
        }
        items(items = apiaries, key = { it.id }) { apiary ->
            ApiaryRow(apiary, navigator)
        }
        item {
            VerticalSpacer(height = 4.dp)
        }
    }
}