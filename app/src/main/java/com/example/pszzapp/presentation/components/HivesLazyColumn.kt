package com.example.pszzapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.presentation.auth.base.VerticalSpacer
import com.example.pszzapp.presentation.dashboard.OverviewButton
import com.example.pszzapp.presentation.destinations.DashboardScreenDestination
import com.example.pszzapp.presentation.destinations.HiveScreenDestination
import com.example.pszzapp.presentation.hive.create.CreateHiveConstants
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun HivesLazyColumn(
    hives: List<HiveModel>,
    navToHive : (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            VerticalSpacer(height = 4.dp)
        }
        items(items = hives, key = { it.id }) { hive ->
            OverviewButton(
                title = hive.name,
                info = "hive_info",
                hiveType = stringResource(CreateHiveConstants.familyType[hive.familyType]),
                onClick = { navToHive(hive.id) },
            )
        }
        item {
            VerticalSpacer(height = 4.dp)
        }
    }
}