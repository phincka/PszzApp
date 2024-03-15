package com.example.pszzapp.presentation.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
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
import com.example.pszzapp.data.util.AuthState
import com.example.pszzapp.presentation.destinations.SignInScreenDestination
import com.example.pszzapp.presentation.destinations.TestScreenDestination
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.destinations.ApiariesScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
  navigator: DestinationsNavigator,
  homeViewModel: MainViewModel = koinViewModel()
) {
  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

  val mainState = homeViewModel.mainState.collectAsState().value
  val signOutState = homeViewModel.signOutState.collectAsState()

  val user = homeViewModel.user.value

  if (user == null) {
    navigator.navigate(SignInScreenDestination)
  }
//  else {
//    if (!user.isEmailVerified) navigator.navigate(VerifyEmailScreenDestination)
//  }

  when(signOutState.value) {
    is AuthState.Loading -> LoadingDialog(stringResource(R.string.home_loading))

    is AuthState.Success -> {
      val success = (signOutState.value as AuthState.Success).success
      if (success) navigator.navigate(SignInScreenDestination)
    }

    is AuthState.Error ->  {
      val errorMessage = (signOutState.value as AuthState.Error).error
      Text(errorMessage)
    }
  }

  Scaffold(
    topBar = {
      TopBar(
        scrollBehavior = scrollBehavior,
        title = stringResource(R.string.home_top_bar_title),
        content = {
          IconButton(
            onClick = {
              homeViewModel.signOut()
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
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = {
          navigator.navigate(ApiariesScreenDestination)
        },
        text = { Text("Pasieki") },
        icon = {
          Icon(
            Icons.Filled.Add,
            contentDescription = null
          )
        },
      )
    },
    bottomBar = {
      var selectedItem by remember { mutableIntStateOf(0) }
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
    Column(modifier = Modifier
      .fillMaxSize()
      .padding(innerPadding)
    ) {
      when (mainState) {
        is MainState.Success -> {
          val hives = mainState.hives
//          HivesLazyColumn(hives, navigator)
        }
        is MainState.Error -> {
          val errorMessage = mainState.message
          TextError(errorMessage)
        }
        is MainState.Loading -> LoadingDialog(stringResource(R.string.home_loading))
      }
    }
  }
}
