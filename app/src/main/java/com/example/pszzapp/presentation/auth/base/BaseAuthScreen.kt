package com.example.pszzapp.presentation.auth.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.presentation.components.FilledButton
import com.example.pszzapp.presentation.components.OutlinedButton
import com.example.pszzapp.presentation.destinations.SignInScreenDestination
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun BaseAuthScreen(
    navigator: DestinationsNavigator,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Hej! Zaloguj się aby korzystać z aplikacji.",
                style = Typography.titleLarge,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                text = stringResource(R.string.signIn_register_button),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
//                navigator.navigate(SignUpScreenDestination)
                },
            )

            FilledButton(
                text = stringResource(R.string.signIn_login_button),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navigator.navigate(SignInScreenDestination)
                },
            )
        }
    }
}

//@Preview
//@Composable
//fun Preview() {
//    PszzAppTheme {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
//        ) {
//            BaseAuthScreen(
//                navigator = DestinationsNavigator
//            )
//        }
//    }
//}