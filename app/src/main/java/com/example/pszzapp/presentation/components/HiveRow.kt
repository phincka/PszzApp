package com.example.pszzapp.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.presentation.destinations.ApiaryScreenDestination
import com.example.pszzapp.presentation.destinations.HiveScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun HiveRow(
    hive: HiveModel,
    navigator : DestinationsNavigator
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        onClick = {
            navigator.navigate(
                HiveScreenDestination(id = hive.id)
            )
        }
    ){
        ListItem(
            headlineContent = { Text(hive.name) },
            overlineContent = { Text("ID: ${hive.id}") },
            trailingContent = { Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
            ) },
        )
    }
}