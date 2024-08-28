package com.example.pszzapp.presentation.hive

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
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.data.util.DropdownMenuItemData
import com.example.pszzapp.presentation.apiaries.EmptyList
import com.example.pszzapp.presentation.components.HivesLazyColumn
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.OverviewsLazyColumn
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.dashboard.ButtonTile
import com.example.pszzapp.presentation.dashboard.ButtonTiles
import com.example.pszzapp.presentation.dashboard.LastOverviews
import com.example.pszzapp.presentation.destinations.ApiariesScreenDestination
import com.example.pszzapp.presentation.destinations.CreateApiaryScreenDestination
import com.example.pszzapp.presentation.destinations.CreateOverviewStep1ScreenDestination
import com.example.pszzapp.presentation.destinations.DashboardScreenDestination
import com.example.pszzapp.presentation.destinations.QrScannerScreenDestination
import com.example.pszzapp.presentation.hive.create.CreateHiveConstants
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun HiveScreen(
    id: String = "M9I1XqC0P2xIqIFpLASb",
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    hiveViewModel: HiveViewModel = koinViewModel(parameters = { parametersOf(id) })
) {
    val hiveState = hiveViewModel.hiveState.collectAsState().value
    val overviewsState = hiveViewModel.overviewsState.collectAsState().value

    var isModalActive by remember { mutableStateOf(false) }



    when (hiveState) {
        is HiveState.Success -> {
            val menuItems = listOf(
                DropdownMenuItemData(
                    icon = Icons.Outlined.Edit,
                    text = "Edytuj ul",
                    onClick = { }
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
                    onClick = {
                        navigator.navigate(
                            CreateOverviewStep1ScreenDestination(
                                hiveId = hiveState.hive.id,
                                apiaryId = hiveState.hive.apiaryId
                            )
                        )
                    }
                ),
                DropdownMenuItemData(
                    icon = Icons.Outlined.Clear,
                    text = stringResource(R.string.hive_nav_remove_hive),
                    onClick = {
                        isModalActive = true
                    }
                ),
            )

            val buttonTilesNavigation = listOf(
                ButtonTile(
                    title = "Raport",
                    icon = R.drawable.ic_tile_button,
                    direction = ApiariesScreenDestination,
                ),
                ButtonTile(
                    title = "Dodaj przegląd",
                    icon = R.drawable.ic_tile_button,
                    direction = CreateOverviewStep1ScreenDestination(
                        hiveId = hiveState.hive.id,
                        apiaryId = hiveState.hive.apiaryId
                    ),
                ),
                ButtonTile(
                    title = "Leczenie",
                    icon = R.drawable.ic_tile_button,
                    direction = ApiariesScreenDestination,
                ),
                ButtonTile(
                    title = "Karmienie",
                    icon = R.drawable.ic_tile_button,
                    direction = CreateApiaryScreenDestination,
                ),
                ButtonTile(
                    title = "Zbiory",
                    icon = R.drawable.ic_tile_button,
                    direction = ApiariesScreenDestination,
                ),
                ButtonTile(
                    title = "Przypomnienia",
                    icon = R.drawable.ic_tile_button,
                    direction = CreateApiaryScreenDestination,
                ),
            )

            HiveLayout(
                navController = navController,
                resultNavigator = resultNavigator,
                menuItems = menuItems,
                isModalActive = isModalActive,
                setModal = { isModalActive = it },
                hive = hiveState.hive,
                overviewsState = overviewsState,
                navigator = navigator,
                buttonTilesNavigation = buttonTilesNavigation,
            )
        }
        is HiveState.Loading -> LoadingDialog()
        is HiveState.Error -> TextError(hiveState.message)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiveLayout(
    navController: NavController,
    resultNavigator: ResultBackNavigator<Boolean>,
    menuItems: List<DropdownMenuItemData>,
    isModalActive: Boolean,
    setModal: (Boolean) -> Unit,
    hive: HiveModel,
    overviewsState: OverviewsState,
    navigator: DestinationsNavigator,
    buttonTilesNavigation: List<ButtonTile>,
) {
    var isDropdownMenuVisible by remember { mutableStateOf(false) }

    var titlesState by remember { mutableIntStateOf(0) }
    val titles = listOf(
        "Informacje z pasieki",
        "Przeglądy"
    )

    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        BackgroundShapes()

        Column {
            TopBar(
                backNavigation = { resultNavigator.navigateBack() },
                title = hive.name,
                warningInfo = "Stan rojowy!",
                subtitle = stringResource(CreateHiveConstants.hiveType[hive.hiveType]),
                menuItems = menuItems,
                isModalActive = isModalActive,
                setModal = setModal,
                onSettingsClick = {isDropdownMenuVisible = true },
                onNotification = {},
            )

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
                    ButtonTiles(
                        navigator = navigator,
                        buttonTilesNavigation = buttonTilesNavigation,
                    )
                }

                1 -> {

                    LastOverviews(
                        sectionTitle = "Ostatnie przeglądy",
                        navigator = navigator,
                    )

//                    Text(text = hive.name)
//                    Spacer(modifier = Modifier.height(24.dp))
//
//
//                    when (overviewsState) {
//                        is OverviewsState.Success -> {
//                            Text(text = "Ostatnie przeglądy")
//                            Spacer(modifier = Modifier.height(16.dp))
//                            OverviewsLazyColumn(
//                                overviews = overviewsState.overviews,
//                                navigator = navigator
//                            )
//                        }
//
//                        is OverviewsState.Loading -> LoadingDialog()
//                        is OverviewsState.Error -> TextError(overviewsState.message)
//                    }
                }
            }



        }
    }
}