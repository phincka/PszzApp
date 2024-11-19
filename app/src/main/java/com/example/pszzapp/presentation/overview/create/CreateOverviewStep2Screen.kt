package com.example.pszzapp.presentation.overview.create

import android.annotation.SuppressLint
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
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.presentation.apiary.create.InputSelect
import com.example.pszzapp.presentation.apiary.create.TabsSelect
import com.example.pszzapp.presentation.auth.base.Button
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.destinations.CreateOverviewStep3ScreenDestination
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
fun CreateOverviewStep2Screen(
    isEditing: Boolean = false,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    viewModel: CreateOverviewViewModel = koinViewModel(),
    overviewData: OverviewModel,
) {
    when (val createOverviewState = viewModel.createOverviewState.collectAsState().value) {
        is CreateOverviewState.Loading -> LoadingDialog()

        is CreateOverviewState.Success -> CreateOverviewLayout(
            isEditing = isEditing,
            navController = navController,
            resultNavigator = resultNavigator,
            createOverviewState = createOverviewState,
            onFormComplete = {
                navigator.navigate(
                    CreateOverviewStep3ScreenDestination(
                        isEditing = isEditing,
                        overviewData = it
                    )
                )
            },
            overviewData = overviewData,
        )

        is CreateOverviewState.Redirect -> navigator.navigate(OverviewScreenDestination(overviewId = createOverviewState.overviewId))

        is CreateOverviewState.Error -> TextError(createOverviewState.message)
    }
}

@Composable
private fun CreateOverviewLayout(
    isEditing: Boolean = false,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    createOverviewState: CreateOverviewState,
    onFormComplete: (OverviewModel) -> Unit,
    overviewData: OverviewModel,
) {
    var overviewDataStep2: OverviewModel by remember {
        mutableStateOf(overviewData)
    }

    var partitionGrid by rememberOptionsState(
        options = OverviewConstants.partitionGrid,
        selectedOption = overviewDataStep2.partitionGrid,
        changed = isEditing,
    )
    var insulator by rememberOptionsState(
        options = OverviewConstants.insulator,
        selectedOption = overviewDataStep2.insulator,
        changed = isEditing,
    )
    var pollenCatcher by rememberOptionsState(
        options = OverviewConstants.pollenCatcher,
        selectedOption = overviewDataStep2.pollenCatcher,
        changed = isEditing,
    )
    var propolisCatcher by rememberOptionsState(
        options = OverviewConstants.propolisCatcher,
        selectedOption = overviewDataStep2.propolisCatcher,
        changed = isEditing,
    )
    var honeyWarehouse by rememberOptionsState(
        options = OverviewConstants.honeyWarehouse,
        selectedOption = overviewDataStep2.honeyWarehouse,
        changed = isEditing,
    )
    var honeyWarehouseNumbers by rememberOptionsState(
        options = OverviewConstants.numbers,
        selectedOption = overviewDataStep2.honeyWarehouseNumbers,
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
                title = if (isEditing) "Edytuj przegląd" else "Dodaj przegląd",
            )

            StepsBelt(maxSteps = 3, currentStep = 2)

            CreateOverviewForm(
                partitionGrid = partitionGrid,
                insulator = insulator,
                pollenCatcher = pollenCatcher,
                propolisCatcher = propolisCatcher,
                honeyWarehouse = honeyWarehouse,
                honeyWarehouseNumbers = honeyWarehouseNumbers,
                onPartitionGridChange = { partitionGrid = it },
                onInsulatorChange = { insulator = it },
                onPollenCatcherChange = { pollenCatcher = it },
                onPropolisCatcherChange = { propolisCatcher = it },
                onHoneyWarehouseChange = { honeyWarehouse = it },
                onHoneyWarehouseNumbersChange = { honeyWarehouseNumbers = it },
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
                        overviewDataStep2 = overviewDataStep2.copy(
                            partitionGrid = partitionGrid.selectedOption,
                            insulator = insulator.selectedOption,
                            pollenCatcher = pollenCatcher.selectedOption,
                            propolisCatcher = propolisCatcher.selectedOption,
                            honeyWarehouse = honeyWarehouse.selectedOption,
                            honeyWarehouseNumbers = honeyWarehouseNumbers.selectedOption,
                        )
                        onFormComplete(overviewDataStep2)
                    }
                )
            }
        }
        OptionsModal(
            optionsState = honeyWarehouseNumbers,
            onOptionSelected = { honeyWarehouseNumbers = it }
        )
    }
}


@Composable
private fun CreateOverviewForm(
    partitionGrid: OptionsState,
    insulator: OptionsState,
    pollenCatcher: OptionsState,
    propolisCatcher: OptionsState,
    honeyWarehouse: OptionsState,
    honeyWarehouseNumbers: OptionsState,
    onPartitionGridChange: (OptionsState) -> Unit,
    onInsulatorChange: (OptionsState) -> Unit,
    onPollenCatcherChange: (OptionsState) -> Unit,
    onPropolisCatcherChange: (OptionsState) -> Unit,
    onHoneyWarehouseChange: (OptionsState) -> Unit,
    onHoneyWarehouseNumbersChange: (OptionsState) -> Unit,
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
            label = "Krata odgrodowa",
            selectedOption = partitionGrid.selectedOption,
            onOptionSelected = { onPartitionGridChange(partitionGrid.copy(selectedOption = it)) },
            options = partitionGrid.options
        )
        TabsSelect(
            label = "Izolator",
            selectedOption = insulator.selectedOption,
            onOptionSelected = { onInsulatorChange(insulator.copy(selectedOption = it)) },
            options = insulator.options
        )
        TabsSelect(
            label = "Poławiacz pyłku",
            selectedOption = pollenCatcher.selectedOption,
            onOptionSelected = { onPollenCatcherChange(pollenCatcher.copy(selectedOption = it)) },
            options = pollenCatcher.options
        )
        TabsSelect(
            label = "Poławiacz Propolisu",
            selectedOption = propolisCatcher.selectedOption,
            onOptionSelected = { onPropolisCatcherChange(propolisCatcher.copy(selectedOption = it)) },
            options = propolisCatcher.options
        )
        TabsSelect(
            label = "Miodnia",
            selectedOption = honeyWarehouse.selectedOption,
            onOptionSelected = { onHoneyWarehouseChange(honeyWarehouse.copy(selectedOption = it)) },
            options = honeyWarehouse.options
        )

        if (honeyWarehouse.selectedOption == 1) {
            InputSelect(
                label = "Ilość nadstawek",
                value = if (honeyWarehouseNumbers.changed) stringResource(honeyWarehouseNumbers.options[honeyWarehouseNumbers.selectedOption]) else "",
                showPlaceholder = !honeyWarehouseNumbers.changed,
                selectedOption = honeyWarehouseNumbers.selectedOption,
                setExpanded = { onHoneyWarehouseNumbersChange(honeyWarehouseNumbers.copy(expanded = it)) },
                options = honeyWarehouseNumbers.options,
            )
        }
    }
}


