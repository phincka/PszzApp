package com.example.pszzapp.presentation.apiaries.create

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.presentation.components.ExposedDropdown
import com.example.pszzapp.presentation.components.FilledButton
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.destinations.ApiaryScreenDestination
import com.example.pszzapp.presentation.hive.create.DataConstants
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun CreateApiaryScreen(
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    viewModel: CreateApiaryViewModel = koinViewModel()
) {
    val createApiaryState = viewModel.createApiaryState.collectAsState().value

    CreateApiaryLayout(
        navController = navController,
        resultNavigator = resultNavigator,
        navigator = navigator,
        viewModel = viewModel,
        createApiaryState = createApiaryState
    )
}

@Composable
fun CreateApiaryLayout(
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    viewModel: CreateApiaryViewModel,
    createApiaryState: CreateApiaryState
) {
    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        Column {
            TopBar(
                backNavigation = { resultNavigator.navigateBack() },
                title = "Dodaj pasiekę",
                content = { }
            )

            when (createApiaryState) {
                is CreateApiaryState.Loading -> LoadingDialog(stringResource(R.string.home_loading))

                is CreateApiaryState.Success -> CreateApiaryForm(navigator, viewModel)

                is CreateApiaryState.Redirect -> navigator.navigate(ApiaryScreenDestination(id = createApiaryState.apiaryId))

                is CreateApiaryState.Error -> TextError(createApiaryState.message)
            }
        }
    }
}

@Composable
fun CreateApiaryForm(
    navigator: DestinationsNavigator,
    viewModel: CreateApiaryViewModel
) {
    var apiaryData: ApiaryModel by remember {
        mutableStateOf(
            ApiaryModel(
                "",
                "",
                0
            )
        )
    }

    var type by remember { mutableIntStateOf(apiaryData.type) }
    var typeExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = apiaryData.name,
            onValueChange = { newValue ->
                apiaryData = apiaryData.copy(name = newValue)
            },
            label = {
                Text(stringResource(R.string.create_hive_form_name))
            },
            keyboardActions = KeyboardActions(
                onDone = {}
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )

        ExposedDropdown(
            expanded = typeExpanded,
            setExpanded = { typeExpanded = it },
            options = DataConstants.type,
            selected = apiaryData.type,
            setSelected = {
                apiaryData = apiaryData.copy(type = it)
            },
            label = stringResource(R.string.create_hive_form_type)
        )

        FilledButton(
            text = "Dodaj pasiekę",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            onClick = { viewModel.createApiary(apiaryData) },
        )
    }
}

object DataConstants {
    val type = listOf(
        R.string.create_hive_form_queen_breed,
        R.string.create_hive_form_family_type,
    )
}