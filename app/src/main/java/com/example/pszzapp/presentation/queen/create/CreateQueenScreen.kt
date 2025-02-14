package com.example.pszzapp.presentation.queen.create

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
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.data.model.QueenModel
import com.example.pszzapp.presentation.apiary.create.InputDate
import com.example.pszzapp.presentation.apiary.create.InputSelect
import com.example.pszzapp.presentation.apiary.create.InputText
import com.example.pszzapp.presentation.auth.base.Button
import com.example.pszzapp.presentation.components.DatePicker
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.destinations.ApiaryScreenDestination
import com.example.pszzapp.presentation.hive.create.CreateHiveConstants
import com.example.pszzapp.presentation.hive.create.OptionsModal
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

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun CreateQueenScreen(
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    viewModel: CreateQueenViewModel = koinViewModel(),
    queenModel: QueenModel? = null,
) {
    val createQueenState by viewModel.createQueenState.collectAsState()

    if (createQueenState is CreateQueenState.Redirect) {
//        navigator.navigate(
//            ApiaryScreenDestination(id = (createQueenState as CreateQueenState.Redirect).apiaryId)
//        )
    }

    CreateQueenLayout(
        navController = navController,
        resultNavigator = resultNavigator,
        navigator = navigator,
        createQueenState = createQueenState,
        queenModel = queenModel,
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun CreateQueenLayout(
    queenModel: QueenModel? = null,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    createQueenState: CreateQueenState
) {
    var queenData by remember(queenModel) { mutableStateOf(queenModel ?: QueenModel()) }
    val isEditing = queenModel != null

    val queenAddedDateState = rememberMaterialDialogState()

    var breedOptions by rememberOptionsState(
        options = CreateHiveConstants.breed,
        selectedOption = queenData.breed,
        changed = isEditing,
    )
    var queenYearTypeOptions by rememberOptionsState(
        options = CreateHiveConstants.queenYear,
        selectedOption = queenData.queenYear,
        changed = isEditing,
    )
    var stateTypeOptions by rememberOptionsState(
        options = CreateHiveConstants.state,
        selectedOption = queenData.state,
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
                title = if (isEditing) "Edytuj matkę" else "Zmień matkę",
            )

            CreateQueenLayoutForm(
                queenData = queenData,
                breedOptions = breedOptions,
                queenYearTypeOptions = queenYearTypeOptions,
                stateTypeOptions = stateTypeOptions,
                onQueenDataChange = { queenData = it },
                onBreedOptionsChange = { breedOptions = it },
                onQueenYearOptionsChange = { queenYearTypeOptions = it },
                onStateOptionsChange = { stateTypeOptions = it },
                onDateClick = { queenAddedDateState.show() }
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
            ) {
                if (createQueenState is CreateQueenState.Error) {
                    TextError(createQueenState.message)
                }

                Button(
                    text = stringResource(R.string.next),
                    showIcon = true,
                    onClick = {
                        queenData = queenData.copy(
                            breed = breedOptions.selectedOption,
                            queenYear = queenYearTypeOptions.selectedOption,
                            state = stateTypeOptions.selectedOption
                        )
                    },
                )
            }

            DatePicker(
                pickedDate = queenData.queenAddedDate,
                setPickedDate = { queenData = queenData.copy(queenAddedDate = it) },
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
private fun CreateQueenLayoutForm(
    queenData: QueenModel,
    breedOptions: OptionsState,
    queenYearTypeOptions: OptionsState,
    stateTypeOptions: OptionsState,
    onQueenDataChange: (QueenModel) -> Unit,
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
            value = queenData.line,
            onValueChange = { onQueenDataChange(queenData.copy(line = it)) }
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

        queenData.queenAddedDate?.let {
            InputDate(
                value = it.toFormattedDate(),
                label = stringResource(R.string.created_date),
                setExpanded = onDateClick
            )
        }
    }
}