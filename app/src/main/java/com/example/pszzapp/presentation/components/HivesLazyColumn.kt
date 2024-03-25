package com.example.pszzapp.presentation.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun HivesLazyColumn(
    hives: List<HiveModel>,
    navigator : DestinationsNavigator
) {
    HorizontalDivider()
    LazyColumn() {
        items(items = hives, key = { it.id }) { hive ->
            HiveRow(hive, navigator)
            HorizontalDivider()
        }
    }
}