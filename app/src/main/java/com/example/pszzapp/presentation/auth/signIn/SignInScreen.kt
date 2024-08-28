package com.example.pszzapp.presentation.auth.signIn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.util.AuthState
import com.example.pszzapp.presentation.apiary.create.InputText
import com.example.pszzapp.presentation.auth.base.Button
import com.example.pszzapp.presentation.auth.base.VerticalSpacer
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.PasswordField
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.destinations.DashboardScreenDestination
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun SignInScreen(
    navigator: DestinationsNavigator,
    navController: NavController,
    signInViewModel: SignInViewModel = koinViewModel()
) {
    val signInState = signInViewModel.signInState.collectAsState().value

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
        ) {
            VerticalSpacer(40.dp)

            Text(
                text = "Witaj,",
                style = Typography.h3,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.neutral90,
            )

            Text(
                text = "Miło znów Cię widzieć!",
                style = Typography.h5,
                color = AppTheme.colors.neutral90,
            )

            VerticalSpacer(20.dp)

            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                InputText(
                    label = stringResource(R.string.signIn_form_login),
                    placeholder = stringResource(R.string.signIn_form_login),
                    value = email,
                    onValueChange = { email = it },
                )

                PasswordField(
                    label = stringResource(R.string.signIn_form_password),
                    value = password,
                    setValue = { newValue ->
                        password = newValue
                    },
                )
            }

            VerticalSpacer(20.dp)

            Button(
                text = stringResource(R.string.signIn_login_button),
                onClick = { signInViewModel.signIn(email, password) },
            )

            VerticalSpacer(32.dp)

            if (signInState is AuthState.Error) TextError(signInState.error)


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.signIn_register_button),
                    textAlign = TextAlign.Start,
                    style = Typography.small,
                    color = AppTheme.colors.neutral90,
                )

                Text(
                    text = stringResource(R.string.signIn_reset_password_text),
                    textAlign = TextAlign.End,
                    style = Typography.small,
                    color = AppTheme.colors.neutral90,
                )
            }
        }

        if (signInState is AuthState.Success) navigator.navigate(DashboardScreenDestination)
        if (signInState is AuthState.Loading) LoadingDialog()
    }
}
