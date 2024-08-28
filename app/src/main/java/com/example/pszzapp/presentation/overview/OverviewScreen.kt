package com.example.pszzapp.presentation.overview

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.data.util.DropdownMenuItemData
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun OverviewScreen(
    overviewId: String,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    overviewViewModel: OverviewViewModel = koinViewModel(parameters = { parametersOf(overviewId) })
) {
//    TextError(overviewId)
//    val hiveState = hiveViewModel.hiveState.collectAsState().value
    val overviewState = overviewViewModel.overviewState.collectAsState().value

    var isModalActive by remember { mutableStateOf(false) }

    when (overviewState) {
        is OverviewState.Success -> {
            val menuItems = listOf(
                DropdownMenuItemData(
                    icon = Icons.Outlined.Edit,
                    text = "Edytuj przegląd",
                    onClick = { }
                ),
                DropdownMenuItemData(
                    icon = Icons.Outlined.Clear,
                    text = stringResource(R.string.hive_nav_remove_hive),
                    onClick = {
                        isModalActive = true
                    }
                ),
            )

            OverviewLayout(
                navController = navController,
                resultNavigator = resultNavigator,
                menuItems = menuItems,
                isModalActive = isModalActive,
                setModal = { isModalActive = it },
                navigator = navigator,
                overview = overviewState.overview
            )
        }

        is OverviewState.Loading -> LoadingDialog()
        is OverviewState.Error -> TextError(overviewState.message)
    }
}


@Composable
fun OverviewLayout(
    navController: NavController,
    resultNavigator: ResultBackNavigator<Boolean>,
    menuItems: List<DropdownMenuItemData>,
    isModalActive: Boolean,
    setModal: (Boolean) -> Unit,
    navigator: DestinationsNavigator,
    overview: OverviewModel,
) {
    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        Column {
            TopBar(
                backNavigation = { resultNavigator.navigateBack() },
                title = "Przegląd",
                menuItems = menuItems,
                isModalActive = isModalActive,
                setModal = setModal
            )

            OutlinedCard(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                border = BorderStroke(1.dp, Color.LightGray),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Informacje o rodzinie",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
                HorizontalDivider(
                    color = Color.LightGray
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Czerw", fontWeight = FontWeight.SemiBold)
                    Text(text = overview.beeMaggots.toString())
                }
                HorizontalDivider(
                    color = Color.LightGray
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Czerw", fontWeight = FontWeight.SemiBold)
                    Text(text = overview.beeMaggots.toString())
                }
            }
            OutlinedCard(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                border = BorderStroke(1.dp, Color.LightGray),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Tytuł karty",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
                HorizontalDivider(
                    color = Color.LightGray
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Czerw", fontWeight = FontWeight.SemiBold)
                    Text(text = overview.beeMaggots.toString())
                }
                HorizontalDivider(
                    color = Color.LightGray
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Czerw", fontWeight = FontWeight.SemiBold)
                    Text(text = overview.beeMaggots.toString())
                }
            }
            OutlinedCard(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                border = BorderStroke(1.dp, Color.LightGray),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Tytuł karty",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
                HorizontalDivider(
                    color = Color.LightGray
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Czerw", fontWeight = FontWeight.SemiBold)
                    Text(text = overview.beeMaggots.toString())
                }
                HorizontalDivider(
                    color = Color.LightGray
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Czerw", fontWeight = FontWeight.SemiBold)
                    Text(text = overview.beeMaggots.toString())
                }
            }


        }
    }
}