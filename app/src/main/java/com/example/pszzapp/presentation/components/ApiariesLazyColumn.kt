package com.example.pszzapp.presentation.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import com.example.pszzapp.data.model.ApiaryModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun ApiariesLazyColumn(
    apiaries: List<ApiaryModel>,
    navigator : DestinationsNavigator
) {
    Divider()
    LazyColumn() {
        items(items = apiaries, key = { it.id }) { apiary ->
            ApiaryRow(apiary, navigator)
            Divider()
        }
    }
}