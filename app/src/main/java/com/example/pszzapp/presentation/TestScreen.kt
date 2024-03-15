package com.example.pszzapp.presentation

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.pszzapp.presentation.destinations.MainScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun TestScreen(
    navigator: DestinationsNavigator,
) {


    Button(onClick = { navigator.navigate(MainScreenDestination) }) {
        Text(text = "MAIN SCREEN")
    }
}
