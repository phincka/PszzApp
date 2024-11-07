package com.example.pszzapp.presentation.dashboard

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.data.model.ListItemOverviewModel
import com.example.pszzapp.data.model.UserModel
import com.example.pszzapp.data.util.AccountUserState
import com.example.pszzapp.data.util.DropdownMenuItemData
import com.example.pszzapp.presentation.auth.base.VerticalSpacer
import com.example.pszzapp.presentation.components.Dropdown
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.OverviewsLazyColumn
import com.example.pszzapp.presentation.components.TextError
import com.example.pszzapp.presentation.destinations.ApiariesScreenDestination
import com.example.pszzapp.presentation.destinations.BaseAuthScreenDestination
import com.example.pszzapp.presentation.destinations.CreateApiaryScreenDestination
import com.example.pszzapp.presentation.destinations.DashboardScreenDestination
import com.example.pszzapp.presentation.destinations.OverviewScreenDestination
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.Direction
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@RootNavGraph(start = true)
@Composable
fun DashboardScreen(
    destinationsNavigator: DestinationsNavigator,
    viewModel: DashboardViewModel = koinViewModel(),
    navController: NavController,
) {
    Log.d("LOG_DASZ", "DASHBOARD")
    val lastOverviewsState = viewModel.lastOverviewsState.collectAsState().value

    val buttonTilesNavigation = listOf(
        ButtonTile(
            title = "Twoje pasieki",
            icon = R.drawable.ic_tile_button,
            direction = ApiariesScreenDestination,
        ),
        ButtonTile(
            title = "Dodaj pasiekę",
            icon = R.drawable.ic_tile_button,
            direction = CreateApiaryScreenDestination(),
        )
    )

    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        when (val accountUserState = viewModel.accountUserState.collectAsState().value) {
            is AccountUserState.Loading -> LoadingDialog()

            is AccountUserState.SignedInState -> DashboardLayout(
                buttonTilesNavigation = buttonTilesNavigation,
                lastOverviewsState = lastOverviewsState,
                navToOverview = destinationsNavigator::navToOverview,
                navigator = destinationsNavigator,
                user = accountUserState.user,
            )

            is AccountUserState.GuestState -> destinationsNavigator.navToBaseAuth()

            is AccountUserState.Error -> TextError(accountUserState.message)

            is AccountUserState.None -> Unit
        }
    }
}


@Composable
fun DashboardLayout(
    buttonTilesNavigation: List<ButtonTile>,
    lastOverviewsState: LastOverviewsState,
    navToOverview: (String) -> Unit,
    navigator: DestinationsNavigator,
    user: UserModel,
) {
    Column {
        CircleTopBar(
            circleText = user.name.first().uppercase(),
            title = user.name,
            subtitle = "Śledź każdy etap rozwoju rodziny pszczeliej",
            showSubtitle = true,
        )

        ButtonTiles(
            navigator = navigator,
            buttonTilesNavigation = buttonTilesNavigation,
        )

//        EndangeredHives(
//            sectionTitle = "Ula wymagające działania",
//            navigator = navigator,
//        )

        LastOverviews(
            sectionTitle = "Ostatnie przeglądy",
            lastOverviewsList = if (lastOverviewsState is LastOverviewsState.Success) lastOverviewsState.overviews else null,
            isLoading = lastOverviewsState is LastOverviewsState.Loading,
            navToOverview = navToOverview,
        )
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

@Composable
fun CircleTopBar(
    circleText: String,
    title: String,
    subtitle: String,
    showSubtitle: Boolean = false,
    isDropdownMenuVisible: Boolean = false,
    onSettingsClick: ((Boolean) -> Unit)? = null,
    menuItems: List<DropdownMenuItemData> = emptyList(),
) {

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(AppTheme.colors.primary50)
        ) {
            Text(
                style = Typography.h4,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.white,
                text = circleText,
            )
        }

        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                style = Typography.h4,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.neutral90,
                text = title,
            )

            if (showSubtitle) {
                Text(
                    style = Typography.label,
                    color = AppTheme.colors.neutral90,
                    text = subtitle,
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        onSettingsClick?.let {
            Image(
                painter = painterResource(R.drawable.settings),
                contentDescription = "arrow_right",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = { onSettingsClick(true) }),
            )
        }
    }

    onSettingsClick?.let { onClick ->
        Dropdown(
            isDropdownMenuVisible = isDropdownMenuVisible,
            setDropdownMenuVisible = { onClick(it) },
            menuItems = menuItems
        )
    }
}

@Composable
fun ButtonTiles(
    navigator: DestinationsNavigator,
    buttonTilesNavigation: List<ButtonTile>
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy((-8).dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(buttonTilesNavigation) { item ->
                Column(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .graphicsLayer {
                            shadowElevation = 4.dp.toPx()
                            shape = RoundedCornerShape(8.dp)
                            clip = false
                        }
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = { navigator.navigate(item.direction) })
                        .background(AppTheme.colors.white)
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(112.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.primary10)
                    ) {
                        Image(
                            painter = painterResource(item.icon),
                            contentDescription = "base_bg",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 20.dp)
                        )
                    }

                    VerticalSpacer(6.dp)

                    Text(
                        text = item.title,
                        style = Typography.small,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTheme.colors.neutral90,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(
    text: String,
) {
    Text(
        text = text,
        style = Typography.h5,
        fontWeight = FontWeight.SemiBold,
        color = AppTheme.colors.neutral90,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun EndangeredHives(
    sectionTitle: String? = null,
    navigator: DestinationsNavigator,
) {
    if (sectionTitle != null) SectionTitle(sectionTitle)

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OverviewButton(
            title = "Ul 01",
            info = "Brak matki!",
            hiveType = "(ul produkcyjny)",
            onClick = { navigator.navigate(DashboardScreenDestination) },
        )

        OverviewButton(
            title = "Ul 01",
            info = "Mała ilosć pokarmu",
            hiveType = "(odkład)",
            onClick = { navigator.navigate(DashboardScreenDestination) },
        )
    }
}

@Composable
fun LastOverviews(
    sectionTitle: String? = null,
    lastOverviewsList: List<ListItemOverviewModel>? = null,
    isLoading: Boolean,
    navToOverview: (String) -> Unit,
) {
    if (isLoading) LoadingDialog()

    if (sectionTitle != null) SectionTitle(sectionTitle)

    lastOverviewsList?.let {
        OverviewsLazyColumn(
            overviews = it,
            navToOverview = navToOverview,
        )
    }
}

@Composable
fun OverviewButton(
    title: String,
    info: String? = null,
    hiveType: String? = null,
    date: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .graphicsLayer {
                shadowElevation = 4.dp.toPx()
                shape = RoundedCornerShape(8.dp)
                clip = false
            }
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(
                color = AppTheme.colors.white
            )
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = Typography.p,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.neutral90,
                )

                Spacer(modifier = Modifier.width(8.dp))

                hiveType?.let {
                    Text(
                        text = it,
                        style = Typography.tiny,
                        fontStyle = FontStyle.Italic,
                        color = AppTheme.colors.neutral60,
                    )
                }
                date?.let {
                    Text(
                        text = it,
                        style = Typography.tiny,
                        fontStyle = FontStyle.Italic,
                        color = AppTheme.colors.neutral60,
                    )
                }
            }

            VerticalSpacer(4.dp)

            if (info != null) {
                Text(
                    text = info,
                    style = Typography.small,
                    color = AppTheme.colors.primary50,
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.arrow_right_black),
            contentDescription = "arrow_right",
            modifier = Modifier.size(24.dp),
        )
    }
}

data class ButtonTile(
    val title: String,
    val icon: Int,
    val direction: Direction,
)

fun DestinationsNavigator.navToOverview(overviewId: String) = navigate(OverviewScreenDestination(overviewId))
private fun DestinationsNavigator.navToBaseAuth() = navigate(BaseAuthScreenDestination)
