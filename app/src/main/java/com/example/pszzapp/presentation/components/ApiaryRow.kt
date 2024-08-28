package com.example.pszzapp.presentation.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.pszzapp.R
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.presentation.apiary.create.CreateApiaryConstants
import com.example.pszzapp.presentation.auth.base.VerticalSpacer
import com.example.pszzapp.presentation.dashboard.OverviewButton
import com.example.pszzapp.presentation.destinations.ApiaryScreenDestination
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun ApiaryRow(
    apiary: ApiaryModel,
    navigator: DestinationsNavigator
) {
    Row(
        modifier = Modifier
            .graphicsLayer {
                shadowElevation = 4.dp.toPx()
                shape = RoundedCornerShape(8.dp)
                clip = false
            }
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = { navigator.navigate(ApiaryScreenDestination(id = apiary.id)) })
            .background(
                color = AppTheme.colors.white
            )
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(60.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(AppTheme.colors.primary50)
        ) {
            Text(
                style = Typography.label,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.white,
                text = apiary.hivesCount.toString(),
            )
            Text(
                style = Typography.label,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.white,
                text = "ULI",
            )
        }

//        Log.d("LOG_H", stringResource(CreateApiaryConstants.type[apiary.type]))
//        Log.d("LOG_H", apiary.toString())

        Column(
        ) {
            Text(
                text = apiary.name,
                style = Typography.label,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.neutral90,
            )
            Text(
                text = apiary.location,
                style = Typography.small,
                color = AppTheme.colors.primary50,
            )
            Text(
                text = stringResource(CreateApiaryConstants.type[apiary.type]),
                style = Typography.tiny,
                textDecoration = TextDecoration.Underline,
                color = AppTheme.colors.neutral60,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(R.drawable.arrow_right_black),
            contentDescription = "arrow_right",
            modifier = Modifier.size(24.dp),
        )
    }

//    Surface(
//        modifier = Modifier
//            .fillMaxSize(),
//        onClick = {
//            navigator.navigate(
//                ApiaryScreenDestination(id = apiary.id)
//            )
//        }
//    ){
//        ListItem(
//            headlineContent = { Text(apiary.name) },
//            overlineContent = { Text("ID: ${apiary.id}") },
//            trailingContent = { Icon(
//                Icons.AutoMirrored.Filled.ArrowForward,
//                contentDescription = null,
//            ) },
//        )
//    }
}