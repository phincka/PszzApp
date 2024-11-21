package com.example.pszzapp.presentation.overview

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.components.modalDialog.ModalDialog
import com.example.pszzapp.data.model.DetailedOverviewModel
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.data.model.toDetailedOverviewModel
import com.example.pszzapp.data.util.DropdownMenuItemData
import com.example.pszzapp.presentation.apiary.create.isRouteInBackStack
import com.example.pszzapp.presentation.apiary.navToDashboard
import com.example.pszzapp.presentation.apiary.navToHiveScreen
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.destinations.CreateOverviewStep1ScreenDestination
import com.example.pszzapp.presentation.main.SnackbarHandler
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.format.DateTimeFormatter

@SuppressLint("StateFlowValueCalledInComposition", "UnrememberedGetBackStackEntry")
@Destination
@Composable
fun OverviewScreen(
    overviewId: String,
    message: String? = null,
    destinationsNavigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    viewModel: OverviewViewModel = koinViewModel(parameters = { parametersOf(overviewId) }),
    snackbarHandler: SnackbarHandler,
) {
    val overviewState = viewModel.overviewState.collectAsState().value
    val removeOverviewState = viewModel.removeOverviewState.collectAsState().value
    val backStackEntry = navController.currentBackStackEntry
    val refresh = backStackEntry?.savedStateHandle?.get<Boolean>("refresh")

    LaunchedEffect(removeOverviewState, refresh) {
        launch {
            if (removeOverviewState is RemoveOverviewState.Error) snackbarHandler.showErrorSnackbar(
                message = removeOverviewState.message
            )

            if (refresh == true) viewModel.getOverviewById(overviewId)
        }
    }

    var isModalActive by remember { mutableStateOf(false) }

    when (overviewState) {
        is OverviewState.Success -> {
            LaunchedEffect(message) {
                launch {
                    message?.let {
                        snackbarHandler.showSuccessSnackbar(
                            message = it
                        )
                    }
                }
            }

            if (removeOverviewState is RemoveOverviewState.Success) {
                val message = "Pomyślnie usunięto przegląd."

                if (navController.isRouteInBackStack("dashboard_screen")) {
                    navController.getBackStackEntry("dashboard_screen").savedStateHandle["refresh"] = true
                    destinationsNavigator.navToDashboard(message = message)
                } else {
                    navController.getBackStackEntry("hive_Screen/${overviewState.overview.hiveId}").savedStateHandle["refresh"] = true
                    destinationsNavigator.navToHiveScreen(hiveId = removeOverviewState.hiveId, message = message)
                }

            }

            val menuItems = listOf(
                DropdownMenuItemData(
                    icon = Icons.Outlined.Edit,
                    text = "Edytuj przegląd",
                    onClick = { destinationsNavigator.navToEditOverview(
                        apiaryId = overviewState.overview.apiaryId,
                        hiveId = overviewState.overview.hiveId,
                        overviewModel = overviewState.overview,
                    ) }
                ),
                DropdownMenuItemData(
                    icon = Icons.Outlined.Clear,
                    text = stringResource(R.string.hive_nav_remove_hive),
                    onClick = {
                        isModalActive = true
                    }
                ),
            )

            OverviewLayout(
                navController = navController,
                resultNavigator = resultNavigator,
                menuItems = menuItems,
                isModalActive = isModalActive,
                setModal = { isModalActive = it },
                removeOverview = { viewModel.removeOverview(overviewId, overviewState.overview.hiveId) },
                detailedOverview = overviewState.overview.toDetailedOverviewModel(),
            )
        }

        is OverviewState.Loading -> LoadingDialog()
        is OverviewState.Error -> TextError(overviewState.message)
    }
}


@Composable
private fun OverviewLayout(
    navController: NavController,
    resultNavigator: ResultBackNavigator<Boolean>,
    menuItems: List<DropdownMenuItemData>,
    isModalActive: Boolean,
    setModal: (Boolean) -> Unit,
    removeOverview: (String) -> Unit,
    detailedOverview: DetailedOverviewModel,
) {
    var isDropdownMenuVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        BackgroundShapes()

        Column {
            TopBar(
                backNavigation = { resultNavigator.navigateBack() },
                title = "Przegląd",
                warningInfo = detailedOverview.warningInfo,
                goodInfo = detailedOverview.goodInfo,
                columnInfo = true,
                subtitle = detailedOverview.overviewDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                menuItems = menuItems,
                onSettingsClick = { isDropdownMenuVisible = true },
                isDropdownMenuVisible = isDropdownMenuVisible,
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                detailedOverview.overviewTiles.forEach { tile ->
                    Row(
                        modifier = Modifier
                            .graphicsLayer {
                                shadowElevation = 4.dp.toPx()
                                shape = RoundedCornerShape(8.dp)
                                clip = false
                            }
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                color = AppTheme.colors.white
                            )
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = tile.title,
                                style = Typography.label,
                                fontWeight = FontWeight.SemiBold,
                                color = AppTheme.colors.neutral90,
                            )

                            tile.overviewItem.forEachIndexed { index, item ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item.key,
                                        style = Typography.small,
                                        color = AppTheme.colors.neutral90,
                                    )

                                    Text(
                                        text = stringResource(item.value),
                                        style = Typography.small,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppTheme.colors.neutral60,
                                    )
                                }

                                if (index != tile.overviewItem.lastIndex) {
                                    HorizontalDivider(
                                        color = AppTheme.colors.neutral30
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    ModalDialog(
        dialogTitle = "Usuń przegląd",
        dialogText = "Czy na pewno chcesz usunąć przegląd?",
        confirmButtonText = stringResource(R.string.remove_modal_remove),
        dismissButtonText = stringResource(R.string.remove_modal_cancel),
        icon = Icons.Filled.Warning,
        isModalActive = isModalActive,
        onDismissRequest = { setModal(false) },
        onConfirmation = { removeOverview(detailedOverview.id) },
    )
}

private fun DestinationsNavigator.navToEditOverview(apiaryId: String, hiveId: String, overviewModel: OverviewModel?) =
    navigate(CreateOverviewStep1ScreenDestination(apiaryId = apiaryId, hiveId = hiveId, overviewModel = overviewModel))