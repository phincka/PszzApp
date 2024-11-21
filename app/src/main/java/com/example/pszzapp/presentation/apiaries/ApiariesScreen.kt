package com.example.pszzapp.presentation.apiaries

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.presentation.apiary.navToDashboard
import com.example.pszzapp.presentation.auth.base.Button
import com.example.pszzapp.presentation.auth.base.VerticalSpacer
import com.example.pszzapp.presentation.components.ApiariesLazyColumn
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.main.SnackbarHandler
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun ApiariesScreen(
    viewModel: ApiariesViewModel = koinViewModel(),
    navigator: DestinationsNavigator,
    navController: NavController,
    snackbarHandler: SnackbarHandler,
    message: String? = null,
) {
    val apiariesState = viewModel.apiariesState.collectAsState().value

    val backStackEntry = navController.currentBackStackEntry
    val refresh = backStackEntry?.savedStateHandle?.get<Boolean>("refresh")

    LaunchedEffect(refresh) {
        if (refresh == true) {
            viewModel.getApiaries()
        }
    }

    LaunchedEffect(message) {
        launch {
            message?.let {
                snackbarHandler.showSuccessSnackbar(
                    message = it
                )
            }
        }
    }

    ApiariesLayout(
        navController = navController,
        navigator = navigator,
        apiariesState = apiariesState,
    )
}


@Composable
fun ApiariesLayout(
    navController: NavController,
    navigator: DestinationsNavigator,
    apiariesState: ApiariesState,
) {
    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        BackgroundShapes()

        Column {
            TopBar(
                backNavigation = { navigator.navigateUp() },
                title = "Twoje pasieki",
            )

            if (apiariesState is ApiariesState.Loading) LoadingDialog()

            if (apiariesState is ApiariesState.Success) {
                if (apiariesState.apiaries.isNotEmpty()) {
                    ApiariesLazyColumn(apiariesState.apiaries, navigator)
                } else {
                    EmptyList(
                        title = "Nie masz jeszcze pasieki",
                        text = "Kliknij w przycisk i dodaj swoją pierwszą pasiekę",
                        buttonTitle = "Dodaj pasiekę",
                        navigate = { navigator.navToDashboard() },
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyList(
    title: String,
    text: String,
    buttonTitle: String,
    showIcon: Boolean = true,
    navigate: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(vertical = 64.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showIcon) {
            Image(
                painter = painterResource(R.drawable.empty_list),
                contentDescription = "arrow_right",
                modifier = Modifier
                    .size(164.dp)
            )
        }

        VerticalSpacer(48.dp)

        Text(
            text = title,
            style = Typography.h5,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colors.neutral90,
        )
        VerticalSpacer(8.dp)

        Text(
            text = text,
            style = Typography.label,
            color = AppTheme.colors.neutral60,
        )

        VerticalSpacer(32.dp)


        Button(
            text = buttonTitle,
            onClick = navigate,
        )
    }
}