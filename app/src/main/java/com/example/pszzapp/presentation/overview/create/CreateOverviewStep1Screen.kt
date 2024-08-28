package com.example.pszzapp.presentation.overview.create

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.presentation.components.DatePicker
import com.example.pszzapp.presentation.components.ExposedDropdown
import com.example.pszzapp.presentation.components.FilledButton
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.SwitchField
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.components.formattedDate
import com.example.pszzapp.presentation.destinations.OverviewScreenDestination
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
//@RootNavGraph(start = true)
@Composable
fun CreateOverviewStep1Screen(
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    viewModel: CreateOverviewViewModel = koinViewModel(),
    hiveId: String = "M9I1XqC0P2xIqIFpLASb",
    apiaryId: String = "7aEXBqahiZ1CTub01sYu",
) {
    val createOverviewState = viewModel.createOverviewState.collectAsState().value

    CreateOverviewLayout(
        resultNavigator = resultNavigator,
        navController = navController,
        navigator = navigator,
        viewModel = viewModel,
        hiveId = hiveId,
        apiaryId = apiaryId,
        createOverviewState = createOverviewState
    )
}

@Composable
fun CreateOverviewLayout(
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    viewModel: CreateOverviewViewModel,
    hiveId: String,
    apiaryId: String,
    createOverviewState: CreateOverviewState,
) {
    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        Column {
            TopBar(
                backNavigation = { resultNavigator.navigateBack() },
                title = "Dodaj przegląd",
            )

            when (createOverviewState) {
                is CreateOverviewState.Loading -> LoadingDialog()

                is CreateOverviewState.Success -> CreateOverviewForm(
                    viewModel = viewModel,
                    hiveId = hiveId,
                    apiaryId = apiaryId
                )

                is CreateOverviewState.Redirect -> navigator.navigate(OverviewScreenDestination(overviewId = createOverviewState.overviewId))

                is CreateOverviewState.Error -> TextError(createOverviewState.message)
            }
        }
    }
}

@Composable
fun CreateOverviewForm(
    viewModel: CreateOverviewViewModel,
    hiveId: String,
    apiaryId: String
) {
    var overviewData: OverviewModel by remember {
        mutableStateOf(
            OverviewModel(
                id = "",
                uid = "",
                apiaryId = apiaryId,
                hiveId = hiveId,
                strength = 0,
                mood = 0,
                streets = 0,
                beeMaggots = false,
                cell = false,
                cellType = 0,
                waxSheets = 0,
                waxSheetsAdded = 0,
                nestFrames = 0,
                excluder = false,
                feeder = false,
                foodAmount = "",
                insulator = false,
                pollenTrap = false,
                propolisTrap = false,
                honeyWarehouse = false,
                honeyFrames = 0,
                workFrame = false,
                workFrameDate = LocalDate.now(),
                note = "",
                overviewDate = LocalDate.now()
            )
        )
    }

    var strengthExpanded by remember { mutableStateOf(false) }
    var moodExpanded by remember { mutableStateOf(false) }
    var streetsExpanded by remember { mutableStateOf(false) }
    var nestFramesExpanded by remember { mutableStateOf(false) }
    var honeyFramesExpanded by remember { mutableStateOf(false) }
    var cellTypeExpanded by remember { mutableStateOf(false) }

    val workFrameDateState = rememberMaterialDialogState()
    val overviewDateState = rememberMaterialDialogState()

    Column(
        modifier = Modifier
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ExposedDropdown(
            expanded = strengthExpanded,
            setExpanded = { strengthExpanded = it },
            options = OverviewConstants.strengts,
            selected = overviewData.strength,
            setSelected = {
                overviewData = overviewData.copy(strength = it)
            },
            label = "Siła"
        )

        ExposedDropdown(
            expanded = moodExpanded,
            setExpanded = { moodExpanded = it },
            options = OverviewConstants.moods,
            selected = overviewData.mood,
            setSelected = {
                overviewData = overviewData.copy(mood = it)
            },
            label = "Nastrój"
        )

        ExposedDropdown(
            expanded = nestFramesExpanded,
            setExpanded = { nestFramesExpanded = it },
            options = OverviewConstants.numbers,
            selected = overviewData.nestFrames,
            setSelected = {
                overviewData = overviewData.copy(nestFrames = it)
            },
            label = "Liczba ramek gniazdowych"
        )

        ExposedDropdown(
            expanded = streetsExpanded,
            setExpanded = { streetsExpanded = it },
            options = OverviewConstants.numbers,
            selected = overviewData.streets,
            setSelected = {
                overviewData = overviewData.copy(streets = it)
            },
            label = "Uliczek"
        )

        SwitchField(
            label = "Czerw",
            checked = overviewData.beeMaggots,
            setChecked = { overviewData = overviewData.copy(beeMaggots = it) }
        )

        SwitchField(
            label = "Mateczniki - rodzaj",
            checked = overviewData.cell,
            setChecked = { overviewData = overviewData.copy(cell = it) }
        )

        if (overviewData.cell) {
            ExposedDropdown(
                expanded = cellTypeExpanded,
                setExpanded = { cellTypeExpanded = it },
                options = OverviewConstants.cells,
                selected = overviewData.cellType,
                setSelected = {
                    overviewData = overviewData.copy(cellType = it)
                },
                label = "Rodzaj matecznika"
            )
        }

        SwitchField(
            label = "Krata odgrodowa",
            checked = overviewData.excluder,
            setChecked = { overviewData = overviewData.copy(excluder = it) }
        )

        SwitchField(
            label = "Izolator",
            checked = overviewData.insulator,
            setChecked = { overviewData = overviewData.copy(insulator = it) }
        )

        SwitchField(
            label = "Poławiacz pyłku",
            checked = overviewData.pollenTrap,
            setChecked = { overviewData = overviewData.copy(pollenTrap = it) }
        )

        SwitchField(
            label = "Poławiacz propolisu",
            checked = overviewData.propolisTrap,
            setChecked = { overviewData = overviewData.copy(propolisTrap = it) }
        )

        SwitchField(
            label = "Miodnia - ilość ramek",
            checked = overviewData.honeyWarehouse,
            setChecked = { overviewData = overviewData.copy(honeyWarehouse = it) }
        )

        if (overviewData.honeyWarehouse) {
            ExposedDropdown(
                expanded = honeyFramesExpanded,
                setExpanded = { honeyFramesExpanded = it },
                options = OverviewConstants.numbers,
                selected = overviewData.honeyFrames,
                setSelected = {
                    overviewData = overviewData.copy(honeyFrames = it)
                },
                label = "Liczba ramek w miodni"
            )
        }

        SwitchField(
            label = "Podkarmiaczka",
            checked = overviewData.feeder,
            setChecked = { overviewData = overviewData.copy(feeder = it) }
        )

        if (overviewData.feeder) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = overviewData.foodAmount,
                onValueChange = {
                    overviewData = overviewData.copy(foodAmount = it)
                },
                label = {
                    Text("Ilość podanego syropu")
                },
            )
        }

        SwitchField(
            label = "Ramka pracy",
            checked = overviewData.workFrame,
            setChecked = { overviewData = overviewData.copy(workFrame = it) }
        )

        if (overviewData.workFrame) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Data dodania ramki pracy: ${ formattedDate(overviewData.workFrameDate) }")

                Button(
                    onClick = { workFrameDateState.show() },
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Localized description"
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Data przeglądu: ${ formattedDate(overviewData.overviewDate) }")

            Button(
                onClick = { overviewDateState.show() },
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = "Localized description"
                )
            }
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = overviewData.note,
            minLines = 4,
            onValueChange = {
                overviewData = overviewData.copy(note = it)
            },
            label = {
                Text("Notatka")
            },
        )

        FilledButton(
            text = "Dodaj przegląd",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            onClick = {
                viewModel.createOverview(overviewModel = overviewData)
            },
        )
    }

    DatePicker(
        pickedDate = overviewData.workFrameDate,
        setPickedDate = { overviewData = overviewData.copy(workFrameDate = it) },
        dateDialogState = workFrameDateState
    )

    DatePicker(
        pickedDate = overviewData.overviewDate,
        setPickedDate = { overviewData = overviewData.copy(overviewDate = it) },
        dateDialogState = overviewDateState
    )
}


object OverviewConstants {
    val strengts = listOf(
        R.string.overview_strengts_1,
        R.string.overview_strengts_2,
        R.string.overview_strengts_3,
    )
    val moods = listOf(
        R.string.overview_moods_1,
        R.string.overview_moods_2,
        R.string.overview_moods_3,
    )
    val cells = listOf(
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
}


