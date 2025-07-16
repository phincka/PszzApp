package com.example.FarmGame.presentation.apiaries

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.FarmGame.R
import com.example.FarmGame.data.model.EquipmentModel
import com.example.FarmGame.data.model.GameItemModel
import com.example.FarmGame.data.model.QuestModel
import com.example.FarmGame.data.model.QuestStatus
import com.example.FarmGame.data.model.UserModel
import com.example.FarmGame.presentation.auth.base.Button
import com.example.FarmGame.presentation.auth.base.VerticalSpacer
import com.example.FarmGame.presentation.components.TopBar
import com.example.farmgame.presentation.dashboard.BackgroundShapes
import com.example.farmgame.presentation.dashboard.getItemById
import com.example.farmgame.presentation.dashboard.loadUserFromStorage
import com.example.farmgame.presentation.dashboard.saveUserToStorage
import com.example.FarmGame.presentation.main.SnackbarHandler
import com.example.FarmGame.presentation.main.bottomBarPadding
import com.example.FarmGame.ui.theme.AppTheme
import com.example.FarmGame.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun ApiariesScreen(
    viewModel: ApiariesViewModel = koinViewModel(),
    navigator: DestinationsNavigator,
    navController: NavController,
    snackbarHandler: SnackbarHandler,
    message: String? = null,
) {
    val context = LocalContext.current

    var user = remember { mutableStateOf(UserModel("1", "Paweł", 0, 0)) }
    val sharedPreferences =
        remember { context.getSharedPreferences("farming_game", Context.MODE_PRIVATE) }


    LaunchedEffect(Unit) {
        user.value = loadUserFromStorage(sharedPreferences)
    }

    LaunchedEffect(message) {
        launch {
            message?.let {
                snackbarHandler.showSuccessSnackbar(
                    message = it
                )
            }
        }
    }

    ApiariesLayout(
        quests = viewModel.quests.value,
        gameItems = viewModel.gameItems.value,
        equipment = viewModel.equipment.collectAsState(),
        addToEquipment = { viewModel.addToEquipment(it) },
        removeEquipmentItem = { viewModel.removeEquipmentItem(it) },

        questStatuses = viewModel.questStatuses.collectAsState(),
        updateQuestStatus = viewModel::updateQuestStatus,
        clearStatuses = viewModel::clearStatuses,

        user = user,
        sharedPreferences = sharedPreferences,

        navController = navController,
        navigator = navigator,
    )
}

@SuppressLint("RememberReturnType")
@Composable
fun ApiariesLayout(
    quests: List<QuestModel>,
    gameItems: List<GameItemModel>,

    equipment: State<List<EquipmentModel>>,
    addToEquipment: (EquipmentModel) -> Unit,
    removeEquipmentItem: (EquipmentModel) -> Unit,

    questStatuses: State<List<QuestStatus>>,
    updateQuestStatus: (QuestStatus) -> Unit,
    clearStatuses: () -> Unit,

    user: MutableState<UserModel>,
    sharedPreferences: SharedPreferences,

    navController: NavController,
    navigator: DestinationsNavigator,
) {
    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        BackgroundShapes()

        Column {
            androidx.compose.material3.Button(
                onClick = { clearStatuses() }
            ) {
                Text("Clear QUESTS")
            }

            TopBar(
                backNavigation = { navigator.navigateUp() },
                title = "Zadania",
            )

            Log.d("LOG_EQQQ", questStatuses.toString())

            var indexx = 0
            if (questStatuses.value.isNotEmpty()) {
                indexx = questStatuses.value.indexOfFirst { it == QuestStatus.STARTED }
            }

            if (quests.count() >= questStatuses.value.count()) {
                quests[indexx].let { questModel ->
                    Column {
                        Text(
                            text = questModel.title,
                            style = Typography.h5,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.neutral90,
                        )
                        VerticalSpacer(8.dp)

                        Text(
                            text = questModel.description,
                            style = Typography.label,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.neutral90,
                        )
                        VerticalSpacer(8.dp)

                        getItemById(
                            id = questModel.requiredItems.itemId,
                            availableGameItems = gameItems,
                        )?.let { gameItem ->
                            Text(
                                text = "Wymagane zasoby: ${gameItem.name} (${questModel.requiredItems.quantity})",
                                style = Typography.label,
                                color = AppTheme.colors.neutral60,
                            )
                            VerticalSpacer(8.dp)
                        }

                        Text(
                            text = "Nagrody za ukończenie misji:",
                            style = Typography.p,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.neutral90,
                        )

                        questModel.reward?.itemId?.let {
                            getItemById(it, gameItems)?.let {
                                Text(
                                    text = it.name,
                                    style = Typography.label,
                                    color = AppTheme.colors.neutral60,
                                )
                                VerticalSpacer(8.dp)
                            }
                        }


                        questModel.reward?.exp?.let {
                            Text(
                                text = "$it EXP",
                                style = Typography.label,
                                color = AppTheme.colors.neutral60,
                            )
                            VerticalSpacer(8.dp)
                        }

                        questModel.reward?.money?.let {
                            Text(
                                text = "$it $",
                                style = Typography.label,
                                color = AppTheme.colors.neutral60,
                            )
                        }
                    }
                    equipment.value.find {
                        questModel.requiredItems.itemId == it.itemId
                    }
                        ?.let { equipmentItem ->
                            if (equipmentItem.quantity < questModel.requiredItems.quantity) {
                                getItemById(
                                    questModel.requiredItems.itemId,
                                    gameItems
                                )?.let { gameItem ->
                                    Text(
                                        text = "Brakuje: ${questModel.requiredItems.quantity - equipmentItem.quantity} ${gameItem.name}",
                                        style = Typography.label,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppTheme.colors.red,
                                    )
                                    VerticalSpacer(8.dp)
                                }
                            } else {
                                Text(
                                    text = "Masz wystarczająco zasobów",
                                    style = Typography.label,
                                    color = AppTheme.colors.neutral60,
                                )

                                Button(
                                    text = "Ukończ zadanie!",
                                    onClick = {
                                        val backStackEntry =
                                            navController.getBackStackEntry("dashboard_screen")
                                        backStackEntry.savedStateHandle["refresh"] = true

                                        updateQuestStatus(QuestStatus.STARTED)

                                        questModel.reward?.exp?.let { exp ->
                                            user.value.exp += exp

                                            saveUserToStorage(sharedPreferences, user.value)
                                        }

                                        questModel.reward?.let { reward ->
                                            reward.itemId?.let { itemId ->
                                                addToEquipment(EquipmentModel(itemId, reward.quantity))
                                            }
                                        }

                                        removeEquipmentItem(questModel.requiredItems)
                                    },
                                )
                            }

                        }

                    VerticalSpacer(32.dp)

                }
            } else {
                Text(
                    text = "Wszystkie misje zostały zrobione",
                )
            }

        }
    }
}

@Composable
fun EmptyList(
    title: String,
    text: String,
    buttonTitle: String,
    showIcon: Boolean = true,
    navigate: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(vertical = 64.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showIcon) {
            Image(
                painter = painterResource(R.drawable.empty_list),
                contentDescription = "arrow_right",
                modifier = Modifier
                    .size(164.dp)
            )
        }

        VerticalSpacer(48.dp)

        Text(
            text = title,
            style = Typography.h5,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colors.neutral90,
        )
        VerticalSpacer(8.dp)

        Text(
            text = text,
            style = Typography.label,
            color = AppTheme.colors.neutral60,
        )

        VerticalSpacer(32.dp)


        Button(
            text = buttonTitle,
            onClick = navigate,
        )
    }
}