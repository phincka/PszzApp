package com.example.pszzapp.presentation.account

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.util.AccountUserState
import com.example.pszzapp.presentation.components.FilledButton
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.dashboard.AccountViewModel
import com.example.pszzapp.presentation.destinations.BaseAuthScreenDestination
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun AccountScreen(
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    viewModel: AccountViewModel = koinViewModel()
) {
    val accountUserState = viewModel.accountUserState.collectAsState().value

    AccountLayout(
        navigator = navigator,
        resultNavigator = resultNavigator,
        navController = navController,
        signOut = { viewModel.signOut() },
        accountUserState = accountUserState
    )
}

@Composable
fun AccountLayout(
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    signOut: () -> Unit,
    accountUserState: AccountUserState,
) {
    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        Column {
            TopBar(
                backNavigation = { resultNavigator.navigateBack() },
                title = "Konto",
                content = { }
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                when (accountUserState) {
                    is AccountUserState.Loading -> LoadingDialog(stringResource(R.string.home_loading))

                    is AccountUserState.SignedInState -> {
                        FilledButton(
                            text = "Wyloguj się",
                            onClick = { signOut() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    is AccountUserState.GuestState -> navigator.navigate(BaseAuthScreenDestination)

                    is AccountUserState.Error -> TextError(accountUserState.message)

                    is AccountUserState.None -> Unit
                }
            }
        }
    }
}