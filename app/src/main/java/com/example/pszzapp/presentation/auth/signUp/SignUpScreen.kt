package com.example.pszzapp.presentation.auth.signUp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.destinations.DashboardScreenDestination
import com.example.pszzapp.presentation.destinations.SignInScreenDestination
import com.example.pszzapp.presentation.main.SnackbarHandler
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun SignUpScreen(
    navigator: DestinationsNavigator,
    navController: NavController,
    viewModel: SignUpViewModel = koinViewModel(),
    snackbarHandler: SnackbarHandler,
) {
    val signUpState = viewModel.signUpState.collectAsState().value

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    if (signUpState is AuthState.Loading) LoadingDialog()
    if (signUpState is AuthState.Success) navigator.navigate(DashboardScreenDestination())

    LaunchedEffect(signUpState) {
        launch {
            if (signUpState is AuthState.Error) snackbarHandler.showErrorSnackbar(message = signUpState.message)
        }
    }

    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        BackgroundShapes()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            VerticalSpacer(40.dp)

            Text(
                text = "Utwórz konto,",
                style = Typography.h3,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.neutral90,
            )

            Text(
                text = "Już tylko jeden krok dzieli Cię od zakupowania!",
                style = Typography.h5,
                color = AppTheme.colors.neutral90,
            )

            VerticalSpacer(20.dp)

            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                InputText(
                    label = stringResource(R.string.signIn_form_name),
                    placeholder = stringResource(R.string.signIn_form_name),
                    value = name,
                    onValueChange = { name = it },
                )

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

                PasswordField(
                    label = stringResource(R.string.signIn_form_repeat_password),
                    value = repeatPassword,
                    setValue = { newValue ->
                        repeatPassword = newValue
                    },
                )
            }

            VerticalSpacer(20.dp)

            Button(
                text = stringResource(R.string.signIn_register_button),
                onClick = { viewModel.signUp(name, email, password, repeatPassword) },
            )

            VerticalSpacer(32.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.signUp_signIn_button),
                    textAlign = TextAlign.Start,
                    style = Typography.small,
                    color = AppTheme.colors.neutral90,
                    modifier = Modifier.clickable(
                        onClick = { navigator.navigate(SignInScreenDestination) }
                    )
                )
            }
        }
    }
}