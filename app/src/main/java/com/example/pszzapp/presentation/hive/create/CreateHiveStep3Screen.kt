package com.example.pszzapp.presentation.hive.create

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
import com.example.pszzapp.presentation.apiary.create.InputText
import com.example.pszzapp.presentation.auth.base.Button
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.destinations.CreateHiveStep1ScreenDestination
import com.example.pszzapp.presentation.destinations.HiveScreenDestination
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StateFlowValueCalledInComposition", "UnrememberedGetBackStackEntry")
@Destination
@Composable
fun CreateHiveStep3Screen(
    isEditing: Boolean = false,
    hiveData: HiveModel,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    viewModel: CreateHiveViewModel = koinViewModel(),
) {
    val createHiveState by viewModel.createHiveState.collectAsState()

    if (createHiveState is CreateHiveState.Redirect) {
        var message: String? = null

        navController.getBackStackEntry("apiary_screen/${(createHiveState as CreateHiveState.Redirect).apiaryId}").savedStateHandle["refresh"] = true

        if (isEditing) {
            navController.getBackStackEntry("hive_screen/${(createHiveState as CreateHiveState.Redirect).hiveId}").savedStateHandle["refresh"] = true
            message = "Gotowe! Aktualizacja przebiegła pomyślnie."
        }

        navigator.navigate(HiveScreenDestination(id = (createHiveState as CreateHiveState.Redirect).hiveId, message = message)) {
            popUpTo(CreateHiveStep1ScreenDestination.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    CreateHiveStep3Layout(
        isEditing = isEditing,
        hiveData = hiveData,
        navController = navController,
        resultNavigator = resultNavigator,
        createHiveState = createHiveState,
        onCreateHive = viewModel::createHive,
        onEditHive = viewModel::editHive
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun CreateHiveStep3Layout(
    isEditing: Boolean = false,
    hiveData: HiveModel,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    createHiveState: CreateHiveState,
    onCreateHive: (HiveModel) -> Unit,
    onEditHive: (HiveModel) -> Unit,
) {
    var hiveDataStep3: HiveModel by remember {
        mutableStateOf(hiveData)
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
                title = if (isEditing) "Edytuj ul" else stringResource(R.string.create_hive),
            )

            StepsBelt(maxSteps = 3, currentStep = 3)

            CreateHiveForm(
                hiveData = hiveDataStep3,
                onHiveDataChange = { hiveDataStep3 = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
            ) {
                if (createHiveState is CreateHiveState.Error) {
                    TextError(createHiveState.message)
                }

                Button(
                    text = stringResource(R.string.save),
                    showIcon = false,
                    onClick = {
                        if (isEditing) onEditHive(hiveDataStep3) else onCreateHive(
                            hiveDataStep3
                        )
                    },
                    isLoading = createHiveState is CreateHiveState.Loading
                )
            }
        }
    }
}

@Composable
private fun CreateHiveForm(
    hiveData: HiveModel,
    onHiveDataChange: (HiveModel) -> Unit
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
            label = stringResource(R.string.queen_note),
            placeholder = stringResource(R.string.queen_note_placeholder),
            value = hiveData.queenNote,
            onValueChange = { newValue ->
                onHiveDataChange(hiveData.copy(queenNote = newValue))
            },
            minLines = 10,
            maxLines = 10,
        )
    }
}