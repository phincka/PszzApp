package com.example.pszzapp.presentation.queen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.components.modalDialog.ModalDialog
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.data.model.QueenModel
import com.example.pszzapp.data.util.DropdownMenuItemData
import com.example.pszzapp.presentation.apiaries.EmptyList
import com.example.pszzapp.presentation.apiary.ApiaryViewModel
import com.example.pszzapp.presentation.apiary.RemoveApiaryState
import com.example.pszzapp.presentation.components.HivesLazyColumn
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.dashboard.CircleTopBar
import com.example.pszzapp.presentation.destinations.ApiariesScreenDestination
import com.example.pszzapp.presentation.destinations.CreateApiaryScreenDestination
import com.example.pszzapp.presentation.destinations.CreateHiveStep1ScreenDestination
import com.example.pszzapp.presentation.destinations.DashboardScreenDestination
import com.example.pszzapp.presentation.destinations.HiveScreenDestination
import com.example.pszzapp.presentation.destinations.QrScannerScreenDestination
import com.example.pszzapp.presentation.main.SnackbarHandler
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun QueenScreen(
    id: String,
    destinationsNavigator: DestinationsNavigator,
    viewModel: QueenViewModel = koinViewModel(parameters = { parametersOf(id) }),
    navController: NavController,
    snackbarHandler: SnackbarHandler,
    message: String? = null,
) {
    val backStackEntry = navController.currentBackStackEntry
    val refresh = backStackEntry?.savedStateHandle?.get<Boolean>("refresh")

    val queenState = viewModel.queenState.collectAsState().value
    val removeQueenState = viewModel.removeQueenState.collectAsState().value

    var isModalActive by remember { mutableStateOf(false) }
    var isDropdownMenuVisible by remember { mutableStateOf(false) }

    if (removeQueenState is RemoveQueenState.Success) {
        destinationsNavigator.navToApiariesScreen(message = "Pomyślnie usunięto pasiekę.")
    }

    LaunchedEffect(removeQueenState, refresh) {
        launch {
            if (removeQueenState is RemoveQueenState.Error) snackbarHandler.showErrorSnackbar(
                message = removeQueenState.message
            )

            if (refresh == true) viewModel.getQueens(id)
        }
    }


    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        BackgroundShapes()

        Column {
            when (queenState) {
                is QueenState.Success -> {
                    QueenLayout(
                        isDropdownMenuVisible = isDropdownMenuVisible,
                        setDropdownMenuVisible = { isDropdownMenuVisible = it },
                        isModalActive = isModalActive,
                        setModal = { isModalActive = it },
                        queen = queenState.queen,
                        navToHive = destinationsNavigator::navToHiveScreen,
                        navToEditApiary = destinationsNavigator::navToEditApiary,
                        navToCreateHive = destinationsNavigator::navToCreateHive,
                        snackbarHandler = snackbarHandler,
                        message = message,
                    )
                }

                is QueenState.Error -> {
                    val errorMessage = queenState.message
                    TextError(errorMessage)
                }

                is QueenState.Loading -> LoadingDialog()
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueenLayout(
    isDropdownMenuVisible: Boolean,
    setDropdownMenuVisible: (Boolean) -> Unit,
    isModalActive: Boolean,
    setModal: (Boolean) -> Unit,
    queen: QueenModel,
    navToHive: (String) -> Unit,
    navToEditApiary: (ApiaryModel) -> Unit,
    navToCreateHive: (String) -> Unit,
    snackbarHandler: SnackbarHandler,
    message: String? = null,
) {
    LaunchedEffect(message) {
        launch {
            message?.let {
                snackbarHandler.showSuccessSnackbar(
                    message = it
                )
            }
        }
    }

//    val menuItems = listOf(
//        DropdownMenuItemData(
//            icon = Icons.Outlined.Edit,
//            text = "Dodaj ul",
//            onClick = { navToCreateHive(queen.id) }
//        ),
//        DropdownMenuItemData(
//            icon = Icons.Outlined.Edit,
//            text = "Edytuj pasiekę",
//            onClick = { navToEditApiary(queen) }
//        ),
////        DropdownMenuItemData(
////            icon = Icons.Outlined.PinDrop,
////            text = "Dodaj lokalizację",
////            onClick = { }
////        ),
////        DropdownMenuItemData(
////            icon = Icons.Outlined.WbSunny,
////            text = "Sprawdź pogodę",
////            onClick = { }
////        ),
////        DropdownMenuItemData(
////            icon = Icons.Outlined.AttachEmail,
////            text = "Wyślij raport z pasieki",
////            onClick = { }
////        ),
//        DropdownMenuItemData(
//            icon = Icons.Outlined.Clear,
//            text = stringResource(R.string.hive_nav_remove_hive),
//            onClick = { setModal(true) }
//        ),
//    )

//    CircleTopBar(
//        circleText = (if (hives.isNotEmpty()) apiary.hivesCount else apiary.name.first()
//            .uppercase()).toString(),
//        title = apiary.name,
//        subtitle = if (apiary.lastOverview != null) "Ostatni przegląd: ${apiary.lastOverview}" else "Brak przeglądów",
//        showSubtitle = hives.isNotEmpty(),
//        isDropdownMenuVisible = isDropdownMenuVisible,
//        onSettingsClick = { setDropdownMenuVisible(it) },
//        menuItems = menuItems,
//    )

//    if (queen.isNotEmpty()) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(AppTheme.colors.primary50)
            ) {
                Image(
                    painter = painterResource(R.drawable.camera_white),
                    contentDescription = "arrow_right",
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        }
//    } else {
//        EmptyList(
//            title = "Brak uli",
//            text = "Kliknij w przycisk i dodaj pierwszy ul.",
//            buttonTitle = "Dodaj ul",
//            navigate = { navToCreateHive(apiary.id) },
//        )
//    }

    ModalDialog(
        dialogTitle = "Usuń pasiekę",
        dialogText = "Czy na pewno chcesz usunąć pasiekę?",
        confirmButtonText = stringResource(R.string.remove_modal_remove),
        dismissButtonText = stringResource(R.string.remove_modal_cancel),
        icon = Icons.Filled.Warning,
        isModalActive = isModalActive,
        onDismissRequest = { setModal(false) },
        onConfirmation = {
//            removeApiary(apiary.id)
        },
    )
}

private fun DestinationsNavigator.navToCreateHive(apiaryId: String) =
    navigate(CreateHiveStep1ScreenDestination(apiaryId))

private fun DestinationsNavigator.navToEditApiary(apiaryModel: ApiaryModel) =
    navigate(CreateApiaryScreenDestination(apiaryModel))

private fun DestinationsNavigator.navToQrScannerScreen() = navigate(QrScannerScreenDestination)

fun DestinationsNavigator.navToApiariesScreen(message: String? = null) =
    navigate(ApiariesScreenDestination(message = message))

fun DestinationsNavigator.navToHiveScreen(hiveId: String, message: String? = null) =
    navigate(HiveScreenDestination(hiveId, message))

fun DestinationsNavigator.navToDashboard(message: String? = null) = navigate(DashboardScreenDestination(message = message))
