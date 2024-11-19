package com.example.pszzapp.presentation.overview.create

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.presentation.apiary.create.InputSelect
import com.example.pszzapp.presentation.apiary.create.TabsSelect
import com.example.pszzapp.presentation.auth.base.Button
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.destinations.CreateOverviewStep2ScreenDestination
import com.example.pszzapp.presentation.destinations.OverviewScreenDestination
import com.example.pszzapp.presentation.hive.create.OptionsModal
import com.example.pszzapp.presentation.hive.create.OptionsState
import com.example.pszzapp.presentation.hive.create.StepsBelt
import com.example.pszzapp.presentation.hive.create.rememberOptionsState
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun CreateOverviewStep1Screen(
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    viewModel: CreateOverviewViewModel = koinViewModel(),
    hiveId: String,
    apiaryId: String,
    overviewModel: OverviewModel? = null,
) {
    when (val createOverviewState = viewModel.createOverviewState.collectAsState().value) {
        is CreateOverviewState.Loading -> LoadingDialog()

        is CreateOverviewState.Success -> CreateOverviewLayout(
            navController = navController,
            resultNavigator = resultNavigator,
            hiveId = hiveId,
            apiaryId = apiaryId,
            createOverviewState = createOverviewState,
            onFormComplete = {
                navigator.navigate(
                    CreateOverviewStep2ScreenDestination(
                        isEditing = overviewModel != null,
                        overviewData = it
                    )
                )
            },
            overviewModel = overviewModel,
        )

        is CreateOverviewState.Redirect -> navigator.navigate(OverviewScreenDestination(overviewId = createOverviewState.overviewId))

        is CreateOverviewState.Error -> TextError(createOverviewState.message)
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun CreateOverviewLayout(
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    hiveId: String,
    apiaryId: String,
    createOverviewState: CreateOverviewState,
    onFormComplete: (OverviewModel) -> Unit,
    overviewModel: OverviewModel? = null,
) {
    val isEditing = overviewModel != null
    var overviewData by remember(overviewModel) { mutableStateOf(overviewModel ?: OverviewModel(
        hiveId = hiveId,
        apiaryId = apiaryId,
    )) }

    var strength by rememberOptionsState(
        options = OverviewConstants.strength,
        selectedOption = overviewData.strength,
        changed = isEditing,
    )
    var mood by rememberOptionsState(
        options = OverviewConstants.mood,
        selectedOption = overviewData.mood,
        changed = isEditing,
    )
    var beeMaggots by rememberOptionsState(
        options = OverviewConstants.beeMaggots,
        selectedOption = overviewData.beeMaggots,
        changed = isEditing,
    )
    var showCells by rememberOptionsState(
        options = OverviewConstants.showCells,
        selectedOption = if (overviewData.cellType != 0) 1 else 0,
        changed = overviewData.cellType != 0,
    )
    var cellTypes by rememberOptionsState(
        options = OverviewConstants.cells,
        selectedOption = overviewData.cellType,
        changed = isEditing,
    )

    BoxWithConstraints(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        BackgroundShapes()

        Column(
            modifier = Modifier
                .heightIn(min = maxHeight)
                .verticalScroll(rememberScrollState()),
        ) {
            TopBar(
                backNavigation = { resultNavigator.navigateBack() },
                title = if (overviewModel != null) "Edytuj przegląd" else "Dodaj przegląd",
            )

            StepsBelt(maxSteps = 3, currentStep = 1)

            CreateOverviewForm(
                strength = strength,
                mood = mood,
                beeMaggots = beeMaggots,
                showCells = showCells,
                cellTypes = cellTypes,
                onStrengthChange = { strength = it },
                onMoodChange = { mood = it },
                onBeeMaggotsChange = { beeMaggots = it },
                onShowCellsChange = { showCells = it },
                onCellTypeChange = { cellTypes = it },
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
            ) {
                if (createOverviewState is CreateOverviewState.Error) {
                    TextError(createOverviewState.message)
                }

                Button(
                    text = stringResource(R.string.next),
                    showIcon = true,
                    onClick = {
                        overviewData = overviewData.copy(
                            strength = strength.selectedOption,
                            mood = mood.selectedOption,
                            beeMaggots = beeMaggots.selectedOption,
                            cellType = cellTypes.selectedOption,
                        )
                        onFormComplete(overviewData)
                    }
                )
            }
        }
        OptionsModal(
            optionsState = cellTypes,
            onOptionSelected = { cellTypes = it }
        )
    }
}


@Composable
private fun CreateOverviewForm(
    strength: OptionsState,
    mood: OptionsState,
    beeMaggots: OptionsState,
    showCells: OptionsState,
    cellTypes: OptionsState,
    onStrengthChange: (OptionsState) -> Unit,
    onMoodChange: (OptionsState) -> Unit,
    onBeeMaggotsChange: (OptionsState) -> Unit,
    onShowCellsChange: (OptionsState) -> Unit,
    onCellTypeChange: (OptionsState) -> Unit,
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
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TabsSelect(
            label = "Siła",
            selectedOption = strength.selectedOption,
            onOptionSelected = { onStrengthChange(strength.copy(selectedOption = it)) },
            options = strength.options
        )
        TabsSelect(
            label = "Nastrój",
            selectedOption = mood.selectedOption,
            onOptionSelected = { onMoodChange(mood.copy(selectedOption = it)) },
            options = mood.options
        )
        TabsSelect(
            label = "Czerw",
            selectedOption = beeMaggots.selectedOption,
            onOptionSelected = { onBeeMaggotsChange(beeMaggots.copy(selectedOption = it)) },
            options = beeMaggots.options
        )
        TabsSelect(
            label = "Mateczniki",
            selectedOption = showCells.selectedOption,
            onOptionSelected = { onShowCellsChange(showCells.copy(selectedOption = it)) },
            options = showCells.options
        )

        if (showCells.selectedOption == 1) {
            InputSelect(
                value = if (cellTypes.changed) stringResource(cellTypes.options[cellTypes.selectedOption]) else "",
                showPlaceholder = !cellTypes.changed,
                selectedOption = cellTypes.selectedOption,
                setExpanded = { onCellTypeChange(cellTypes.copy(expanded = it)) },
                options = cellTypes.options,
            )
        }
    }
}


object OverviewConstants {
    val strength = listOf(
        R.string.overview_strengts_1,
        R.string.overview_strengts_2,
        R.string.overview_strengts_3,
    )
    val mood = listOf(
        R.string.overview_moods_1,
        R.string.overview_moods_2,
        R.string.overview_moods_3,
    )
    val beeMaggots = listOf(
        R.string.no,
        R.string.yes,
    )
    val showCells = listOf(
        R.string.no,
        R.string.yes,
    )
    val cells = listOf(
        R.string.overview_moods_1,
        R.string.overview_moods_1,
        R.string.overview_moods_1,
        R.string.overview_moods_1,
    )
    val numbers = listOf(
        R.string.overview_numbers_1,
        R.string.overview_numbers_2,
        R.string.overview_numbers_3,
        R.string.overview_numbers_4,
        R.string.overview_numbers_5,
        R.string.overview_numbers_6,
        R.string.overview_numbers_7,
        R.string.overview_numbers_8,
        R.string.overview_numbers_9,
        R.string.overview_numbers_10,
    )
    val partitionGrid = listOf(
        R.string.no,
        R.string.yes,
    )
    val insulator = listOf(
        R.string.no,
        R.string.yes,
    )
    val pollenCatcher = listOf(
        R.string.no,
        R.string.yes,
    )
    val propolisCatcher = listOf(
        R.string.no,
        R.string.yes,
    )
    val honeyWarehouse = listOf(
        R.string.no,
        R.string.yes,
    )
    val foodAmount = listOf(
        R.string.overview_strengts_1,
        R.string.overview_strengts_2,
        R.string.overview_strengts_3,
    )
    val workFrame = listOf(
        R.string.no,
        R.string.yes,
    )
}


