package com.example.pszzapp.presentation.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.model.UserModel
import com.example.pszzapp.data.util.AccountUserState
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.destinations.BaseAuthScreenDestination
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@RootNavGraph(start = true)
@Destination
@Composable
fun DashboardScreen(
    navigator: DestinationsNavigator,
    viewModel: DashboardViewModel = koinViewModel(),
    navController: NavController
) {
    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {

        when (val accountUserState = viewModel.accountUserState.collectAsState().value) {
            is AccountUserState.Loading -> LoadingDialog(stringResource(R.string.home_loading))

            is AccountUserState.SignedInState -> DashboardLayout()

            is AccountUserState.GuestState -> navigator.navigate(BaseAuthScreenDestination)

            is AccountUserState.Error -> TextError(accountUserState.message)

            is AccountUserState.None -> Unit
        }
    }


}


@Composable
fun DashboardLayout() {
    Column {
        TopBar(
            title = "Dashboard",
            content = { }
        )

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

        }
    }
}


//enum class BottomBarDestination(
//    val direction: DirectionDestinationSpec,
//    val icon: ImageVector,
//    val label: String
//) {
//    DashboardScreen(DashboardScreenDestination, Icons.Default.Home, "Dashboard"),
//    ApiariesScreen(ApiariesScreenDestination, Icons.Default.Email, "Apiaries"),
//}

//@Composable
//fun BottomBar(
//    navController: NavController
//) {
//    val currentDestination: TypedDestination<*> = navController.appCurrentDestinationAsState().value
//        ?: NavGraphs.root.startAppDestination
//
//    NavigationBar {
//        BottomBarDestination.entries.forEach { destination ->
//            NavigationBarItem(
//                selected = currentDestination == destination.direction,
//                onClick = {
//                    navController.navigate(destination.direction, fun NavOptionsBuilder.() {
//                        launchSingleTop = true
//                    })
//                },
//                icon = { Icon(destination.icon, contentDescription = destination.label)},
//                label = { Text(destination.label) },
//            )
//        }
//    }
//}