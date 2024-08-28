package com.example.pszzapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.pszzapp.R
import com.example.pszzapp.data.util.DropdownMenuItemData
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography

@Composable
fun TopBar(
    title: String,
    subtitle: String? = null,
    warningInfo: String? = null,
    goodInfo: String? = null,
    onSettingsClick: (() -> Unit)? = null,
    onNotification: (() -> Unit)? = null,
    backNavigation: (() -> Unit)? = null,
    menuItems: List<DropdownMenuItemData> = emptyList(),
    isModalActive: Boolean = false,
    setModal: (Boolean) -> Unit = {},
) {
    var isDropdownMenuVisible by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            backNavigation?.let {
                Image(
                    painter = painterResource(R.drawable.arrow_left_black),
                    contentDescription = "arrow_right",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = it),
                )
            }

            Column(
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        style = Typography.h4,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTheme.colors.neutral90,
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        warningInfo?.let {
                            Text(
                                buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                        append("UWAGA! ")
                                    }
                                    append(it)
                                },
                                style = Typography.label,
                                color = AppTheme.colors.primary50,
                            )
                        }

                        goodInfo?.let {
                            Text(
                                text = it,
                                style = Typography.label,
                                fontWeight = FontWeight.SemiBold,
                                color = AppTheme.colors.green100,
                            )
                        }
                    }
                }
                subtitle?.let {
                    Text(
                        text = it,
                        style = Typography.tiny,
                        color = AppTheme.colors.black90,
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            onSettingsClick?.let {
                Image(
                    painter = painterResource(R.drawable.settings),
                    contentDescription = "arrow_right",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = it),
                )
            }

            onNotification?.let {
                Image(
                    painter = painterResource(R.drawable.notification),
                    contentDescription = "arrow_right",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = it),
                )
            }
        }
    }

    Dropdown(
        isDropdownMenuVisible = isDropdownMenuVisible,
        setDropdownMenuVisible = { isDropdownMenuVisible = it },
        menuItems = menuItems
    )
}