package com.example.farmgame.presentation.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.FarmGame.R
import com.example.FarmGame.data.model.EquipmentModel
import com.example.FarmGame.data.model.FieldModel
import com.example.FarmGame.data.model.FieldType
import com.example.FarmGame.data.model.GameItemModel
import com.example.FarmGame.data.model.ItemType
import com.example.FarmGame.data.model.Status
import com.example.FarmGame.data.model.UserModel
import com.example.FarmGame.presentation.auth.base.VerticalSpacer
import com.example.FarmGame.presentation.dashboard.DashboardViewModel
import com.example.FarmGame.presentation.main.SnackbarHandler
import com.example.FarmGame.presentation.main.bottomBarPadding
import com.example.FarmGame.presentation.qrScanner.checkLevel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.concurrent.fixedRateTimer

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@RootNavGraph(start = true)
@Composable
fun DashboardScreen(
    message: String? = null,
    viewModel: DashboardViewModel = koinViewModel(),
    navController: NavController,
    snackbarHandler: SnackbarHandler,
) {
    val backStackEntry = navController.currentBackStackEntry
    val refresh = backStackEntry?.savedStateHandle?.get<Boolean>("refresh")

    LaunchedEffect(refresh) {
        launch {
            val currentBackStackEntry = navController.getBackStackEntry("dashboard_screen")

            if (refresh == true) viewModel.onRefresh()
            currentBackStackEntry.savedStateHandle["refresh"] = false
        }
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

    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        FarmingGameScreen(
            gameItems = viewModel.gameItems.collectAsState(),

            fields = viewModel.fields.collectAsState(),
            addField = { viewModel.addField(it) },
            updateField = { viewModel.updateField(it) },
            removeField = { viewModel.removeField(it) },

            equipment = viewModel.equipment.collectAsState(),
            addToEquipment = { viewModel.addToEquipment(it) },
            removeEquipmentItem = { viewModel.removeEquipmentItem(it) },
            clearEquipment = viewModel::clearEquipment,
            context = LocalContext.current
        )
    }
}


enum class Tool { NONE, PLANTING, HARVESTING }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FarmingGameScreen(
    gameItems: State<List<GameItemModel>>,
    fields: State<List<FieldModel>>,
    addField: (FieldModel) -> Unit,
    updateField: (FieldModel) -> Unit,
    removeField: (FieldModel) -> Unit,
    equipment: State<List<EquipmentModel>>,
    addToEquipment: (EquipmentModel) -> Unit,
    removeEquipmentItem: (EquipmentModel) -> Unit,
    clearEquipment: () -> Unit,
    context: Context
) {
    val sharedPreferences =
        remember { context.getSharedPreferences("farming_game", Context.MODE_PRIVATE) }
    val selectedTool = remember { mutableStateOf(Tool.NONE) }
    val selectedSeed = remember { mutableStateOf<String?>(null) }
    val timers = remember { mutableStateMapOf<Pair<Int, Int>, MutableState<Int>>() }
    var user = remember { mutableStateOf(UserModel("1", "Paweł", 0, 0)) }

    val openAlertDialog = remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(fields.value) {
        Log.d("LOG_USER", loadUserFromStorage(sharedPreferences).toString())
        user.value = loadUserFromStorage(sharedPreferences)

        fields.value.forEach { field ->
            val remainingTime = calculateRemainingTime(field)
            timers[field.x to field.y] = mutableIntStateOf(remainingTime)
            startFieldTimer(field, timers, updateField)
        }
    }

    Column(modifier = Modifier.background(Color.White)) {
        TopGameBar(
            exp = user.value.exp,
            money = user.value.money,
            setBottomSheet = { showBottomSheet = true }
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.fillMaxSize()
            ) {
                items(25) { index ->
                    val x = index % 5
                    val y = index / 5
                    val field = fields.value.firstOrNull { it.x == x && it.y == y }
                    val bgColor = when (field?.status) {
                        Status.IN_PROGRESS -> Color.Yellow
                        Status.READY_TO_HARVEST -> Color.Green
                        else -> Color.LightGray
                    }
                    val timeLeft = timers[x to y]?.value ?: 0

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(bgColor)
                            .border(1.dp, Color.Black)
                            .clickable {
                                when (selectedTool.value) {
                                    Tool.PLANTING -> {
                                        if (field == null && selectedSeed.value != null) {
                                            equipment.value
                                                .firstOrNull { it.itemId == selectedSeed.value }
                                                ?.takeIf { it.quantity > 0 }
                                                ?.let { seed ->
                                                    getItemById(
                                                        seed.itemId,
                                                        gameItems.value
                                                    )?.let { gameItem ->
                                                        val newField = FieldModel(
                                                            title = gameItem.name,
                                                            x = x,
                                                            y = y,
                                                            fieldType = FieldType.FARMLAND,
                                                            status = Status.IN_PROGRESS,
                                                            actionDuration = gameItem.growingTime,
                                                            timestamp = System.currentTimeMillis(),
                                                            cropId = getSeedCrop(seed.itemId)
                                                        )
                                                        addField(newField)
                                                        timers[x to y] =
                                                            mutableIntStateOf(gameItem.growingTime)
                                                        removeEquipmentItem(
                                                            EquipmentModel(
                                                                seed.itemId,
                                                                1
                                                            )
                                                        )
                                                    }
                                                }
                                        }
                                    }

                                    Tool.HARVESTING -> {
                                        if (field?.status == Status.READY_TO_HARVEST) {
                                            field.cropId?.let { id ->
                                                getItemById(
                                                    getCropSeed(id),
                                                    gameItems.value
                                                )?.let { gameItem ->
                                                    gameItem.yieldMultiplier?.let { yieldMultiplier ->
                                                        addToEquipment(
                                                            EquipmentModel(
                                                                id,
                                                                yieldMultiplier
                                                            )
                                                        )
                                                    }
                                                    gameItem.expReward?.let { expReward ->
                                                        user.value.exp += expReward
                                                        saveUserToStorage(
                                                            sharedPreferences,
                                                            user.value
                                                        )
                                                    }
                                                }
                                            }
                                            removeField(field)
                                            timers.remove(x to y)
                                        }
                                    }

                                    else -> Unit
                                }
                            }
                    ) {
                        Column {
                            Text(text = field?.title ?: "")
                            if (field?.status == Status.IN_PROGRESS) {
                                Text(text = "${timeLeft / 60}min")
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),

            ) {
            if (selectedTool.value == Tool.PLANTING) {
                Column {
                    Text("Wybierz nasiona:")
                    FlowRow {
                        equipment.value.forEach { equipmentModel ->
                            getItemById(id = equipmentModel.itemId, gameItems.value)
                                ?.takeIf { it.itemType == ItemType.SEED }
                                ?.let { item ->
                                    Button(
                                        onClick = {
                                            selectedSeed.value =
                                                if (selectedSeed.value == item.id) null else item.id
                                        },
                                        modifier = Modifier
                                            .size(64.dp)
                                    ) {
                                        Text(
                                            text = item.name,
                                            fontSize = 12.sp,
                                        )
                                    }
                                }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
            }

            Column {
                Text("Wybrane narzędzie: ${selectedTool.value}")
                Row {
                    Tool.entries.forEach { tool ->
                        Button(
                            onClick = {
                                selectedTool.value =
                                    if (selectedTool.value == tool) Tool.NONE else tool
                            },
                            modifier = Modifier
                                .size(64.dp)
                        ) {
                            Text(
                                text = tool.name,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Image(
                    painter = painterResource(R.drawable.settings),
                    contentDescription = "arrow_right",
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(onClick = { /* TODO */ })
                )

                Image(
                    painter = painterResource(R.drawable.apiary),
                    contentDescription = "arrow_right",
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(onClick = {
                            openAlertDialog.value = true
                        })
                )
            }

        }
    }



    if (openAlertDialog.value) {
        Dialog(onDismissRequest = { openAlertDialog.value = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(325.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text("Ekwipunek:")
                VerticalSpacer(32.dp)

                FlowRow(
                ) {
                    equipment.value.forEach { equipmentModel ->
                        getItemById(id = equipmentModel.itemId, gameItems.value)?.let {
                            Text(
                                text = "${it.name}: ${equipmentModel.quantity}",
                                modifier = Modifier
                                    .size(64.dp)
                                    .border(1.dp, Color.Black)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            // Sheet content
            Button(onClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            }) {
                Text("Hide bottom sheet")
            }
        }
    }
}

@Composable
fun TopGameBar(
    exp: Int,
    money: Int,
    setBottomSheet: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "Money: $money"
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${checkLevel(exp)}"
            )
            LinearProgressIndicator(
                progress = 0.6f,
                modifier = Modifier.size(50.dp, 10.dp),
                color = Color.Green,
                trackColor = Color.Red,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            Image(
                painter = painterResource(R.drawable.settings),
                contentDescription = "arrow_right",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = { /* TODO */ })
            )
        }

    }

    Column(
        modifier = Modifier
            .padding(top = 8.dp)
    ) {


        Column {
            Text(
                text = "Aktywna misja: Wyrwij chwasty 1 ->",
                modifier = Modifier.clickable(
                    onClick = { setBottomSheet(true) }
                )
            )
        }
    }
}


fun getItemById(id: String, availableGameItems: List<GameItemModel>): GameItemModel? {
    return availableGameItems.find { it.id == id }
}

fun getSeedCrop(id: String): String {
    return id.replace("seed", "crop", ignoreCase = true)
}

fun getCropSeed(id: String): String {
    return id.replace("crop", "seed", ignoreCase = true)
}

fun saveUserToStorage(sharedPreferences: SharedPreferences, user: UserModel) {
    val editor = sharedPreferences.edit()
    val gson = Gson()
    val json = gson.toJson(user)
    editor.putString("user", json)
    editor.apply()
}

fun loadUserFromStorage(sharedPreferences: SharedPreferences): UserModel {
    val gson = Gson()
    val json = sharedPreferences.getString("user", null)
    val type = object : TypeToken<UserModel>() {}.type
    return gson.fromJson(json, type) ?: UserModel("", "", 0, 0)
}

fun removeUserFromStorage(sharedPreferences: SharedPreferences) {
    val editor = sharedPreferences.edit()
    editor.remove("user")
    editor.apply()
}

fun removeUserQuestStateFromStorage(sharedPreferences: SharedPreferences) {
    val editor = sharedPreferences.edit()
    editor.remove("userQuestState")
    editor.apply()
}

fun calculateRemainingTime(field: FieldModel): Int {
    val elapsedTime = (System.currentTimeMillis() - field.timestamp) / 1000
    return maxOf(field.actionDuration - elapsedTime.toInt(), 0)
}

fun startFieldTimer(
    field: FieldModel,
    timers: MutableMap<Pair<Int, Int>, MutableState<Int>>,
    updateField: (FieldModel) -> Unit
) {
    val key = field.x to field.y
    fixedRateTimer("FieldTimer-$key", initialDelay = 0L, period = 1000L) {
        timers[key]?.let { timeLeft ->
            if (timeLeft.value > 0) {
                timeLeft.value -= 1
            } else {
                if (field.status != Status.READY_TO_HARVEST) {
                    updateField(field.copy(status = Status.READY_TO_HARVEST))
                }
                this.cancel()
            }
        }
    }
}

@Composable
fun BackgroundShapes() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(640.dp)
                .align(Alignment.TopStart)
                .offset {
                    IntOffset(
                        y = -100,
                        x = 0,
                    )
                },
        ) {
            Image(
                painter = painterResource(R.drawable.top_bg),
                contentScale = ContentScale.FillBounds,
                contentDescription = "",
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(391.dp)
                .align(Alignment.BottomEnd),
        ) {
            Image(
                painter = painterResource(R.drawable.bottom_bg),
                contentScale = ContentScale.FillBounds,
                contentDescription = "",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

