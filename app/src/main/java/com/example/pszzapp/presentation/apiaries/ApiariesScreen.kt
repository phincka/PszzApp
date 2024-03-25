package com.example.pszzapp.presentation.apiaries

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.presentation.components.ApiariesLazyColumn
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun ApiariesScreen(
    apiariesViewModel: ApiariesViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<Boolean>,
    navigator: DestinationsNavigator,
    navController: NavController
) {
    val apiariesState = apiariesViewModel.apiariesState.collectAsState().value

    ApiariesLayout(
        navController = navController,
        navigator = navigator,
        resultNavigator = resultNavigator,
        apiariesState = apiariesState
    )
}


@Composable
fun ApiariesLayout(
    navController: NavController,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>,
    apiariesState: ApiariesState,
) {
    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        Column {
            TopBar(
                backNavigation = { resultNavigator.navigateBack() },
                title = "Pasieki",
                content = {}
            )

            when (apiariesState) {
                is ApiariesState.Loading -> LoadingDialog(stringResource(R.string.home_loading))
                is ApiariesState.Success -> ApiariesLazyColumn(apiariesState.apiaries, navigator)
                is ApiariesState.Error -> TextError(apiariesState.message)
            }
        }

    }
}
