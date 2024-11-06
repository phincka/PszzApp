package com.example.pszzapp.presentation.treatment

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.util.DropdownMenuItemData
import com.example.pszzapp.presentation.apiaries.EmptyList
import com.example.pszzapp.presentation.apiary.navToDashboard
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.destinations.CreateFeedingScreenDestination
import com.example.pszzapp.presentation.feeding.FeedingScreenViewModel
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
fun TreatmentScreen(
    hiveId: String,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    overviewViewModel: FeedingScreenViewModel = koinViewModel(parameters = { parametersOf(hiveId) })
) {
//    TextError(overviewId)
//    val hiveState = hiveViewModel.hiveState.collectAsState().value
    val overviewState = overviewViewModel.overviewState.collectAsState().value

    var isModalActive by remember { mutableStateOf(false) }

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
    TreatmentLayout(
        navController = navController,
        resultNavigator = resultNavigator,
        menuItems = menuItems,
        isModalActive = isModalActive,
        setModal = { isModalActive = it },
        navigator = navigator,
//        overview = overviewState.overview
    )

//    when (overviewState) {
//        is FeedingScreenState.Success -> {
//
//
//            FeedingLayout(
//                navController = navController,
//                resultNavigator = resultNavigator,
//                menuItems = menuItems,
//                isModalActive = isModalActive,
//                setModal = { isModalActive = it },
//                navigator = navigator,
//                overview = overviewState.overview
//            )
//        }
//
//        is FeedingScreenState.Loading -> LoadingDialog()
//        is FeedingScreenState.Error -> TextError(overviewState.message)
//    }
}

@Composable
private fun TreatmentLayout(
    navController: NavController,
    resultNavigator: ResultBackNavigator<Boolean>,
    menuItems: List<DropdownMenuItemData>,
    isModalActive: Boolean,
    setModal: (Boolean) -> Unit,
    navigator: DestinationsNavigator,
//    overview: DetailedOverviewModel,
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
                title = "Leczenie",
                menuItems = menuItems,
                onSettingsClick = { isDropdownMenuVisible = true },
            )

            FiltersBelt()

            FeedingInfo(
                hiveName = "Maja",
                sugar = 12,
                cake = 2,
            )

            EmptyList(
                title = "Brak karmienia",
                text = "Nie karmiłeś jeszcze tego ula. Kknij w przycisk i dodaj karmienia.",
                buttonTitle = "Dodaj karmienie",
                navigate = { navigator.navToDashboard() },
                showIcon = false,
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
//                overview.overviewTiles.forEach { tile ->
//                    Row(
//                        modifier = Modifier
//                            .graphicsLayer {
//                                shadowElevation = 4.dp.toPx()
//                                shape = RoundedCornerShape(8.dp)
//                                clip = false
//                            }
//                            .clip(RoundedCornerShape(8.dp))
//                            .background(
//                                color = AppTheme.colors.white
//                            )
//                            .padding(8.dp)
//                            .fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically,
//                    ) {
//                        Column {
//                            Text(
//                                text = tile.title,
//                                style = Typography.label,
//                                fontWeight = FontWeight.SemiBold,
//                                color = AppTheme.colors.neutral90,
//                            )
//
//                            tile.overviewItem.forEachIndexed { index, item ->
//                                Row(
//                                    Modifier
//                                        .fillMaxWidth()
//                                        .padding(vertical = 6.dp),
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Text(
//                                        text = item.key,
//                                        style = Typography.small,
//                                        color = AppTheme.colors.neutral90,
//                                    )
//
//                                    Text(
//                                        text = stringResource(item.value),
//                                        style = Typography.small,
//                                        fontWeight = FontWeight.SemiBold,
//                                        color = AppTheme.colors.neutral60,
//                                    )
//                                }
//
//                                if (index != tile.overviewItem.lastIndex) {
//                                    HorizontalDivider(
//                                        color = AppTheme.colors.neutral30
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
            }
        }
    }
}

@Composable
private fun FiltersBelt(
    onFilterClick: () -> Unit = {},
    onResetCounterClick: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable(onClick = onFilterClick),
        ) {
            Image(
                painter = painterResource(R.drawable.settings),
                contentDescription = "arrow_right",
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Filtruj",
                style = Typography.small,
                color = AppTheme.colors.neutral90,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable(onClick = onResetCounterClick),
        ) {
            Text(
                text = "Resetuj licznik",
                style = Typography.small,
                color = AppTheme.colors.neutral90,
            )
            Image(
                painter = painterResource(R.drawable.reset),
                contentDescription = "arrow_right",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@Composable
private fun FeedingInfo(
    hiveName: String,
    sugar: Int? = null,
    cake: Int? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            buildAnnotatedString {
                append("Ul $hiveName")
                sugar.let {
                    append(" otrzymał ")
                    withStyle(
                        style = SpanStyle(
                            color = AppTheme.colors.primary50
                        )
                    ) {
                        append("$sugar kg cukru")
                    }
                }
                cake.let {
                    append(" oraz ")
                    withStyle(
                        style = SpanStyle(
                            color = AppTheme.colors.primary50
                        )
                    ) {
                        append("$cake kg ciasta")
                    }
                }
            },
            style = Typography.label,
            color = AppTheme.colors.neutral90,
            fontWeight = FontWeight.SemiBold,
        )
    }
}