package com.example.pszzapp.presentation.auth.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pszzapp.R
import com.example.pszzapp.presentation.destinations.SignInScreenDestination
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun BaseAuthScreen(
    navigator: DestinationsNavigator,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
            .background(Color.White)
    ) {
        BaseBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = 84.dp, start = 16.dp, end = 16.dp)
                    .width(275.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Get Cooking",
                    style = Typography.heading,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.white,
                    textAlign = TextAlign.Center,
                )

                VerticalSpacer(20.dp)

                Text(
                    text = "Simple way to find Tasty Recipe",
                    style = Typography.p,
                    color = AppTheme.colors.white,
                    textAlign = TextAlign.Center,
                )

                VerticalSpacer(52.dp)

                Button(
                    onClick = { navigator.navigate(SignInScreenDestination) },
                    text = "Start!",
                    showIcon = true,
                )
            }
        }
    }
}

@Composable
fun VerticalSpacer(height: Dp) = Spacer(modifier = Modifier.height(height))

@Composable
private fun BaseBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.base_bg),
            contentScale = ContentScale.FillBounds,
            contentDescription = "base_bg",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun Button(
    onClick: () -> Unit = {},
    text: String,
    showIcon: Boolean = false,
    isLoading: Boolean = false,
) {
    Button(
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        onClick = onClick,
        contentPadding = PaddingValues(16.dp),
        shape = RoundedCornerShape(10.dp),
    ) {
        if (!isLoading) {
            Text(
                text = text,
                style = Typography.p,
                fontWeight = FontWeight.SemiBold
            )

            if (showIcon) {
                Image(
                    painter = painterResource(R.drawable.arrow_right),
                    contentDescription = "base_bg",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(start = 8.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = AppTheme.colors.white,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}