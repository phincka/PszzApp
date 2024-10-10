package com.example.pszzapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pszzapp.data.model.ListItemOverviewModel
import com.example.pszzapp.presentation.auth.base.VerticalSpacer
import com.example.pszzapp.presentation.dashboard.OverviewButton
import com.example.pszzapp.presentation.destinations.OverviewScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.time.format.DateTimeFormatter

@Composable
fun OverviewsLazyColumn(
    overviews: List<ListItemOverviewModel>,
    navToOverview: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            VerticalSpacer(height = 4.dp)
        }
        items(items = overviews, key = { it.id }) { overview ->
            OverviewButton(
                title = overview.overviewDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                info = overview.warningInfo,
                onClick = { navToOverview(overview.id) },
            )
        }
        item {
            VerticalSpacer(height = 4.dp)
        }
    }
}