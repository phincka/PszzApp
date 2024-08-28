package com.example.pszzapp.presentation.hive.create

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.presentation.apiary.create.InputDate
import com.example.pszzapp.presentation.apiary.create.InputSelect
import com.example.pszzapp.presentation.apiary.create.InputText
import com.example.pszzapp.presentation.auth.base.Button
import com.example.pszzapp.presentation.components.DatePicker
import com.example.pszzapp.presentation.components.Modal
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.components.formattedDate
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.destinations.CreateHiveStep3ScreenDestination
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@Destination
@Composable
fun CreateHiveStep2Screen(
    hiveData: HiveModel,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    viewModel: CreateHiveViewModel = koinViewModel(),
) {
    val createHiveState by viewModel.createHiveState.collectAsState()

    CreateHiveStep2Layout(
        hiveData = hiveData,
        navController = navController,
        resultNavigator = resultNavigator,
        navigator = navigator,
        createHiveState = createHiveState
    )
}

@Composable
private fun CreateHiveStep2Layout(
    hiveData: HiveModel,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    createHiveState: CreateHiveState
) {
    val queenAddedDateState = rememberMaterialDialogState()

    var breedOptions by rememberOptionsState(CreateHiveConstants.breed)
    var queenYearTypeOptions by rememberOptionsState(CreateHiveConstants.queenYear)
    var stateTypeOptions by rememberOptionsState(CreateHiveConstants.state)

    var hiveDataStep2 by remember {
        mutableStateOf(
            hiveData.copy(
                breed = 0,
                line = "",
                state = 0,
                queenYear = 0,
                queenAddedDate = LocalDate.now(),
                queenNote = ""
            )
        )
    }

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
                title = stringResource(R.string.create_hive),
            )

            StepsBelt(maxSteps = 3, currentStep = 2)

            CreateHiveForm(
                hiveData = hiveDataStep2,
                breedOptions = breedOptions,
                queenYearTypeOptions = queenYearTypeOptions,
                stateTypeOptions = stateTypeOptions,
                onHiveDataChange = { hiveDataStep2 = it },
                onBreedOptionsChange = { breedOptions = it },
                onQueenYearOptionsChange = { queenYearTypeOptions = it },
                onStateOptionsChange = { stateTypeOptions = it },
                onDateClick = { queenAddedDateState.show() }
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
            ) {
                if (createHiveState is CreateHiveState.Error) {
                    TextError(createHiveState.message)
                }

                Button(
                    text = stringResource(R.string.next),
                    showIcon = true,
                    onClick = {
                        hiveDataStep2 = hiveDataStep2.copy(
                            breed = breedOptions.selectedOption,
                            queenYear = queenYearTypeOptions.selectedOption,
                            state = stateTypeOptions.selectedOption
                        )
                        navigator.navigate(
                            CreateHiveStep3ScreenDestination(hiveData = hiveDataStep2)
                        )
                    },
                )
            }

            DatePicker(
                pickedDate = hiveDataStep2.queenAddedDate,
                setPickedDate = { hiveDataStep2 = hiveDataStep2.copy(queenAddedDate = it) },
                dateDialogState = queenAddedDateState
            )
        }

        OptionsModal(
            optionsState = breedOptions,
            onOptionSelected = { breedOptions = it }
        )

        OptionsModal(
            optionsState = queenYearTypeOptions,
            onOptionSelected = { queenYearTypeOptions = it }
        )

        OptionsModal(
            optionsState = stateTypeOptions,
            onOptionSelected = { stateTypeOptions = it }
        )
    }
}

@Composable
private fun CreateHiveForm(
    hiveData: HiveModel,
    breedOptions: OptionsState,
    queenYearTypeOptions: OptionsState,
    stateTypeOptions: OptionsState,
    onHiveDataChange: (HiveModel) -> Unit,
    onBreedOptionsChange: (OptionsState) -> Unit,
    onQueenYearOptionsChange: (OptionsState) -> Unit,
    onStateOptionsChange: (OptionsState) -> Unit,
    onDateClick: () -> Unit
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
        InputSelect(
            value = breedOptions.getCurrentOption(),
            label = stringResource(R.string.queen_breed),
            showPlaceholder = !breedOptions.changed,
            selectedOption = breedOptions.selectedOption,
            setExpanded = { onBreedOptionsChange(breedOptions.copy(expanded = it)) },
            options = breedOptions.options
        )

        InputText(
            label = stringResource(R.string.queen_line),
            placeholder = "np. Nieska",
            value = hiveData.line,
            onValueChange = { onHiveDataChange(hiveData.copy(line = it)) }
        )

        InputSelect(
            value = queenYearTypeOptions.getCurrentOption(),
            label = stringResource(R.string.queen_year),
            showPlaceholder = !queenYearTypeOptions.changed,
            selectedOption = queenYearTypeOptions.selectedOption,
            setExpanded = { onQueenYearOptionsChange(queenYearTypeOptions.copy(expanded = it)) },
            options = queenYearTypeOptions.options
        )

        InputSelect(
            value = stateTypeOptions.getCurrentOption(),
            label = stringResource(R.string.queen_state),
            showPlaceholder = !stateTypeOptions.changed,
            selectedOption = stateTypeOptions.selectedOption,
            setExpanded = { onStateOptionsChange(stateTypeOptions.copy(expanded = it)) },
            options = stateTypeOptions.options
        )

        InputDate(
            value = hiveData.queenAddedDate.toFormattedDate(),
            label = stringResource(R.string.created_date),
            setExpanded = onDateClick
        )
    }
}
