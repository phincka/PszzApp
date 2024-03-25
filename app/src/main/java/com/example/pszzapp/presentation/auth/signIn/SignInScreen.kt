package com.example.pszzapp.presentation.auth.signIn

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.presentation.components.InputField
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.PasswordField
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.R
import com.example.pszzapp.data.util.AuthState
import com.example.pszzapp.presentation.components.FilledButton
import com.example.pszzapp.presentation.components.OutlinedButton
import com.example.pszzapp.presentation.destinations.DashboardScreenDestination
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun SignInScreen(
    navigator: DestinationsNavigator,
    navController: NavController,
    signInViewModel: SignInViewModel = koinViewModel()
) {
    val signInState = signInViewModel.signInState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                text = stringResource(R.string.signIn_title),
                style = Typography.headlineLarge,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
    
            Spacer(modifier = Modifier.height(32.dp))
    
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InputField(
                    label = stringResource(R.string.signIn_form_login),
                    value = email,
                    setValue = { newValue ->
                        email = newValue
                    },
                    icon = Icons.Default.Email,
                )
    
                PasswordField(
                    label = stringResource(R.string.signIn_form_password),
                    value = password,
                    setValue = { newValue ->
                        password = newValue
                    },
                    icon = Icons.Default.Lock
                )
            }
    
            Spacer(modifier = Modifier.height(16.dp))
    
            FilledButton(
                text = stringResource(R.string.signIn_login_button),
                modifier = Modifier.fillMaxWidth(),
                onClick = { signInViewModel.signIn(email, password) },
            )
    
            OutlinedButton(
                text = stringResource(R.string.signIn_register_button),
                modifier = Modifier.fillMaxWidth(),
                onClick = {},
            )
    
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(stringResource(R.string.signIn_reset_password_text))
                Text(
                    text = stringResource(R.string.signIn_reset_password_button),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        // TODO: DODAĆ WIDOK
                        Log.d("APP_LOG", "TODO RESETUJ HASŁO")
                    }
                )
            }
    
            when(signInState.value) {
                is AuthState.Loading -> LoadingDialog(stringResource(R.string.signIn_title))
    
                is AuthState.Success -> {
                    val success = (signInState.value as AuthState.Success).success
                    if (success) navigator.navigate(DashboardScreenDestination)
                }
    
                is AuthState.Error ->  {
                    val errorMessage = (signInState.value as AuthState.Error).error
                    TextError(errorMessage)
                }
            }
        }
    }
}