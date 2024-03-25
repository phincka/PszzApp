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
import com.example.pszzapp.presentation.apiaries.create.DataConstants
import com.example.pszzapp.presentation.components.ExposedDropdown
import com.example.pszzapp.presentation.components.FilledButton
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun CreateHiveScreen(
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController
) {
    CreateHiveLayout(
        navController = navController,
        resultNavigator = resultNavigator,
    )
}

@Composable
fun CreateHiveLayout(
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
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

            CreateHiveForm()
        }
    }
}

@Composable
fun CreateHiveForm() {
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
        modifier = Modifier
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextField(
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
            setSelected = { type = it },
            selected = apiaryData.type,
            label = "Typ rodziny"
        )

        ExposedDropdown(
            expanded = typeExpanded,
            setExpanded = { typeExpanded = it },
            options = DataConstants.type,
            setSelected = { type = it },
            selected = apiaryData.type,
            label = "Rodzaj ula"
        )

        ExposedDropdown(
            expanded = typeExpanded,
            setExpanded = { typeExpanded = it },
            options = DataConstants.type,
            setSelected = { type = it },
            selected = apiaryData.type,
            label = "Data stworzenia ula"
        )

        ExposedDropdown(
            expanded = typeExpanded,
            setExpanded = { typeExpanded = it },
            options = DataConstants.type,
            setSelected = { type = it },
            selected = apiaryData.type,
            label = "Rasa"
        )

        ExposedDropdown(
            expanded = typeExpanded,
            setExpanded = { typeExpanded = it },
            options = DataConstants.type,
            setSelected = { type = it },
            selected = apiaryData.type,
            label = "Linia"
        )

        ExposedDropdown(
            expanded = typeExpanded,
            setExpanded = { typeExpanded = it },
            options = DataConstants.type,
            setSelected = { type = it },
            selected = apiaryData.type,
            label = "Oznaczenie matki"
        )

        ExposedDropdown(
            expanded = typeExpanded,
            setExpanded = { typeExpanded = it },
            options = DataConstants.type,
            setSelected = { type = it },
            selected = apiaryData.type,
            label = "Data poddania matki"
        )

        ExposedDropdown(
            expanded = typeExpanded,
            setExpanded = { typeExpanded = it },
            options = DataConstants.type,
            setSelected = { type = it },
            selected = apiaryData.type,
            label = "Notatka"
        )


        FilledButton(
            text = "Dodaj ul",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            onClick = { },
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