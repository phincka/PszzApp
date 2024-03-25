package com.example.pszzapp.presentation.hive

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AttachEmail
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.util.DropdownMenuItemData
import com.example.pszzapp.presentation.components.Dropdown
import com.example.pszzapp.presentation.components.Modal
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun HiveScreen(
    id: String,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
//    apiaryViewModel: ApiaryViewModel = koinViewModel(parameters = { parametersOf(id) })
) {
//    val apiaryState = apiaryViewModel.apiaryState.collectAsState().value

    var isDropdownMenuVisible by remember { mutableStateOf(false) }
    var isModalActive by remember { mutableStateOf(false) }


    val menuItems = listOf(
        DropdownMenuItemData(
            icon = Icons.Outlined.Edit,
            text = "Edytuj ul",
            onClick = {
//                navigator.navigate(
//                    AddHiveLocationDestination(id, lat, lng)
//                )
            }
        ),
        DropdownMenuItemData(
            icon = Icons.Outlined.PinDrop,
            text = "Ogólne informacje",
            onClick = { }
        ),
        DropdownMenuItemData(
            icon = Icons.Outlined.WbSunny,
            text = "Informacje o matce",
            onClick = { }
        ),
        DropdownMenuItemData(
            icon = Icons.Outlined.AttachEmail,
            text = "Dodaj przegląd",
            onClick = { }
        ),
        DropdownMenuItemData(
            icon = Icons.Outlined.Clear,
            text = stringResource(R.string.hive_nav_remove_hive),
            onClick = {
                isModalActive = true
            }
        ),
    )

    HiveLayout(
        navController = navController,
        navigator = navigator,
        resultNavigator = resultNavigator,
        isDropdownMenuVisible = isDropdownMenuVisible,
        setDropdownMenuVisible = { isDropdownMenuVisible = it },
        menuItems = menuItems,
        isModalActive = isModalActive,
        setModal = { isModalActive = it },
//        hiveState = hiveState
    )
}


@Composable
fun HiveLayout(
    navController: NavController,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>,
    isDropdownMenuVisible: Boolean,
    setDropdownMenuVisible: (Boolean) -> Unit,
    menuItems: List<DropdownMenuItemData>,
    isModalActive: Boolean,
    setModal: (Boolean) -> Unit,
//    hiveState: HivesState,
) {
    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        Column {
            TopBar(
                backNavigation = { resultNavigator.navigateBack() },
                title = "Ul",
                content = {
                    IconButton(
                        onClick = { setDropdownMenuVisible(true) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = null
                        )
                    }
                }
            )

            Dropdown(
                isDropdownMenuVisible = isDropdownMenuVisible,
                setDropdownMenuVisible = { setDropdownMenuVisible(it)},
                menuItems = menuItems
            )

            Modal(
                dialogTitle = stringResource(R.string.hive_remove_modal_title),
                dialogText = stringResource(R.string.hive_remove_modal_text),
                icon = Icons.Filled.Warning,
                isModalActive = isModalActive,
                onDismissRequest = { setModal(false) },
                onConfirmation = { },
            )

//        when (hiveState) {
//            is HivesState.Success -> {
//                val hives = apiaryState.hives
//                ApiaryLayout(
//                    hives,
//                    navigator
//                )
//                    HivesLazyColumn(hives, navigator)

//            }
//
//            is HivesState.Error -> {
//                val errorMessage = apiaryState.message
//                TextError(errorMessage)
//            }
//
//            is HivesState.Loading -> LoadingDialog(stringResource(R.string.home_loading))
//        }


        }
    }
}