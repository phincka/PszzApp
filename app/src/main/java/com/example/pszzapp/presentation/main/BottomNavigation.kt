package com.example.pszzapp.presentation.main

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pszzapp.presentation.destinations.AccountScreenDestination
import com.example.pszzapp.presentation.destinations.ApiariesScreenDestination
import com.example.pszzapp.presentation.destinations.ApiaryScreenDestination
import com.example.pszzapp.presentation.destinations.DashboardScreenDestination
import com.example.pszzapp.presentation.destinations.HiveScreenDestination
import com.example.pszzapp.presentation.destinations.OverviewScreenDestination
import com.example.pszzapp.presentation.destinations.QrScannerScreenDestination
import com.ramcosta.composedestinations.navigation.clearBackStack
import com.ramcosta.composedestinations.navigation.popBackStack
import com.ramcosta.composedestinations.navigation.popUpTo
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.Route


enum class BottomNavigationItems(
    val direction: Route,
    val icon: ImageVector,
    val label: String,
) {
    Dashboard(
        direction = DashboardScreenDestination,
        icon = Icons.Default.Home,
        label = "Dashboard",
    ),
    Apiaries(
        direction = ApiariesScreenDestination,
        icon = Icons.Default.AddCard,
        label = "Pasieki",
    ),
    QrScanner(
        direction = QrScannerScreenDestination,
        icon = Icons.Default.CameraAlt,
        label = "Skaner",
    ),
    Notifications(
        direction = AccountScreenDestination,
        icon = Icons.Default.Notifications,
        label = "Powiadomienia",
    ),
    Account(
        direction = AccountScreenDestination,
        icon = Icons.Default.AccountCircle,
        label = "Konto",
    ),
}

enum class VisibleBottomBarDestination(val route: String) {
    Dashboard(DashboardScreenDestination.route),
    Apiaries(ApiariesScreenDestination.route),
    Apiary(ApiaryScreenDestination.route),
    Hive(HiveScreenDestination.route),
    Overview(OverviewScreenDestination.route),
    QrScanner(QrScannerScreenDestination.route),
    Account(AccountScreenDestination.route);

    companion object {
        fun find(route: String): VisibleBottomBarDestination? = entries.find { it.route == route }
    }
}

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
        bottomBar = {
            currentRoute?.let {
                Column(modifier = Modifier
                    .offset {
                    IntOffset(
                        x = 0,
                        y = bottomBarOffset.roundToPx()
                    )
                }) {
                    NavigationBar(
                        modifier = Modifier.height(BOTTOM_BAR_HEIGHT)
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
                                icon = { Icon(item.icon, contentDescription = item.label) },
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