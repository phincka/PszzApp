package com.example.pszzapp.presentation.overview

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.model.DetailedOverviewModel
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.data.util.DropdownMenuItemData
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.presentation.overview.create.OverviewConstants
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.format.DateTimeFormatter

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun OverviewScreen(
    overviewId: String,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    overviewViewModel: OverviewViewModel = koinViewModel(parameters = { parametersOf(overviewId) })
) {
//    TextError(overviewId)
//    val hiveState = hiveViewModel.hiveState.collectAsState().value
    val overviewState = overviewViewModel.overviewState.collectAsState().value

    var isModalActive by remember { mutableStateOf(false) }

    when (overviewState) {
        is OverviewState.Success -> {
            val menuItems = listOf(
                DropdownMenuItemData(
                    icon = Icons.Outlined.Edit,
                    text = "Edytuj przegląd",
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

            OverviewLayout(
                navController = navController,
                resultNavigator = resultNavigator,
                menuItems = menuItems,
                isModalActive = isModalActive,
                setModal = { isModalActive = it },
                navigator = navigator,
                overview = overviewState.overview
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
    navigator: DestinationsNavigator,
    overview: DetailedOverviewModel,
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
                warningInfo = overview.warningInfo,
                goodInfo = overview.goodInfo,
                columnInfo = true,
                subtitle = overview.overviewDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                menuItems = menuItems,
                isModalActive = isModalActive,
                setModal = setModal,
                onSettingsClick = { isDropdownMenuVisible = true },
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                overview.overviewTiles.forEach { tile ->
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
}