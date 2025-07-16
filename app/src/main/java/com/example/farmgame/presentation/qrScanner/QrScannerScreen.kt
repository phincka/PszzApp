package com.example.FarmGame.presentation.qrScanner

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.FarmGame.data.model.EquipmentModel
import com.example.FarmGame.data.model.ItemType
import com.example.FarmGame.data.model.UserModel
import com.example.FarmGame.presentation.auth.base.VerticalSpacer
import com.example.FarmGame.presentation.components.TopBar
import com.example.farmgame.presentation.dashboard.getItemById
import com.example.farmgame.presentation.dashboard.loadUserFromStorage
import com.example.farmgame.presentation.dashboard.saveUserToStorage
import com.example.FarmGame.presentation.main.bottomBarPadding
import com.example.FarmGame.ui.theme.AppTheme
import com.example.FarmGame.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StateFlowValueCalledInComposition", "PermissionLaunchedDuringComposition")
@Destination
@Composable
fun QrScannerScreen(
    navigator: DestinationsNavigator,
    viewModel: QrScannerViewModel = koinViewModel(),
    navController: NavController,
) {
    val equipment = viewModel.equipment.collectAsState().value

    var hiveId by remember { mutableStateOf("") }


    LaunchedEffect(key1 = hiveId) {
        launch {
            delay(10000L)
            hiveId = ""
        }
    }
    val context = LocalContext.current


    val gameItems by viewModel.gameItems.collectAsState()

    var user = remember { mutableStateOf(UserModel("1", "Paweł", 0, 0)) }

    val sharedPreferences =
        remember { context.getSharedPreferences("farming_game", Context.MODE_PRIVATE) }

    LaunchedEffect(Unit) {
        user.value = loadUserFromStorage(sharedPreferences)
    }

    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            TopBar(
                backNavigation = { navigator.navigateUp() },
                title = "Sklep",
            )

            Column {
                Text("User: ${user.value.name}")
                Text("Exp: ${user.value.exp}")
                Text("Money: ${user.value.money}")
            }

            Column {
                Text(
                    text = "Sprzedaj",
                    style = Typography.h3,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.neutral90,
                )
                VerticalSpacer(32.dp)
                equipment.forEach { equipmentModel ->
                    getItemById(
                        id = equipmentModel.itemId,
                        gameItems
                    )?.let { gameItem ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row {
                                Text(
                                    text = gameItem.name,
                                    style = Typography.h5,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppTheme.colors.neutral90,
                                )
                                Text(
                                    text = "x${equipmentModel.quantity}",
                                    style = Typography.p,
                                    color = AppTheme.colors.neutral90,
                                )
                                Text(
                                    text = "${gameItem.price} $",
                                    style = Typography.h5,
                                    color = AppTheme.colors.neutral90,
                                )
                            }

                            Button(
                                onClick = {
                                    val backStackEntry =
                                        navController.getBackStackEntry("dashboard_screen")
                                    backStackEntry.savedStateHandle["refresh"] = true

                                    user.value.money += gameItem.price

                                    viewModel.removeEquipmentItem(equipmentModel.copy(quantity = 1))
                                    saveUserToStorage(sharedPreferences, user.value)
                                },
                            ) {
                                Text("Sprzedaj")
                            }
                        }
                    }
                }
            }
            VerticalSpacer(64.dp)
            Column {
                Text(
                    text = "Kup",
                    style = Typography.h3,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.neutral90,
                )
                VerticalSpacer(32.dp)
                gameItems.filter { it.itemType == ItemType.SEED }.forEach { gameItem ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    ) {
                        Text(
                            text = gameItem.name,
                            style = Typography.h5,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.neutral90,
                        )
                        Text(
                            text = gameItem.description,
                            style = Typography.p,
                            color = AppTheme.colors.neutral90,
                        )
                        Text(
                            text = "Czas wzrostu: ${gameItem.growingTime}s",
                            style = Typography.p,
                            color = AppTheme.colors.neutral90,
                        )
                        gameItem.yieldMultiplier?.let { yieldMultiplier ->
                            Text(
                                text = "Mnożnik plonu: $yieldMultiplier",
                                style = Typography.p,
                                color = AppTheme.colors.neutral90,
                            )
                        }

                        gameItem.expReward?.let { expReward ->
                            Text(
                                text = "Exp za zebranie: $expReward",
                                style = Typography.p,
                                color = AppTheme.colors.neutral90,
                            )
                        }
                        Text(
                            text = "Wymagany level ${gameItem.requiredLevel}",
                            style = Typography.h5,
                            color = AppTheme.colors.neutral90,
                        )
                        Text(
                            text = "${gameItem.price} $",
                            style = Typography.h5,
                            color = AppTheme.colors.neutral90,
                        )

                        if (user.value.money < gameItem.price || checkLevel(user.value.exp) < gameItem.requiredLevel) {
                            Text(
                                text = "Nie masz tyle pieniędzy lub nie masz wystarczającego poziomu",
                                style = Typography.h5,
                                color = AppTheme.colors.red,
                            )
                        } else {
                            androidx.compose.material3.Button(
                                onClick = {
                                    val backStackEntry =
                                        navController.getBackStackEntry("dashboard_screen")
                                    backStackEntry.savedStateHandle["refresh"] = true

                                    user.value.money -= gameItem.price

                                    saveUserToStorage(sharedPreferences, user.value)

                                    viewModel.addEquipmentItem(
                                        EquipmentModel(
                                            itemId = gameItem.id,
                                            quantity = 1
                                        )
                                    )
                                },
                            ) {
                                Text("Kup")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun checkLevel(exp: Int): Int {
    return when {
        exp >= 2600 -> 10
        exp >= 2100 -> 9
        exp >= 1650 -> 8
        exp >= 1250 -> 7
        exp >= 900 -> 6
        exp >= 600 -> 5
        exp >= 350 -> 4
        exp >= 150 -> 3
        exp >= 50 -> 2
        else -> 1
    }
}
