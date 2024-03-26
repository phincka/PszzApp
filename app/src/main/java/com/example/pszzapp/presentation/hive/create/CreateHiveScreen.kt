package com.example.pszzapp.presentation.hive.create

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.presentation.apiaries.create.CreateApiaryState
import com.example.pszzapp.presentation.apiaries.create.CreateApiaryViewModel
import com.example.pszzapp.presentation.components.FilledButton
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.destinations.ApiaryScreenDestination
import com.example.pszzapp.presentation.destinations.HiveScreenDestination
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun CreateHiveScreen(
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    viewModel: CreateHiveViewModel = koinViewModel(),
    id: String,
) {
    val createHiveState = viewModel.createHiveState.collectAsState().value

    println("---------------")
    println(id)
    println("---------------")

    CreateHiveLayout(
        resultNavigator = resultNavigator,
        navController = navController,
        navigator = navigator,
        viewModel = viewModel,
        apiaryId = id,
        createHiveState = createHiveState
    )
}

@Composable
fun CreateHiveLayout(
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    navigator: DestinationsNavigator,
    viewModel: CreateHiveViewModel,
    apiaryId: String,
    createHiveState: CreateHiveState,
) {
    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        Column {
            TopBar(
                backNavigation = { resultNavigator.navigateBack() },
                title = "Dodaj Ul",
                content = {}
            )

            when (createHiveState) {
                is CreateHiveState.Loading -> LoadingDialog(stringResource(R.string.home_loading))

                is CreateHiveState.Success -> CreateHiveForm(viewModel = viewModel, apiaryId = apiaryId)

                is CreateHiveState.Redirect -> navigator.navigate(HiveScreenDestination(id = createHiveState.hiveId))

                is CreateHiveState.Error -> TextError(createHiveState.message)
            }


        }
    }
}

@Composable
fun CreateHiveForm(viewModel: CreateHiveViewModel, apiaryId: String) {
    var hiveData: HiveModel by remember {
        mutableStateOf(
            HiveModel(
                "",
                "",
                apiaryId
            )
        )
    }

//    var type by remember { mutableIntStateOf(hiveData) }
//    var typeExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = hiveData.name,
            onValueChange = { newValue ->
                hiveData = hiveData.copy(name = newValue)
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

//        ExposedDropdown(
//            expanded = typeExpanded,
//            setExpanded = { typeExpanded = it },
//            options = DataConstants.type,
//            setSelected = { type = it },
//            selected = hiveData.type,
//            label = "Typ rodziny"
//        )

//        ExposedDropdown(
//            expanded = typeExpanded,
//            setExpanded = { typeExpanded = it },
//            options = DataConstants.type,
//            setSelected = { type = it },
//            selected = hiveData.type,
//            label = "Rodzaj ula"
//        )
//
//        ExposedDropdown(
//            expanded = typeExpanded,
//            setExpanded = { typeExpanded = it },
//            options = DataConstants.type,
//            setSelected = { type = it },
//            selected = hiveData.type,
//            label = "Data stworzenia ula"
//        )
//
//        ExposedDropdown(
//            expanded = typeExpanded,
//            setExpanded = { typeExpanded = it },
//            options = DataConstants.type,
//            setSelected = { type = it },
//            selected = hiveData.type,
//            label = "Rasa"
//        )
//
//        ExposedDropdown(
//            expanded = typeExpanded,
//            setExpanded = { typeExpanded = it },
//            options = DataConstants.type,
//            setSelected = { type = it },
//            selected = hiveData.type,
//            label = "Linia"
//        )
//
//        ExposedDropdown(
//            expanded = typeExpanded,
//            setExpanded = { typeExpanded = it },
//            options = DataConstants.type,
//            setSelected = { type = it },
//            selected = hiveData.type,
//            label = "Oznaczenie matki"
//        )
//
//        ExposedDropdown(
//            expanded = typeExpanded,
//            setExpanded = { typeExpanded = it },
//            options = DataConstants.type,
//            setSelected = { type = it },
//            selected = hiveData.type,
//            label = "Data poddania matki"
//        )
//
//        ExposedDropdown(
//            expanded = typeExpanded,
//            setExpanded = { typeExpanded = it },
//            options = DataConstants.type,
//            setSelected = { type = it },
//            selected = hiveData.type,
//            label = "Notatka"
//        )


        FilledButton(
            text = "Dodaj ul",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            onClick = {
                viewModel.createHive(hiveModel = hiveData)
            },
        )
    }
}


//@androidx.compose.ui.tooling.preview.Preview(device = "spec:width=1080px,height=2340px,dpi=440")
//@androidx.compose.runtime.Composable
//private fun Preview() {
//    val currentTimestamp = System.currentTimeMillis()
//    var apiaryData: ApiaryModel by remember {
//        mutableStateOf(
//            ApiaryModel(
//                "",
//                "",
//                0
//            )
//        )
//    }
//
//    var type by remember { mutableIntStateOf(apiaryData.type) }
//    var typeExpanded by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier.background(Color.White)
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            TextField(
//                modifier = Modifier.fillMaxWidth(),
//                value = apiaryData.name,
//                onValueChange = { newValue ->
//                    apiaryData = apiaryData.copy(name = newValue)
//                },
//                label = {
//                    Text(stringResource(R.string.create_hive_form_name))
//                },
//                keyboardActions = KeyboardActions(
//                    onDone = {}
//                ),
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    imeAction = ImeAction.Next
//                )
//            )
//
//            ExposedDropdown(
//                expanded = typeExpanded,
//                setExpanded = { typeExpanded = it },
//                options = DataConstants.type,
//                setSelected = { type = it },
//                selected = apiaryData.type,
//                label = stringResource(R.string.create_hive_form_family_type)
//            )
//        }
//    }
//}


object DataConstants {
    val type = listOf(
        R.string.create_hive_form_queen_breed,
        R.string.create_hive_form_family_type,
    )
}