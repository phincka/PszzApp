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
import com.example.pszzapp.presentation.destinations.ApiaryScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun ApiaryRow(
    apiary: ApiaryModel,
    navigator : DestinationsNavigator
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        onClick = {
            navigator.navigate(
                ApiaryScreenDestination(id = apiary.id)
            )
        }
    ){
        ListItem(
            headlineContent = { Text(apiary.name) },
            overlineContent = { Text("ID: ${apiary.id}") },
            trailingContent = { Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
            ) },
        )
    }
}