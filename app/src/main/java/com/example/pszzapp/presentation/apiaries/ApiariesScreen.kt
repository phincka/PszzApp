package com.example.pszzapp.presentation.apiaries

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarViewWeek
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.util.DropdownMenuItemData
import com.example.pszzapp.presentation.apiary.navToDashboard
import com.example.pszzapp.presentation.auth.base.Button
import com.example.pszzapp.presentation.auth.base.VerticalSpacer
import com.example.pszzapp.presentation.components.ApiariesLazyColumn
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.destinations.CreateApiaryScreenDestination
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.Route
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val menuItems = listOf(
        DropdownMenuItemData(
            icon = Icons.Outlined.CalendarViewWeek,
            text = "Skanuj ul",
            onClick = { showBottomSheet = true }
        ),
        DropdownMenuItemData(
            icon = Icons.Outlined.PinDrop,
            text = "Dodaj pasiekę",
            onClick = { navigator.navigate(CreateApiaryScreenDestination()) }
        ),
    )

    ApiariesLayout(
        navController = navController,
        navigator = navigator,
        resultNavigator = resultNavigator,
        apiariesState = apiariesState,
        menuItems = menuItems,
        sheetState = sheetState,
        showBottomSheet = showBottomSheet,
        setShowBottomSheet = { showBottomSheet = it }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiariesLayout(
    navController: NavController,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>,
    apiariesState: ApiariesState,
    menuItems: List<DropdownMenuItemData> = emptyList(),
    sheetState: SheetState,
    showBottomSheet: Boolean,
    setShowBottomSheet: (Boolean) -> Unit,
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