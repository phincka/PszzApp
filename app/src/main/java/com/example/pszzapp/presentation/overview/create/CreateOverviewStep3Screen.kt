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
import com.example.pszzapp.presentation.apiary.create.InputDate
import com.example.pszzapp.presentation.apiary.create.TabsSelect
import com.example.pszzapp.presentation.auth.base.Button
import com.example.pszzapp.presentation.components.DatePicker
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.destinations.CreateOverviewStep1ScreenDestination
import com.example.pszzapp.presentation.destinations.OverviewScreenDestination
import com.example.pszzapp.presentation.hive.create.OptionsState
import com.example.pszzapp.presentation.hive.create.StepsBelt
import com.example.pszzapp.presentation.hive.create.rememberOptionsState
import com.example.pszzapp.presentation.hive.create.toFormattedDate
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StateFlowValueCalledInComposition", "UnrememberedGetBackStackEntry")
@Destination
@Composable
fun CreateOverviewStep3Screen(
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
                if (isEditing) viewModel.editOverview(it) else viewModel.createOverview(it)
            },
            overviewData = overviewData,
        )

        is CreateOverviewState.Redirect -> {
            navController.getBackStackEntry("hive_Screen/${overviewData.hiveId}").savedStateHandle["refresh"] = true

            navigator.navigate(
                OverviewScreenDestination(
                    overviewId = createOverviewState.overviewId
                )
            ) {
                popUpTo(CreateOverviewStep1ScreenDestination.route) { inclusive = true }
            }
        }

        is CreateOverviewState.Error -> TextError(createOverviewState.message)
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun CreateOverviewLayout(
    isEditing: Boolean = false,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    createOverviewState: CreateOverviewState,
    onFormComplete: (OverviewModel) -> Unit,
    overviewData: OverviewModel,
) {
    var overviewDataStep3: OverviewModel by remember {
        mutableStateOf(overviewData)
    }

    var foodAmount by rememberOptionsState(
        options = OverviewConstants.foodAmount,
        selectedOption = overviewDataStep3.foodAmount,
        changed = isEditing,
    )
    var workFrame by rememberOptionsState(
        options = OverviewConstants.workFrame,
        selectedOption = overviewDataStep3.workFrame,
        changed = isEditing,
    )
    val workFrameDateState = rememberMaterialDialogState()
    val overviewDateState = rememberMaterialDialogState()


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

            StepsBelt(maxSteps = 3, currentStep = 3)

            CreateOverviewForm(
                overviewData = overviewDataStep3,
                foodAmount = foodAmount,
                workFrame = workFrame,
                onFoodAmountChange = { foodAmount = it },
                onWorkFrameChange = { workFrame = it },
                onWorkFrameDateClick = { workFrameDateState.show() },
                onOverviewDateClick = { overviewDateState.show() },
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
            ) {
                if (createOverviewState is CreateOverviewState.Error) {
                    TextError(createOverviewState.message)
                }

                Button(
                    text = stringResource(R.string.save),
                    showIcon = true,
                    onClick = {
                        overviewDataStep3 = overviewDataStep3.copy(
                            foodAmount = foodAmount.selectedOption,
                            workFrame = workFrame.selectedOption,
                        )

                        onFormComplete(overviewDataStep3)
                    }
                )
            }

            DatePicker(
                pickedDate = overviewDataStep3.workFrameDate,
                setPickedDate = { overviewDataStep3 = overviewDataStep3.copy(workFrameDate = it) },
                dateDialogState = workFrameDateState
            )

            DatePicker(
                pickedDate = overviewDataStep3.overviewDate,
                setPickedDate = { overviewDataStep3 = overviewDataStep3.copy(overviewDate = it) },
                dateDialogState = overviewDateState
            )
        }
    }
}


@Composable
private fun CreateOverviewForm(
    overviewData: OverviewModel,
    foodAmount: OptionsState,
    workFrame: OptionsState,
    onFoodAmountChange: (OptionsState) -> Unit,
    onWorkFrameChange: (OptionsState) -> Unit,
    onWorkFrameDateClick: () -> Unit,
    onOverviewDateClick: () -> Unit,
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
            label = "Ilość pokarmu",
            selectedOption = foodAmount.selectedOption,
            onOptionSelected = { onFoodAmountChange(foodAmount.copy(selectedOption = it)) },
            options = foodAmount.options
        )
        TabsSelect(
            label = "Ramka pracy",
            selectedOption = workFrame.selectedOption,
            onOptionSelected = { onWorkFrameChange(workFrame.copy(selectedOption = it)) },
            options = workFrame.options
        )
        if (workFrame.selectedOption == 1) {
            InputDate(
                value = overviewData.workFrameDate?.toFormattedDate().toString(),
                label = "Data wymiany ramki pracy",
                setExpanded = onWorkFrameDateClick,
            )
        }

        InputDate(
            value = overviewData.overviewDate?.toFormattedDate().toString(),
            label = "Data przeglądu",
            setExpanded = onOverviewDateClick,
        )
    }
}



