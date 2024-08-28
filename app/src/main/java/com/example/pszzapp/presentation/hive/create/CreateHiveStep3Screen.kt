package com.example.pszzapp.presentation.hive.create

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.ui.unit.min
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.presentation.apiary.create.CreateApiaryState
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
import com.example.pszzapp.presentation.destinations.ApiaryScreenDestination
import com.example.pszzapp.presentation.destinations.HiveScreenDestination
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

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun CreateHiveStep3Screen(
    hiveData: HiveModel,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    viewModel: CreateHiveViewModel = koinViewModel(),
) {
    val createHiveState by viewModel.createHiveState.collectAsState()

    if (createHiveState is CreateHiveState.Redirect) {
        navigator.navigate(HiveScreenDestination(id = (createHiveState as CreateHiveState.Redirect).hiveId))
    }

    CreateHiveStep3Layout(
        hiveData = hiveData,
        navController = navController,
        resultNavigator = resultNavigator,
        navigator = navigator,
        createHiveState = createHiveState,
        onCreateHive = viewModel::createHive
    )
}

@Composable
private fun CreateHiveStep3Layout(
    hiveData: HiveModel,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    createHiveState: CreateHiveState,
    onCreateHive: (HiveModel) -> Unit,
) {
    var hiveDataStep3 by remember { mutableStateOf(hiveData.copy(queenNote = "")) }

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
                title = stringResource(R.string.create_hive),
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
                    onClick = { onCreateHive(hiveDataStep3) },
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