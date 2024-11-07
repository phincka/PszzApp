package com.example.pszzapp.presentation.apiary.create

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
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.presentation.auth.base.Button
import com.example.pszzapp.presentation.components.Modal
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.destinations.ApiaryScreenDestination
import com.example.pszzapp.presentation.hive.create.StepsBelt
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun CreateApiaryLocationScreen(
    apiaryData: ApiaryModel,
    isEditing: Boolean = false,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    viewModel: CreateApiaryViewModel = koinViewModel(),
) {
    val createApiaryState = viewModel.createApiaryState.collectAsState().value

    if (createApiaryState is CreateApiaryState.Redirect) navigator.navigate(
        ApiaryScreenDestination(
            id = createApiaryState.apiaryId
        )
    )

    CreateApiaryLocationLayout(
        isEditing = isEditing,
        navController = navController,
        resultNavigator = resultNavigator,
        createApiaryState = createApiaryState,
        apiaryData = apiaryData,
        createApiary = viewModel::createApiary,
        editApiary = viewModel::editApiary,
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun CreateApiaryLocationLayout(
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    isEditing: Boolean = false,
    createApiaryState: CreateApiaryState,
    apiaryData: ApiaryModel,
    createApiary: (ApiaryModel) -> Unit,
    editApiary: (ApiaryModel) -> Unit,
) {
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
                .verticalScroll(rememberScrollState()),
        ) {
            TopBar(
                backNavigation = { resultNavigator.navigateBack() },
                title = if (isEditing) "Edytuj pasiekę" else "Dodaj pasiekę",
            )

            StepsBelt(
                maxSteps = 2,
                currentStep = 2
            )

            var apiaryModel: ApiaryModel by remember {
                mutableStateOf(
                    ApiaryModel(
                        id = apiaryData.id,
                        uid = apiaryData.uid,
                        name = apiaryData.name,
                        type = apiaryData.type,
                        location = apiaryData.location,
                    )
                )
            }

            Column {
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
                        label = stringResource(R.string.apiary_form_location_label),
                        placeholder = stringResource(R.string.apiary_form_location_label),
                        value = apiaryModel.location,
                        onValueChange = { newValue ->
                            apiaryModel = apiaryModel.copy(location = newValue)
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
            ) {
                if (createApiaryState is CreateApiaryState.Error) TextError(createApiaryState.message)

                Button(
                    text = "Zapisz",
                    showIcon = false,
                    onClick = { if (isEditing) editApiary(apiaryModel) else createApiary(apiaryModel) },
                    isLoading = createApiaryState is CreateApiaryState.Loading
                )
            }

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