package com.example.pszzapp.presentation.hiveInfo

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.data.model.DetailedHiveInfoModel
import com.example.pszzapp.data.model.toDetailedHiveInfoModel
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.dashboard.BackgroundShapes
import com.example.pszzapp.presentation.main.SnackbarHandler
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.format.DateTimeFormatter

@SuppressLint("StateFlowValueCalledInComposition", "UnrememberedGetBackStackEntry")
@Destination
//@RootNavGraph(start = true)
@Composable
fun HiveInfoScreen(
    hiveId: String,
    message: String? = null,
    resultNavigator: ResultBackNavigator<Boolean>,
    navController: NavController,
    viewModel: HiveInfoViewModel = koinViewModel(parameters = { parametersOf(hiveId) }),
    snackbarHandler: SnackbarHandler,
) {
    val hiveInfoState = viewModel.overviewState.collectAsState().value
    val removeOverviewState = viewModel.removeOverviewState.collectAsState().value
    val backStackEntry = navController.currentBackStackEntry
    val refresh = backStackEntry?.savedStateHandle?.get<Boolean>("refresh")

    LaunchedEffect(removeOverviewState, refresh) {
        launch {
            if (removeOverviewState is RemoveOverview2State.Error) snackbarHandler.showErrorSnackbar(
                message = removeOverviewState.message
            )

            if (refresh == true) viewModel.getHiveInfo(hiveId)
        }
    }

    when (hiveInfoState) {
        is HiveInfoState.Success -> {
            LaunchedEffect(message) {
                launch {
                    message?.let {
                        snackbarHandler.showSuccessSnackbar(
                            message = it
                        )
                    }
                }
            }

            OverviewLayout(
                navController = navController,
                resultNavigator = resultNavigator,
                detailedHiveInfo = hiveInfoState.hiveInfo.toDetailedHiveInfoModel(),
            )
        }

        is HiveInfoState.Loading -> LoadingDialog()
        is HiveInfoState.Error -> TextError(hiveInfoState.message)
    }
}


@Composable
private fun OverviewLayout(
    navController: NavController,
    resultNavigator: ResultBackNavigator<Boolean>,
    detailedHiveInfo: DetailedHiveInfoModel,
) {
    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        BackgroundShapes()

        Column {
            TopBar(
                backNavigation = { resultNavigator.navigateBack() },
                title = "Informacje o rodzinie",
                warningInfo = detailedHiveInfo.warningInfo,
                goodInfo = detailedHiveInfo.goodInfo,
                columnInfo = true,
//                subtitle = "Ostatni przegląd: ${detailedHiveInfo.overviewDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}",
            )

            Log.d("LOG_H", detailedHiveInfo.overviewDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")).toString())
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                detailedHiveInfo.hiveInfoTiles.forEach { tile ->
                    Row(
                        modifier = Modifier
                            .graphicsLayer {
                                shadowElevation = 4.dp.toPx()
                                shape = RoundedCornerShape(8.dp)
                                clip = false
                            }
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                color = AppTheme.colors.white
                            )
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = tile.title,
                                style = Typography.label,
                                fontWeight = FontWeight.SemiBold,
                                color = AppTheme.colors.neutral90,
                            )

                            val filtredItems = tile.overviewItem.filterNot { it.disabled }
                            filtredItems.forEachIndexed { index, item ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item.key,
                                        style = Typography.small,
                                        color = AppTheme.colors.neutral90,
                                    )

                                    item.value?.let {
                                        Text(
                                            text = stringResource(it),
                                            style = Typography.small,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AppTheme.colors.neutral60,
                                        )
                                    }

                                    item.stringValue?.let {
                                        Text(
                                            text = it,
                                            style = Typography.small,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AppTheme.colors.neutral60,
                                        )
                                    }
                                }

                                if (index != filtredItems.lastIndex) {
                                    HorizontalDivider(
                                        color = AppTheme.colors.neutral30
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
