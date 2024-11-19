package com.example.pszzapp.presentation.hive

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AttachEmail
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.components.modalDialog.ModalDialog
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.data.util.DropdownMenuItemData
import com.example.pszzapp.presentation.apiary.navToApiariesScreen
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.OverviewsLazyColumn
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.dashboard.ButtonTile
import com.example.pszzapp.presentation.dashboard.ButtonTiles
import com.example.pszzapp.presentation.dashboard.navToOverview
import com.example.pszzapp.presentation.destinations.CreateApiaryScreenDestination
import com.example.pszzapp.presentation.destinations.CreateHiveStep1ScreenDestination
import com.example.pszzapp.presentation.destinations.CreateOverviewStep1ScreenDestination
import com.example.pszzapp.presentation.destinations.FeedingScreenDestination
import com.example.pszzapp.presentation.destinations.OverviewScreenDestination
import com.example.pszzapp.presentation.destinations.TreatmentScreenDestination
import com.example.pszzapp.presentation.hive.create.CreateHiveConstants
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
@Composable
fun HiveScreen(
    id: String,
    destinationsNavigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    viewModel: HiveViewModel = koinViewModel(parameters = { parametersOf(id) }),
) {
    val backStackEntry = navController.currentBackStackEntry
    val refresh = backStackEntry?.savedStateHandle?.get<Boolean>("refresh")

    LaunchedEffect(refresh) {
        if (refresh == true) {
            viewModel.refreshHive(id)
        }
    }

    val hiveState = viewModel.hiveState.collectAsState().value
    val overviewsState = viewModel.overviewsState.collectAsState().value
    val lastOverviewIdState = viewModel.lastOverviewIdState.collectAsState().value

    val removeHiveState = viewModel.removeHiveState.collectAsState().value

    if (removeHiveState is RemoveHiveState.Success) {
        destinationsNavigator.navToApiariesScreen()
    }

    var isModalActive by remember { mutableStateOf(false) }

    when (hiveState) {
        is HiveState.Success -> {
            val hiveId = hiveState.hive.id
            val menuItems = listOf(
                DropdownMenuItemData(
                    icon = Icons.Outlined.Edit,
                    text = "Edytuj ul",
                    onClick = {
                        destinationsNavigator.navToEditHive(
                            apiaryId = hiveState.hive.apiaryId,
                            hiveModel = hiveState.hive
                        )
                    }
                ),
//                DropdownMenuItemData(
//                    icon = Icons.Outlined.PinDrop,
//                    text = "Ogólne informacje",
//                    onClick = { }
//                ),
//                DropdownMenuItemData(
//                    icon = Icons.Outlined.WbSunny,
//                    text = "Informacje o matce",
//                    onClick = { }
//                ),
                DropdownMenuItemData(
                    icon = Icons.Outlined.AttachEmail,
                    text = "Dodaj przegląd",
                    onClick = {
                        destinationsNavigator.navigate(
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

            val buttonTilesNavigation = mutableListOf(
                ButtonTile(
                    title = "Dodaj przegląd",
                    icon = R.drawable.ic_tile_button,
                    direction = CreateOverviewStep1ScreenDestination(
                        hiveId = hiveId,
                        apiaryId = hiveState.hive.apiaryId
                    ),
                ),
                ButtonTile(
                    title = "Leczenie",
                    icon = R.drawable.ic_tile_button,
                    direction = TreatmentScreenDestination(hiveId = hiveId),
                ),
                ButtonTile(
                    title = "Karmienie",
                    icon = R.drawable.ic_tile_button,
                    direction = FeedingScreenDestination(hiveId = hiveId),
                ),
                ButtonTile(
                    title = "Przypomnienia",
                    icon = R.drawable.ic_tile_button,
                    direction = CreateApiaryScreenDestination(),
                ),
            )
            if (lastOverviewIdState is LastOverviewIdState.Success && lastOverviewIdState.overviewId != null) {
                buttonTilesNavigation.add(
                    0,
                    ButtonTile(
                        title = "Raport",
                        icon = R.drawable.ic_tile_button,
                        direction = OverviewScreenDestination(overviewId = lastOverviewIdState.overviewId),
                    )
                )
            }

            HiveLayout(
                navController = navController,
                resultNavigator = resultNavigator,
                menuItems = menuItems,
                isModalActive = isModalActive,
                setModal = { isModalActive = it },
                hive = hiveState.hive,
                overviewsState = overviewsState,
                navigator = destinationsNavigator,
                buttonTilesNavigation = buttonTilesNavigation,
                geOverviewsByHiveId = viewModel::geOverviewsByHiveId,
                removeHive = viewModel::removeHive
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
    geOverviewsByHiveId: (String) -> Unit,
    removeHive: (String) -> Unit,
) {
    var isDropdownMenuVisible by remember { mutableStateOf(false) }

    var titlesState by remember { mutableIntStateOf(0) }
    val titles = listOf(
        TitleTab(
            title = "Informacje z pasieki",
        ),
        TitleTab(
            title = "Przeglądy",
            onClick = {
                geOverviewsByHiveId(hive.id)
            },
        )
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
                onSettingsClick = { isDropdownMenuVisible = true },
                isDropdownMenuVisible = isDropdownMenuVisible,
            )

            PrimaryTabRow(
                selectedTabIndex = titlesState,
                indicator = {},
                divider = {},
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                containerColor = Color.Transparent,
            ) {
                titles.forEachIndexed { index, titleTab ->
                    Tab(
                        selected = titlesState == index,
                        onClick = {
                            titlesState = index
                            titleTab.onClick?.let { it() }
                        },
                        text = {
                            Text(
                                text = titleTab.title,
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
                    when (overviewsState) {
                        is OverviewsState.Success -> {
                            OverviewsLazyColumn(
                                overviews = overviewsState.overviews,
                                navToOverview = navigator::navToOverview
                            )
                        }

                        is OverviewsState.Loading -> LoadingDialog()
                        is OverviewsState.Error -> TextError(overviewsState.message)
                        is OverviewsState.None -> Unit
                    }
                }
            }
        }
    }

    ModalDialog(
        dialogTitle = "Usuń pasiekę",
        dialogText = "Czy na pewno chcesz usunąć pasiekę?",
        confirmButtonText = stringResource(R.string.remove_modal_remove),
        dismissButtonText = stringResource(R.string.remove_modal_cancel),
        icon = Icons.Filled.Warning,
        isModalActive = isModalActive,
        onDismissRequest = { setModal(false) },
        onConfirmation = { removeHive(hive.id) },
    )
}

data class TitleTab(
    val title: String,
    val onClick: (() -> Unit)? = null,
)

private fun DestinationsNavigator.navToEditHive(apiaryId: String, hiveModel: HiveModel) =
    navigate(CreateHiveStep1ScreenDestination(apiaryId = apiaryId, hiveModel))