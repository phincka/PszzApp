package com.example.pszzapp.presentation.apiary

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.pszzapp.R
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun ApiariesScreen(
    apiariesViewModel: ApiariesViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<Boolean>
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val apiariesState = apiariesViewModel.apiariesState.collectAsState().value

    Scaffold(
        topBar = {
            TopBar(
                backNavigation = { resultNavigator.navigateBack() },
                scrollBehavior = scrollBehavior,
                title = stringResource(R.string.home_top_bar_title),
                content = {
                    IconButton(
                        onClick = {
                            //              apiariesViewModel.signOut()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null
                        )
                    }
                }
            )
        },

        bottomBar = {
            var selectedItem by remember { mutableIntStateOf(1) }
            val items = listOf("Home", "Pasieki", "Konto")

            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (apiariesState) {
                is ApiariesState.Success -> {
                    val apiaries = apiariesState.apiaries

                    //          HivesLazyColumn(hives, navigator)
                }

                is ApiariesState.Error -> {
                    val errorMessage = apiariesState.message
                    TextError(errorMessage)
                }

                is ApiariesState.Loading -> LoadingDialog(stringResource(R.string.home_loading))
            }
        }
    }
}
