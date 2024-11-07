package com.example.pszzapp.presentation.feeding.create

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.model.FeedingModel
import com.example.pszzapp.data.util.DropdownMenuItemData
import com.example.pszzapp.presentation.apiary.create.InputDate
import com.example.pszzapp.presentation.apiary.create.InputNumber
import com.example.pszzapp.presentation.apiary.create.InputSelect
import com.example.pszzapp.presentation.apiary.create.TabsSelect
import com.example.pszzapp.presentation.components.DatePicker
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.feeding.FeedingScreenViewModel
import com.example.pszzapp.presentation.hive.create.OptionsModal
import com.example.pszzapp.presentation.hive.create.OptionsState
import com.example.pszzapp.presentation.hive.create.rememberOptionsState
import com.example.pszzapp.presentation.hive.create.toFormattedDate
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.LocalDate
import java.util.Locale

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun CreateFeedingScreen(
    hiveId: String = "EpSAjdCNLwnVfdezmF45",
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
    FeedingLayout(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedingLayout(
    navController: NavController,
    resultNavigator: ResultBackNavigator<Boolean>,
    menuItems: List<DropdownMenuItemData>,
    isModalActive: Boolean,
    setModal: (Boolean) -> Unit,
    navigator: DestinationsNavigator,
//    overview: DetailedOverviewModel,
) {
    var feedingData by remember {
        mutableStateOf(
            FeedingModel(
                id = "",
                uid = "",
                apiaryId = "",
                hiveId = "",
                concentration = "0",
                syrupAmount = "0",
                sugarAmount = "0",
                waterAmount = "0",
                feedingDate = LocalDate.now(),
                cakeAmount = "0",
            )
        )
    }

    var concentration by rememberOptionsState(FeedConstants.concentration)
    var shareToApiary by rememberOptionsState(FeedConstants.shareToApiary)
    var cakeTypeOptions by rememberOptionsState(FeedConstants.cakeType)
    val feedingDateState = rememberMaterialDialogState()

    var titlesState by remember { mutableIntStateOf(0) }
    val titles = listOf(
        "Syrop",
        "Ciasto"
    )

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
                title = "Dodaj karmienie",
                menuItems = menuItems,
            )

            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                titles.forEachIndexed { index, title ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .height(34.dp)
                            .clickable(onClick = {
                                titlesState = index
                            })
                            .then(
                                if (index == titlesState) {
                                    Modifier
                                        .background(AppTheme.colors.primary50)
                                } else {
                                    Modifier
                                        .background(AppTheme.colors.primary10)
                                }
                            ),
                    ) {
                        Text(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = Typography.small,
                            fontWeight = FontWeight.SemiBold,
                            color = if (titlesState == index) AppTheme.colors.white else AppTheme.colors.primary30,
                        )
                    }
                }
            }

            when (titlesState) {
                0 -> {
                    SugarForm(
                        feedingData = feedingData,
                        onFeedingDataChange = { feedingData = it },
                        concentration = concentration,
                        shareToApiary = shareToApiary,
                        onConcentrationSelected = { concentration = it },
                        onShareToApiarySelected = { shareToApiary = it },
                        onFeedingDateClick = { feedingDateState.show() },
                    )
                }

                1 -> {
                    CakeForm(
                        feedingData = feedingData,
                        onFeedingDataChange = { feedingData = it },
                        cakeTypeOptions = cakeTypeOptions,
                        shareToApiary = shareToApiary,
                        onCakeTypeChange = { cakeTypeOptions = it },
                        onShareToApiarySelected = { shareToApiary = it },
                        onFeedingDateClick = { feedingDateState.show() },
                    )
                }
            }

            DatePicker(
                pickedDate = feedingData.feedingDate,
                setPickedDate = { feedingData = feedingData.copy(feedingDate = it) },
                dateDialogState = feedingDateState
            )
        }

        OptionsModal(
            optionsState = cakeTypeOptions,
            onOptionSelected = { cakeTypeOptions = it }
        )
    }
}


@Composable
private fun SugarForm(
    feedingData: FeedingModel,
    onFeedingDataChange: (FeedingModel) -> Unit,
    concentration: OptionsState,
    shareToApiary: OptionsState,
    onConcentrationSelected: (OptionsState) -> Unit,
    onShareToApiarySelected: (OptionsState) -> Unit,
    onFeedingDateClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .graphicsLayer {
                shadowElevation = 4.dp.toPx()
                shape = RoundedCornerShape(8.dp)
                clip = true
            }
            .clip(RoundedCornerShape(8.dp))
            .background(AppTheme.colors.white)
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TabsSelect(
            label = "Stężenie",
            selectedOption = concentration.selectedOption,
            onOptionSelected = { onConcentrationSelected(concentration.copy(selectedOption = it)) },
            options = concentration.options
        )

        val concentration = stringResource(FeedConstants.concentration[concentration.selectedOption])

        InputNumber(
            label = "Ilość syropu (litry)",
            value = feedingData.syrupAmount,
            onValueChange = {
                val amountData = calcSyrup(
                    baseValue = "syrup",
                    sugar = feedingData.sugarAmount,
                    water = feedingData.waterAmount,
                    syrup = it,
                    concentration = concentration
                )
                onFeedingDataChange(
                    feedingData.copy(
                        syrupAmount = amountData.syrup,
                        sugarAmount = amountData.sugar,
                        waterAmount = amountData.water,
                    )
                )
            }
        )

        InputNumber(
            label = "Ilość cukru (kg)",
            value = feedingData.sugarAmount,

            onValueChange = {
                val amountData = calcSyrup(
                    baseValue = "sugar",
                    sugar = it,
                    water = feedingData.waterAmount,
                    syrup = feedingData.syrupAmount,
                    concentration = concentration
                )
                onFeedingDataChange(
                    feedingData.copy(
                        syrupAmount = amountData.syrup,
                        sugarAmount = amountData.sugar,
                        waterAmount = amountData.water,
                    )
                )
            }
        )

        InputNumber(
            label = "Ilość wody w syropie (kg)",
            value = feedingData.waterAmount,
            onValueChange = {
                val amountData = calcSyrup(
                    baseValue = "water",
                    sugar = feedingData.sugarAmount,
                    water = it,
                    syrup = feedingData.syrupAmount,
                    concentration = concentration
                )
                onFeedingDataChange(
                    feedingData.copy(
                        syrupAmount = amountData.syrup,
                        sugarAmount = amountData.sugar,
                        waterAmount = amountData.water,
                    )
                )
            }
        )

        InputDate(
            value = feedingData.feedingDate.toFormattedDate(),
            label = "Data karmienia",
            setExpanded = onFeedingDateClick
        )

        TabsSelect(
            label = "Dodać dla całej pasieki?",
            selectedOption = shareToApiary.selectedOption,
            onOptionSelected = { onShareToApiarySelected(shareToApiary.copy(selectedOption = it)) },
            options = shareToApiary.options
        )
    }
}


@Composable
private fun CakeForm(
    feedingData: FeedingModel,
    onFeedingDataChange: (FeedingModel) -> Unit,
    cakeTypeOptions: OptionsState,
    shareToApiary: OptionsState,
    onCakeTypeChange: (OptionsState) -> Unit,
    onShareToApiarySelected: (OptionsState) -> Unit,
    onFeedingDateClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .graphicsLayer {
                shadowElevation = 4.dp.toPx()
                shape = RoundedCornerShape(8.dp)
                clip = true
            }
            .clip(RoundedCornerShape(8.dp))
            .background(AppTheme.colors.white)
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InputNumber(
            label = "Ilość ciasta (kg)",
            value = feedingData.cakeAmount,
            onValueChange = {
                onFeedingDataChange(
                    feedingData.copy(
                        cakeAmount = it
                    )
                )
            }
        )

        InputSelect(
            value = cakeTypeOptions.getCurrentOption(),
            label = "Rodzaj ciasta",
            showPlaceholder = !cakeTypeOptions.changed,
            selectedOption = cakeTypeOptions.selectedOption,
            setExpanded = { onCakeTypeChange(cakeTypeOptions.copy(expanded = it)) },
            options = cakeTypeOptions.options
        )

        InputDate(
            value = feedingData.feedingDate.toFormattedDate(),
            label = "Data karmienia",
            setExpanded = onFeedingDateClick
        )

        TabsSelect(
            label = "Dodać dla całej pasieki?",
            selectedOption = shareToApiary.selectedOption,
            onOptionSelected = { onShareToApiarySelected(shareToApiary.copy(selectedOption = it)) },
            options = shareToApiary.options
        )
    }
}

object FeedConstants {
    val concentration = listOf(
        R.string.concentration_2_1,
        R.string.concentration_1_1,
        R.string.concentration_3_2,
    )
    val shareToApiary = listOf(
        R.string.no,
        R.string.yes,
    )
    val cakeType = listOf(
        R.string.no,
        R.string.yes,
    )
}

@SuppressLint("DefaultLocale")
fun calcSyrup(
    baseValue: String,
    sugar: String,
    water: String,
    syrup: String,
    concentration: String = "1:1"
): SyrupCalcResult {
    if (syrup.isEmpty() || sugar.isEmpty() || water.isEmpty()) {
        return SyrupCalcResult(
            syrup = syrup,
            sugar = sugar,
            water = water
        )
    }

    var syrupValue = syrup.toDoubleOrNull() ?: 1.0
    var sugarValue = sugar.toDoubleOrNull() ?: 1.0
    var waterValue = water.toDoubleOrNull() ?: 1.0

    when (concentration) {
        "1:1" -> {
            when (baseValue) {
                "syrup" -> {
                    sugarValue = syrup.toDouble() * 0.63
                    waterValue = syrup.toDouble() * 0.63
                }

                "sugar" -> {
                    syrupValue = sugarValue * 1.63
                    waterValue = sugarValue
                }

                "water" -> {
                    syrupValue = waterValue * 1.63
                    sugarValue = waterValue
                }
            }
        }

        "2:1" -> {
            when (baseValue) {
                "syrup" -> {
                    sugarValue = syrupValue * 0.834
                    waterValue = syrupValue * 0.417
                }

                "sugar" -> {
                    waterValue = sugarValue * 0.5
                    syrupValue = (sugarValue + waterValue) / 1.25
                }

                "water" -> {
                    sugarValue = waterValue * 2
                    syrupValue = (sugarValue + waterValue) / 1.25
                }
            }
        }

        "3:2" -> {
            when (baseValue) {
                "syrup" -> {
                    sugarValue = syrupValue * 0.75
                    waterValue = syrupValue * 0.5
                }

                "sugar" -> {
                    waterValue = sugarValue * 0.66
                    syrupValue = (sugarValue + waterValue) / 1.25
                }

                "water" -> {
                    sugarValue = waterValue * 1.5
                    syrupValue = (sugarValue + waterValue) / 1.25
                }
            }
        }

        else -> {
            return SyrupCalcResult(
                syrup = syrup,
                sugar = sugar,
                water = water,
            )
        }
    }

    return SyrupCalcResult(
        syrup = String.format(Locale.US, "%.2f", syrupValue),
        sugar = String.format(Locale.US, "%.2f", sugarValue),
        water = String.format(Locale.US, "%.2f", waterValue),
    )
}

data class SyrupCalcResult(
    val syrup: String,
    val sugar: String,
    val water: String,
)

