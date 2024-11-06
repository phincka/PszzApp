package com.example.pszzapp.presentation.apiary

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.pszzapp.data.util.DropdownMenuItemData
import com.example.pszzapp.presentation.apiaries.EmptyList
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
fun ApiaryScreen(
    id: String,
    destinationsNavigator: DestinationsNavigator,
    viewModel: ApiaryViewModel = koinViewModel(parameters = { parametersOf(id) }),
    navController: NavController,
    snackbarHandler: SnackbarHandler,
) {
    val apiaryState = viewModel.apiaryState.collectAsState().value
    val removeApiaryState = viewModel.removeApiaryState.collectAsState().value

    var isModalActive by remember { mutableStateOf(false) }
    var isDropdownMenuVisible by remember { mutableStateOf(false) }

    if (removeApiaryState is RemoveApiaryState.Success) {
        destinationsNavigator.navToApiariesScreen()
    }

    LaunchedEffect(removeApiaryState) {
        launch {
            if (removeApiaryState is RemoveApiaryState.Error) snackbarHandler.showErrorSnackbar(
                message = removeApiaryState.message
            )
        }
    }

    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        BackgroundShapes()

        Column {
            when (apiaryState) {
                is ApiaryState.Success -> {
                    ApiaryLayout(
                        isDropdownMenuVisible = isDropdownMenuVisible,
                        setDropdownMenuVisible = { isDropdownMenuVisible = it },
                        isModalActive = isModalActive,
                        setModal = { isModalActive = it },
                        apiary = apiaryState.apiary,
                        hives = apiaryState.hives,
                        removeApiary = viewModel::removeApiary,
                        onSearchTextChange = viewModel::onSearchTextChange,
                        searchText = viewModel.searchText.collectAsState().value,
                        persons = viewModel.persons.collectAsState().value,
                        isSearching = viewModel.isSearching.collectAsState().value,
                        navToQrScannerScreen = destinationsNavigator::navToQrScannerScreen,
                        navToHive = destinationsNavigator::navToHiveScreen,
                        navToDashboard = destinationsNavigator::navToDashboard,
                        navToEditApiary = destinationsNavigator::navToEditApiary,
                        navToCreateHive = destinationsNavigator::navToCreateHive,
                    )
                }

                is ApiaryState.Error -> {
                    val errorMessage = apiaryState.message
                    TextError(errorMessage)
                }

                is ApiaryState.Loading -> LoadingDialog()
            }
        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiaryLayout(
    isDropdownMenuVisible: Boolean,
    setDropdownMenuVisible: (Boolean) -> Unit,
    isModalActive: Boolean,
    setModal: (Boolean) -> Unit,
    apiary: ApiaryModel,
    hives: List<HiveModel>,
    removeApiary: (String) -> Unit,
    onSearchTextChange: (String) -> Unit,
    searchText: String,
    persons: List<HiveModel>,
    isSearching: Boolean,
    navToQrScannerScreen: () -> Unit,
    navToHive: (String) -> Unit,
    navToDashboard: () -> Unit,
    navToEditApiary: (ApiaryModel) -> Unit,
    navToCreateHive: (String) -> Unit,
) {
    val menuItems = listOf(
        DropdownMenuItemData(
            icon = Icons.Outlined.Edit,
            text = "Dodaj ul",
            onClick = { navToCreateHive(apiary.id) }
        ),
        DropdownMenuItemData(
            icon = Icons.Outlined.Edit,
            text = "Edytuj pasiekę",
            onClick = { navToEditApiary(apiary) }
        ),
//        DropdownMenuItemData(
//            icon = Icons.Outlined.PinDrop,
//            text = "Dodaj lokalizację",
//            onClick = { }
//        ),
//        DropdownMenuItemData(
//            icon = Icons.Outlined.WbSunny,
//            text = "Sprawdź pogodę",
//            onClick = { }
//        ),
//        DropdownMenuItemData(
//            icon = Icons.Outlined.AttachEmail,
//            text = "Wyślij raport z pasieki",
//            onClick = { }
//        ),
        DropdownMenuItemData(
            icon = Icons.Outlined.Clear,
            text = stringResource(R.string.hive_nav_remove_hive),
            onClick = { setModal(true) }
        ),
    )

    CircleTopBar(
        circleText = (if (hives.isNotEmpty()) apiary.hivesCount else apiary.name.first()
            .uppercase()).toString(),
        title = apiary.name,
        subtitle = if (apiary.lastOverview != null) "Ostatni przegląd: ${apiary.lastOverview}" else "Brak przeglądów",
        showSubtitle = hives.isNotEmpty(),
        isDropdownMenuVisible = isDropdownMenuVisible,
        onSettingsClick = { setDropdownMenuVisible(it) },
        menuItems = menuItems,
    )

    if (hives.isNotEmpty()) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .height(60.dp)
                    .background(
                        color = AppTheme.colors.white,
                        shape = RoundedCornerShape(10.dp),
                    ),
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = {
                    Text(
                        text = "Nazwa ula",
                        style = Typography.label,
                    )
                },
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = AppTheme.colors.white,
                    unfocusedBorderColor = AppTheme.colors.white,
                    focusedPlaceholderColor = AppTheme.colors.neutral30,
                    unfocusedPlaceholderColor = AppTheme.colors.neutral30,
                ),
                textStyle = Typography.label.copy(
                    fontWeight = FontWeight.Medium,
                    color = AppTheme.colors.neutral90,
                ),
                maxLines = 1,
                keyboardActions = KeyboardActions(
                    onDone = {}
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Image(
                        painter = painterResource(R.drawable.search_black),
                        contentDescription = "arrow_right",
                        modifier = Modifier
                            .size(16.dp)
                    )
                },
                trailingIcon = {
                    Image(
                        painter = painterResource(R.drawable.ramove),
                        contentDescription = "arrow_right",
                        modifier = Modifier
                            .size(12.dp)
                            .clickable(onClick = {
                                onSearchTextChange("")
                            })
                    )
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(AppTheme.colors.primary50)
                    .clickable(
                        onClick = navToQrScannerScreen,
                    )
            ) {
                Image(
                    painter = painterResource(R.drawable.camera_white),
                    contentDescription = "arrow_right",
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        }

        if (isSearching) {
            LoadingDialog()
        } else {
            HivesLazyColumn(persons, navToHive)
        }
    } else {
        EmptyList(
            title = "Widok w trakcie budowy",
            text = "Kliknij w przycisk i wróć do ekranu głównego.",
            buttonTitle = "Dodaj pasiekę",
            navigate = navToDashboard,
        )
    }

    ModalDialog(
        dialogTitle = "Usuń pasiekę",
        dialogText = "Czy na pewno chcesz usunąć pasiekę?",
        confirmButtonText = stringResource(R.string.remove_modal_remove),
        dismissButtonText = stringResource(R.string.remove_modal_cancel),
        icon = Icons.Filled.Warning,
        isModalActive = isModalActive,
        onDismissRequest = { setModal(false) },
        onConfirmation = { removeApiary(apiary.id) },
    )
}

private fun DestinationsNavigator.navToCreateHive(apiaryId: String) =
    navigate(CreateHiveStep1ScreenDestination(apiaryId))

private fun DestinationsNavigator.navToEditApiary(apiaryModel: ApiaryModel) =
    navigate(CreateApiaryScreenDestination(apiaryModel))

private fun DestinationsNavigator.navToQrScannerScreen() = navigate(QrScannerScreenDestination)
fun DestinationsNavigator.navToApiariesScreen() = navigate(ApiariesScreenDestination)
private fun DestinationsNavigator.navToHiveScreen(hiveId: String) =
    navigate(HiveScreenDestination(hiveId))

fun DestinationsNavigator.navToDashboard() = navigate(DashboardScreenDestination)
