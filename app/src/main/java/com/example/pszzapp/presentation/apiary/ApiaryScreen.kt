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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachEmail
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.util.DropdownMenuItemData
import com.example.pszzapp.presentation.apiaries.EmptyList
import com.example.pszzapp.presentation.components.HivesLazyColumn
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.dashboard.CircleTopBar
import com.example.pszzapp.presentation.destinations.CreateHiveStep1ScreenDestination
import com.example.pszzapp.presentation.destinations.DashboardScreenDestination
import com.example.pszzapp.presentation.destinations.QrScannerScreenDestination
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
//@RootNavGraph(start = true)
@Composable
fun ApiaryScreen(
//    id: String = "7aEXBqahiZ1CTub01sYu",
    id: String,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>,
    apiaryViewModel: ApiaryViewModel = koinViewModel(parameters = { parametersOf(id) }),
    navController: NavController
) {
    val apiaryState = apiaryViewModel.apiaryState.collectAsState().value

    var isModalActive by remember { mutableStateOf(false) }

    val menuItems = listOf(
        DropdownMenuItemData(
            icon = Icons.Outlined.Edit,
            text = "Dodaj ul",
            onClick = { navigator.navigate(CreateHiveStep1ScreenDestination(apiaryId = id)) }
        ),
        DropdownMenuItemData(
            icon = Icons.Outlined.Edit,
            text = "Edytuj pasiekę",
            onClick = { }
        ),
        DropdownMenuItemData(
            icon = Icons.Outlined.PinDrop,
            text = "Dodaj lokalizację",
            onClick = { }
        ),
        DropdownMenuItemData(
            icon = Icons.Outlined.WbSunny,
            text = "Sprawdź pogodę",
            onClick = { }
        ),
        DropdownMenuItemData(
            icon = Icons.Outlined.AttachEmail,
            text = "Wyślij raport z pasieki",
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

    ApiaryLayout(
        navController = navController,
        navigator = navigator,
        resultNavigator = resultNavigator,
        menuItems = menuItems,
        isModalActive = isModalActive,
        setModal = { isModalActive = it },
        apiaryState = apiaryState,
        viewModel = apiaryViewModel,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiaryLayout(
    navController: NavController,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>,
    menuItems: List<DropdownMenuItemData>,
    isModalActive: Boolean,
    setModal: (Boolean) -> Unit,
    apiaryState: ApiaryState,
    viewModel: ApiaryViewModel
) {
    var isDropdownMenuVisible by remember { mutableStateOf(false) }

    var titlesState by remember { mutableIntStateOf(0) }
    val titles = listOf(
        "Ule",
        "Zbiory"
    )

    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        BackgroundShapes()

        Column {
            when (apiaryState) {
                is ApiaryState.Success -> {
                    CircleTopBar(
                        circleText = (if (apiaryState.hives.isNotEmpty()) apiaryState.apiary.hivesCount else apiaryState.apiary.name.first()
                            .uppercase()).toString(),
                        title = apiaryState.apiary.name,
                        subtitle = if (apiaryState.apiary.lastOverview != null) "Ostatni przegląd: ${apiaryState.apiary.lastOverview}" else "Brak przeglądów",
                        hideSubtitle = apiaryState.hives.isNotEmpty(),
                        isDropdownMenuVisible = isDropdownMenuVisible,
                        onSettingsClick = { isDropdownMenuVisible = it },
                        menuItems = menuItems,
                    )

                    if (apiaryState.hives.isNotEmpty()) {
                        PrimaryTabRow(
                            selectedTabIndex = titlesState,
                            indicator = {},
                            divider = {},
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            containerColor = Color.Transparent,
                        ) {
                            titles.forEachIndexed { index, title ->
                                Tab(
                                    selected = titlesState == index,
                                    onClick = { titlesState = index },
                                    text = {
                                        Text(
                                            text = title,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            style = Typography.small,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (titlesState == index) AppTheme.colors.white else AppTheme.colors.primary30,
                                        )
                                    },
                                    modifier = if (titlesState == index) {
                                        Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(AppTheme.colors.primary50)
                                            .height(34.dp)
                                    } else {
                                        Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.Transparent)
                                            .height(34.dp)
                                    }
                                )
                            }
                        }

                        when (titlesState) {
                            0 -> {
                                val searchText by viewModel.searchText.collectAsState()
                                val persons by viewModel.persons.collectAsState()
                                val isSearching by viewModel.isSearching.collectAsState()

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
                                        modifier = Modifier.background(AppTheme.colors.white),
                                        value = searchText,
                                        onValueChange = viewModel::onSearchTextChange,
                                        placeholder = {
                                            Text(
                                                text = stringResource(R.string.apiary_form_name_label),
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
                                                    .size(12.dp)
                                            )
                                        },
                                        trailingIcon = {
                                            Image(
                                                painter = painterResource(R.drawable.ramove),
                                                contentDescription = "arrow_right",
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clickable(onClick = {
                                                        viewModel.onSearchTextChange("")
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
                                                onClick = {
                                                    navigator.navigate(
                                                        QrScannerScreenDestination()
                                                    )
                                                },
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
                                    HivesLazyColumn(persons, navigator)
                                }
                            }

                            1 -> {
                                EmptyList(
                                    title = "Widok w trakcie budowy",
                                    text = "Kliknij w przycisk i wróć do ekranu głównego.",
                                    buttonTitle = "Dodaj pasiekę",
                                    navigator = navigator,
                                    direction = DashboardScreenDestination,
                                )
                            }
                        }
                    } else {
                        EmptyList(
                            title = "Widok w trakcie budowy",
                            text = "Kliknij w przycisk i wróć do ekranu głównego.",
                            buttonTitle = "Dodaj pasiekę",
                            navigator = navigator,
                            direction = DashboardScreenDestination,
                        )
                    }
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