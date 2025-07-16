package com.example.FarmGame.presentation.main

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.FarmGame.presentation.destinations.AccountScreenDestination
import com.example.FarmGame.presentation.destinations.ApiariesScreenDestination
import com.example.FarmGame.presentation.destinations.DashboardScreenDestination
import com.example.FarmGame.presentation.destinations.QrScannerScreenDestination
import com.example.FarmGame.ui.theme.AppTheme
import com.ramcosta.composedestinations.navigation.clearBackStack
import com.ramcosta.composedestinations.navigation.popBackStack
import com.ramcosta.composedestinations.navigation.popUpTo
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.Route
import com.example.FarmGame.R
import kotlinx.coroutines.launch

enum class BottomNavigationItems(
    val direction: Route,
    val icon: Int,
    val label: String,
) {
    Dashboard(
        direction = DashboardScreenDestination,
        icon = R.drawable.home,
        label = "Dashboard",
    ),
    Apiaries(
        direction = ApiariesScreenDestination,
        icon = R.drawable.apiary,
        label = "Pasieki",
    ),
    QrScanner(
        direction = QrScannerScreenDestination,
        icon = R.drawable.camera_white,
        label = "Skaner",
    ),

    Notifications(
        direction = AccountScreenDestination,
        icon = R.drawable.notification,
        label = "Powiadomienia",
    ),
    Account(
        direction = AccountScreenDestination,
        icon = R.drawable.account,
        label = "Konto",
    ),
}

enum class VisibleBottomBarDestination(val route: String) {
    Dashboard(DashboardScreenDestination.route),
    Apiaries(ApiariesScreenDestination.route),
    QrScanner(QrScannerScreenDestination.route),
    Account(AccountScreenDestination.route);

    companion object {
        fun find(route: String): VisibleBottomBarDestination? = entries.find { it.route == route }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter", "RestrictedApi",
    "UnusedBoxWithConstraintsScope"
)
@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = shouldShowBottomBarByDestination(currentDestinationRoute = currentRoute)
    val bottomBarOffset by animateDpAsState(
        targetValue = getBottomBarOffset(showBottomBar),
        label = ""
    )

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            currentRoute?.let {

                Column(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = 0,
                                y = bottomBarOffset.roundToPx()
                            )
                        }) {
                    NavigationBar(
                        modifier = Modifier.height(BOTTOM_BAR_HEIGHT),
                        containerColor = Color.Transparent,
                    ) {
                        BottomNavigationItems.entries.forEach { item ->
                            val route = item.direction.route
                            val rootBottomNavItemRoute = navController
                                .currentBackStack
                                .value
                                .findLast { entry ->
                                    BottomNavigationItems.entries
                                        .map { it.direction.route }
                                        .contains(entry.destination.route)
                                }
                                ?.destination
                                ?.route
                            val isSelected = rootBottomNavItemRoute == route

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        modifier = Modifier.size(32.dp),
                                        painter = painterResource(item.icon),
                                        contentDescription = item.label
                                    )
                                },
//                                label = { Text(item.label) },
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        navController.popBackStack(route, false)
                                    } else {
                                        navController.navToBottomNavItem(
                                            route,
                                            popUpTo = DashboardScreenDestination
                                        )
                                    }
                                },
                                colors = NavigationBarItemColors(
                                    selectedIndicatorColor = Color.Transparent,
                                    selectedIconColor = AppTheme.colors.primary50,
                                    selectedTextColor = AppTheme.colors.primary50,
                                    unselectedIconColor = AppTheme.colors.neutral30,
                                    unselectedTextColor = AppTheme.colors.neutral30,
                                    disabledIconColor = AppTheme.colors.neutral10,
                                    disabledTextColor = AppTheme.colors.neutral10,
                                ),
                            )
                        }
                    }
                }
            }
        },
        content = { content() },
    )
}

private fun shouldShowBottomBarByDestination(currentDestinationRoute: String?): Boolean =
    VisibleBottomBarDestination.entries.map { it.route }.contains(currentDestinationRoute)

private fun getBottomBarOffset(isBottomBarVisible: Boolean) = when (isBottomBarVisible) {
    true -> 0.dp
    else -> BOTTOM_BAR_HEIGHT + DIVIDER_HEIGHT
}

val BOTTOM_BAR_HEIGHT = 82.dp
val DIVIDER_HEIGHT = 1.dp


fun NavController.navToBottomNavItem(
    route: String,
    stackToClearRoute: Route? = null,
    popUpTo: DestinationSpec<*>,
) {
    stackToClearRoute?.let {
        if (!clearBackStack(it)) {
            popBackStack(it, inclusive = true, saveState = false)
        }
    }

    navigate(route) {
        popUpTo(popUpTo) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun Modifier.bottomBarPadding(navController: NavController): Modifier {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val showBottomBar = shouldShowBottomBarByDestination(
        currentDestinationRoute = navBackStackEntry?.destination?.route
    )

    return if (showBottomBar) {
        padding(bottom = getBottomBarOffset(isBottomBarVisible = false), top = 8.dp)
    } else {
        this
    }
}

@Composable
fun getSystemBottomBarHeight(): Dp {
    val view = LocalView.current
    val density = LocalDensity.current

    // Get WindowInsets for the current view
    val insets = ViewCompat.getRootWindowInsets(view)
    val bottomBarHeightPx = insets?.getInsets(WindowInsetsCompat.Type.systemBars())?.bottom ?: 0

    // Convert the pixel value to dp
    return with(density) { bottomBarHeightPx.toDp() }
}