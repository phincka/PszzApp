package com.example.pszzapp.presentation.auth.signIn

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pszzapp.presentation.components.InputField
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.PasswordField
import com.example.pszzapp.presentation.components.TextButton
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TextOutlinedButton
import com.example.pszzapp.R
import com.example.pszzapp.data.util.AuthState
import com.example.pszzapp.presentation.NavGraphs
import com.example.pszzapp.presentation.destinations.MainScreenDestination
import com.example.pszzapp.ui.theme.PszzAppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun BaseAuthScreen(
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

        TextOutlinedButton(
            text = stringResource(R.string.signIn_register_button),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
//                navigator.navigate(SignUpScreenDestination)
            },
        )

        TextButton(
            text = stringResource(R.string.signIn_login_button),
            modifier = Modifier.fillMaxWidth(),
            onClick = {  },
        )

//        Row(
//            modifier = Modifier.padding(top = 16.dp),
//            horizontalArrangement = Arrangement.spacedBy(4.dp)
//        ) {
//            Text(
//                text = stringResource(R.string.signIn_reset_password_text),
//                style = Typography.bodyMedium
//            )
//            Text(
//                text = stringResource(R.string.signIn_reset_password_button),
//                style = Typography.bodyMedium,
//                textDecoration = TextDecoration.Underline,
//                modifier = Modifier.clickable {
//                    // TODO: DODAĆ WIDOK
//                    Log.d("APP_LOG", "TODO RESETUJ HASŁO")
//                }
//            )
//        }

//        when(signInState.value) {
//            is AuthState.Loading -> LoadingDialog(stringResource(R.string.signIn_title))
//
//            is AuthState.Success -> {
//                val success = (signInState.value as AuthState.Success).success
//                if (success) navigator.navigate(MainScreenDestination)
//            }
//
//            is AuthState.Error ->  {
//                val errorMessage = (signInState.value as AuthState.Error).error
//                TextError(errorMessage)
//            }
//        }
    }
}

@Preview
@Composable
fun Preview() {
    PszzAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BaseAuthScreen()
        }
    }
}