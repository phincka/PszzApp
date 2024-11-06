package com.example.pszzapp.presentation.apiary.create

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.presentation.auth.base.Button
import com.example.pszzapp.presentation.auth.base.VerticalSpacer
import com.example.pszzapp.presentation.components.Modal
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.components.formattedDate
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.destinations.ApiaryScreenDestination
import com.example.pszzapp.presentation.destinations.CreateApiaryLocationScreenDestination
import com.example.pszzapp.presentation.hive.create.StepsBelt
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun CreateApiaryScreen(
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    viewModel: CreateApiaryViewModel = koinViewModel(),
    apiaryModel: ApiaryModel? = null,
) {
    val createApiaryState by viewModel.createApiaryState.collectAsState()

    if (createApiaryState is CreateApiaryState.Redirect) {
        navigator.navigate(
            ApiaryScreenDestination(id = (createApiaryState as CreateApiaryState.Redirect).apiaryId)
        )
    }

    CreateApiaryLayout(
        navController = navController,
        resultNavigator = resultNavigator,
        navigator = navigator,
        createApiaryState = createApiaryState,
        apiaryModel = apiaryModel,
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun CreateApiaryLayout(
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    createApiaryState: CreateApiaryState,
    apiaryModel: ApiaryModel? = null,
) {
    var apiaryData by remember { mutableStateOf(ApiaryModel()) }

    LaunchedEffect(apiaryModel) {
        apiaryModel?.let {
            apiaryData = apiaryModel
        }
    }

    var typeOptions by remember {
        mutableStateOf(
            ApiaryType(
                expanded = false,
                selectedOption = 0,
                changed = false,
                options = CreateApiaryConstants.type,
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
                .verticalScroll(rememberScrollState())
        ) {
            TopBar(
                backNavigation = { resultNavigator.navigateBack() },
                title = if (apiaryModel != null) "Edytuj pasiekę" else "Dodaj pasiekę",
            )

            StepsBelt(maxSteps = 2, currentStep = 1)

            ApiaryForm(
                apiaryData = apiaryData,
                onApiaryDataChange = { apiaryData = it },
                typeOptions = typeOptions,
                onTypeOptionsChange = { typeOptions = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            SaveApiaryButton(
                createApiaryState = createApiaryState,
                onSaveClick = {
                    apiaryData = apiaryData.copy(type = typeOptions.selectedOption)
                    navigator.navigate(
                        CreateApiaryLocationScreenDestination(
                            isEditing = apiaryModel != null,
                            apiaryData = apiaryData
                        )
                    )
                }
            )
        }

        Modal(
            options = typeOptions.options,
            isModalActive = typeOptions.expanded,
            onConfirmation = {
                typeOptions =
                    typeOptions.copy(expanded = false, selectedOption = it, changed = true)
            },
            onDismissRequest = {
                typeOptions = typeOptions.copy(expanded = false)
            },
        )
    }
}

@Composable
private fun ApiaryForm(
    apiaryData: ApiaryModel,
    onApiaryDataChange: (ApiaryModel) -> Unit,
    typeOptions: ApiaryType,
    onTypeOptionsChange: (ApiaryType) -> Unit
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
            label = stringResource(R.string.apiary_form_name_label),
            placeholder = stringResource(R.string.apiary_form_name_label),
            value = apiaryData.name,
            onValueChange = { newValue ->
                onApiaryDataChange(apiaryData.copy(name = newValue))
            },
        )

        InputSelect(
            value = if (typeOptions.changed) stringResource(typeOptions.options[typeOptions.selectedOption]) else "",
            label = stringResource(R.string.apiary_form_type_title),
            showPlaceholder = !typeOptions.changed,
            selectedOption = typeOptions.selectedOption,
            setExpanded = { onTypeOptionsChange(typeOptions.copy(expanded = it)) },
            options = typeOptions.options,
        )
    }
}

@Composable
private fun SaveApiaryButton(
    createApiaryState: CreateApiaryState,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        if (createApiaryState is CreateApiaryState.Error) {
            TextError(createApiaryState.message)
        }

        Button(
            text = "Dalej",
            showIcon = true,
            onClick = onSaveClick,
            isLoading = createApiaryState is CreateApiaryState.Loading
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputText(
    label: String? = null,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    minLines: Int = 1,
    maxLines: Int = 1,
) {
    Column {
        label?.let {
            Text(
                text = label,
                style = Typography.label,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.neutral90,
            )
        }

        VerticalSpacer(6.dp)

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = Typography.label,
                )
            },
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppTheme.colors.primary40,
                unfocusedBorderColor = AppTheme.colors.neutral30,
                focusedPlaceholderColor = AppTheme.colors.neutral30,
                unfocusedPlaceholderColor = AppTheme.colors.neutral30,
            ),
            textStyle = Typography.label.copy(
                fontWeight = FontWeight.Medium,
                color = AppTheme.colors.neutral90,
            ),
            maxLines = maxLines,
            minLines = minLines,
            keyboardActions = KeyboardActions(
                onDone = {}
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputNumber(
    label: String? = null,
    value: String,
    onValueChange: (String) -> Unit,
    minLines: Int = 1,
    maxLines: Int = 1,
) {
    Column {
        label?.let {
            Text(
                text = label,
                style = Typography.label,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.neutral90,
            )
        }

        VerticalSpacer(6.dp)

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = if (value == "0") "" else value,
            placeholder = {
                Text(
                    text = "0",
                    style = Typography.label,
                )
            },
            onValueChange = {
                onValueChange(it.replace(",", "."))
            },
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppTheme.colors.primary40,
                unfocusedBorderColor = AppTheme.colors.neutral30,
                focusedPlaceholderColor = AppTheme.colors.neutral30,
                unfocusedPlaceholderColor = AppTheme.colors.neutral30,
            ),
            textStyle = Typography.label.copy(
                fontWeight = FontWeight.Medium,
                color = AppTheme.colors.neutral90,
            ),
            maxLines = maxLines,
            minLines = minLines,
            keyboardActions = KeyboardActions(
                onDone = {}
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number,
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputSelect(
    value: String,
    label: String? = null,
    showPlaceholder: Boolean = true,
    selectedOption: Int,
    setExpanded: (Boolean) -> Unit,
    options: List<Int>,
) {
    val source = remember { MutableInteractionSource() }
    if (source.collectIsPressedAsState().value) setExpanded(true)

    Column {
        label?.let {
            Text(
                text = label,
                style = Typography.label,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.neutral90,
            )
        }

        VerticalSpacer(6.dp)

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            interactionSource = source,
            value = value,
            onValueChange = { },
            placeholder = {
                if (showPlaceholder) {
                    Text(
                        text = stringResource(options[selectedOption]),
                        style = Typography.label,
                    )
                }
            },
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppTheme.colors.primary40,
                unfocusedBorderColor = AppTheme.colors.neutral30,
                focusedPlaceholderColor = AppTheme.colors.neutral30,
                unfocusedPlaceholderColor = AppTheme.colors.neutral30,
            ),
            textStyle = Typography.label.copy(
                fontWeight = FontWeight.Medium,
                color = AppTheme.colors.neutral90,
            ),
            maxLines = 1,
            keyboardActions = KeyboardActions(
                onDone = {}
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            readOnly = true,
        )
    }
}

@Composable
fun TabsSelect(
    label: String? = null,
    selectedOption: Int,
    options: List<Int>,
    onOptionSelected: (Int) -> Unit,
) {
    val source = remember { MutableInteractionSource() }

    Column {
        label?.let {
            Text(
                text = label,
                style = Typography.label,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.neutral90,
            )
        }

        VerticalSpacer(6.dp)

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEachIndexed { index, option ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .height(34.dp)
                        .clickable(onClick = {
                            onOptionSelected(index)
                        })
                        .then(
                            if (index == selectedOption) {
                                Modifier
                                    .background(AppTheme.colors.primary50)
                            } else {
                                Modifier
                                    .background(AppTheme.colors.primary10)
                            }
                        ),
                ) {
                    Text(
                        text = stringResource(option),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = Typography.small,
                        fontWeight = FontWeight.SemiBold,
                        color = if (index == selectedOption) AppTheme.colors.primary10 else AppTheme.colors.primary30,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputDate(
    value: String,
    label: String? = null,
    setExpanded: () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    if (source.collectIsPressedAsState().value) setExpanded()

    Column {
        label?.let {
            Text(
                text = label,
                style = Typography.label,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.neutral90,
            )
        }

        VerticalSpacer(6.dp)

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            interactionSource = source,
            value = value,
            onValueChange = { },
            placeholder = {
                if (value == "") {
                    Text(
                        text = formattedDate(LocalDate.now()),
                        style = Typography.label,
                    )
                }
            },
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppTheme.colors.primary40,
                unfocusedBorderColor = AppTheme.colors.neutral30,
                focusedPlaceholderColor = AppTheme.colors.neutral30,
                unfocusedPlaceholderColor = AppTheme.colors.neutral30,
            ),
            textStyle = Typography.label.copy(
                fontWeight = FontWeight.Medium,
                color = AppTheme.colors.neutral90,
            ),
            maxLines = 1,
            keyboardActions = KeyboardActions(
                onDone = {}
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            readOnly = true,
        )
    }
}

data class ApiaryType(
    val expanded: Boolean,
    val selectedOption: Int,
    val changed: Boolean,
    val options: List<Int>,
)

object CreateApiaryConstants {
    val type = listOf(
        R.string.apiary_type_1,
        R.string.apiary_type_2,
    )
}
