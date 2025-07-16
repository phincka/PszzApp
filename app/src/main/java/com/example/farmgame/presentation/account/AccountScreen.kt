package com.example.FarmGame.presentation.account

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.FarmGame.data.model.UserModel
import com.example.FarmGame.data.util.AccountUserState
import com.example.FarmGame.presentation.auth.base.VerticalSpacer
import com.example.FarmGame.presentation.components.FilledButton
import com.example.FarmGame.presentation.components.LoadingDialog
import com.example.FarmGame.presentation.components.TextError
import com.example.FarmGame.presentation.components.TopBar
import com.example.FarmGame.presentation.dashboard.AccountViewModel
import com.example.farmgame.presentation.dashboard.BackgroundShapes
import com.example.FarmGame.presentation.destinations.BaseAuthScreenDestination
import com.example.FarmGame.presentation.main.bottomBarPadding
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

    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        BackgroundShapes()


        when (accountUserState) {
            is AccountUserState.Loading -> LoadingDialog()

            is AccountUserState.SignedInState -> {
                AccountLayout(
                    resultNavigator = resultNavigator,
                    signOut = { viewModel.signOut() },
                    user = accountUserState.user
                )
            }

            is AccountUserState.GuestState -> navigator.navigate(BaseAuthScreenDestination)

            is AccountUserState.Error -> TextError(accountUserState.message)

            is AccountUserState.None -> Unit
        }
    }
}

@Composable
fun AccountLayout(
    resultNavigator: ResultBackNavigator<Boolean>,
    signOut: () -> Unit,
    user: UserModel,
) {
    Column {
        TopBar(
            backNavigation = { resultNavigator.navigateBack() },
            title = "Konto",
        )

        Column(
            modifier = Modifier.padding(12.dp)
        ) {
//            if (user.isBetaTester) {
//                Text(
//                    text = "Beta tester",
//                    style = Typography.p,
//                    fontWeight = FontWeight.SemiBold,
//                    color = AppTheme.colors.neutral90,
//                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
//                )
//            }
//            Text(
//                text = "Rodzaj konta: ${user.role}",
//                style = Typography.p,
//                fontWeight = FontWeight.SemiBold,
//                color = AppTheme.colors.neutral90,
//                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
//            )
//            Text(
//                text = "Typ konta: ${if (user.isPremium) "PREMIUM" else "STANDARD"}",
//                style = Typography.p,
//                fontWeight = FontWeight.SemiBold,
//                color = AppTheme.colors.neutral90,
//                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
//            )

            VerticalSpacer(64.dp)

            FilledButton(
                text = "Wyloguj się",
                onClick = { signOut() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}