package com.example.pszzapp.presentation.hive.create

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.presentation.apiary.create.InputDate
import com.example.pszzapp.presentation.apiary.create.InputSelect
import com.example.pszzapp.presentation.apiary.create.InputText
import com.example.pszzapp.presentation.auth.base.Button
import com.example.pszzapp.presentation.components.*
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.destinations.CreateHiveStep2ScreenDestination
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun CreateHiveStep1Screen(
    apiaryId: String,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    viewModel: CreateHiveViewModel = koinViewModel(),
    hiveModel: HiveModel? = null,
) {
    val createHiveState by viewModel.createHiveState.collectAsState()

    CreateHiveLayout(
        navController = navController,
        resultNavigator = resultNavigator,
        createHiveState = createHiveState,
        apiaryId = apiaryId,
        onHiveCreated = { hiveData ->
            navigator.navigate(CreateHiveStep2ScreenDestination(
                hiveData = hiveData,
                isEditing = hiveModel != null,
            ))
        },
        hiveModel = hiveModel,
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun CreateHiveLayout(
    apiaryId: String,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    createHiveState: CreateHiveState,
    onHiveCreated: (HiveModel) -> Unit,
    hiveModel: HiveModel? = null,
) {
    val hiveCreatedDateState = rememberMaterialDialogState()

    var familyTypeOptions by rememberOptionsState(CreateHiveConstants.familyType)
    var hiveTypeOptions by rememberOptionsState(CreateHiveConstants.hiveType)

    var hiveData by remember { mutableStateOf(HiveModel()) }

    LaunchedEffect(hiveModel) {
        hiveModel?.let {
            hiveData = hiveModel
        }
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

            StepsBelt(maxSteps = 3, currentStep = 1)

            CreateHiveForm(
                hiveData = hiveData,
                familyTypeOptions = familyTypeOptions,
                hiveTypeOptions = hiveTypeOptions,
                onHiveDataChange = { hiveData = it },
                onFamilyTypeChange = { familyTypeOptions = it },
                onHiveTypeChange = { hiveTypeOptions = it },
                onDateClick = { hiveCreatedDateState.show() }
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
                        hiveData = hiveData.copy(
                            apiaryId = apiaryId,
                            familyType = familyTypeOptions.selectedOption,
                            hiveType = hiveTypeOptions.selectedOption
                        )
                        onHiveCreated(hiveData)
                    }
                )
            }

            DatePicker(
                pickedDate = hiveData.hiveCreatedDate,
                setPickedDate = { hiveData = hiveData.copy(hiveCreatedDate = it) },
                dateDialogState = hiveCreatedDateState
            )
        }

        OptionsModal(
            optionsState = familyTypeOptions,
            onOptionSelected = { familyTypeOptions = it }
        )

        OptionsModal(
            optionsState = hiveTypeOptions,
            onOptionSelected = { hiveTypeOptions = it }
        )
    }
}

@Composable
private fun CreateHiveForm(
    hiveData: HiveModel,
    familyTypeOptions: OptionsState,
    hiveTypeOptions: OptionsState,
    onHiveDataChange: (HiveModel) -> Unit,
    onFamilyTypeChange: (OptionsState) -> Unit,
    onHiveTypeChange: (OptionsState) -> Unit,
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
        InputText(
            label = stringResource(R.string.hive_name),
            placeholder = "np. 01",
            value = hiveData.name,
            onValueChange = { onHiveDataChange(hiveData.copy(name = it)) }
        )

        InputSelect(
            value = familyTypeOptions.getCurrentOption(),
            label = stringResource(R.string.family_type),
            showPlaceholder = !familyTypeOptions.changed,
            selectedOption = familyTypeOptions.selectedOption,
            setExpanded = { onFamilyTypeChange(familyTypeOptions.copy(expanded = it)) },
            options = familyTypeOptions.options
        )

        InputSelect(
            value = hiveTypeOptions.getCurrentOption(),
            label = stringResource(R.string.hive_type),
            showPlaceholder = !hiveTypeOptions.changed,
            selectedOption = hiveTypeOptions.selectedOption,
            setExpanded = { onHiveTypeChange(hiveTypeOptions.copy(expanded = it)) },
            options = hiveTypeOptions.options
        )

        hiveData.hiveCreatedDate?.let {
            InputDate(
                value = it.toFormattedDate(),
                label = stringResource(R.string.created_date),
                setExpanded = onDateClick
            )
        }
    }
}

@Composable
fun rememberOptionsState(options: List<Int>): MutableState<OptionsState> {
    return remember {
        mutableStateOf(
            OptionsState(
                expanded = false,
                selectedOption = 0,
                changed = false,
                options = options
            )
        )
    }
}

data class OptionsState(
    val expanded: Boolean,
    val selectedOption: Int,
    val changed: Boolean,
    val options: List<Int>,
) {
    @Composable
    fun getCurrentOption(): String {
        return if (changed) stringResource(options[selectedOption]) else ""
    }
}

fun LocalDate.toFormattedDate(): String {
    return if (this != LocalDate.now()) formattedDate(this) else ""
}

@Composable
fun OptionsModal(
    optionsState: OptionsState,
    onOptionSelected: (OptionsState) -> Unit
) {
    Modal(
        options = optionsState.options,
        isModalActive = optionsState.expanded,
        onConfirmation = { selectedOption ->
            onOptionSelected(
                optionsState.copy(
                    expanded = false,
                    selectedOption = selectedOption,
                    changed = true
                )
            )
        },
        onDismissRequest = {
            onOptionSelected(optionsState.copy(expanded = false))
        }
    )
}


@Composable
@SuppressLint("Range")
fun StepsBelt(
    maxSteps: Int = 2,
    currentStep: Int = 1,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .graphicsLayer {
                    shadowElevation = 4.dp.toPx()
                    shape = RoundedCornerShape(8.dp)
                    clip = true
                }
                .clip(RoundedCornerShape(8.dp))
                .background(AppTheme.colors.white)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Krok ${currentStep}/${maxSteps}",
                style = Typography.label,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.neutral90,
            )

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppTheme.colors.primary20)
                    .fillMaxWidth()
                    .height(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppTheme.colors.primary50)
                        .fillMaxWidth(currentStep.toFloat() / maxSteps.toFloat())
                        .fillMaxHeight()
                )

            }
        }
    }
}

object CreateHiveConstants {
    val familyType = listOf(
        R.string.hive_family_type_1,
        R.string.hive_family_type_2,
        R.string.hive_family_type_3,
    )
    val hiveType = listOf(
        R.string.hive_hive_type_1,
        R.string.hive_hive_type_2,
        R.string.hive_hive_type_3,
    )
    val breed = listOf(
        R.string.hive_breed_1,
        R.string.hive_breed_2,
    )
    val state = listOf(
        R.string.hive_breed_1,
        R.string.hive_breed_2,
    )
    val queenYear = listOf(
        R.string.hive_queen_year_1,
        R.string.hive_queen_year_2,
        R.string.hive_queen_year_3,
        R.string.hive_queen_year_4,
        R.string.hive_queen_year_5,
    )
}