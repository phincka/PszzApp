package com.example.pszzapp.presentation.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavController.OnDestinationChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@Composable
fun Snackbar(
    modifier: Modifier = Modifier,
    navController: NavController,
    snackbarHandler: SnackbarHandler
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var isVisible by remember { mutableStateOf(false) }
    var isNavBarVisible by remember { mutableStateOf(false) }
    var isSuccessSnackbar by remember { mutableStateOf(false) }
    val snackbarPadding = if (isNavBarVisible) BOTTOM_BAR_HEIGHT else 0.dp
    val currentSnackbarData = snackbarHostState.currentSnackbarData
    val snackbarColor = if (isSuccessSnackbar) Color.Green else Color.Red
    val destinationChangedListener = OnDestinationChangedListener { _, _, _ ->
        snackbarHostState.currentSnackbarData?.dismiss()
        isVisible = false
    }
    val context = LocalContext.current

    DisposableEffect(Unit) {
        navController.addOnDestinationChangedListener(destinationChangedListener)
        onDispose {
            navController.removeOnDestinationChangedListener(destinationChangedListener)
        }
    }

    LaunchedEffect(Unit) {
        snackbarHandler.showSnackBarEvent.collect { data ->
            isSuccessSnackbar = data.success
            val message = data.message ?: data.messageResId?.let { context.getString(it) }
            message?.let {
                when (snackbarHostState.showSnackbar(it)) {
                    SnackbarResult.Dismissed -> data.onDismiss()
                    SnackbarResult.ActionPerformed -> Unit
                }
            } ?: run { Log.d("LOG", IllegalStateException("Missing snackbar message").toString()) }
        }
    }

    LaunchedEffect(currentSnackbarData) {
        isNavBarVisible = VisibleBottomBarDestination.entries.map { it.route }
            .contains(navController.currentDestination?.route)

        currentSnackbarData?.let { snackbarData ->
            val duration = calculateSnackbarDuration(currentSnackbarData.visuals.message)
            isVisible = true
            delay(duration)
            isVisible = false
            delay(AnimationConstants.DefaultDurationMillis.toLong())
            snackbarData.dismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
            .padding(bottom = snackbarPadding)
            .clipToBounds()
    ) {
        currentSnackbarData?.let { snackbarData ->
            CustomSnackbar(
                message = snackbarData.visuals.message,
                snackbarColor = snackbarColor
            )
        }
    }
}

class SnackbarHandler(private val scope: CoroutineScope) {
    private val _showSnackBarEvent = MutableSharedFlow<SnackbarData>()
    val showSnackBarEvent: SharedFlow<SnackbarData> = _showSnackBarEvent

    fun showSuccessSnackbar(
        messageResId: Int? = null,
        message: String? = null
    ) {
        scope.launch {
            _showSnackBarEvent.emit(
                SnackbarData(
                    messageResId = messageResId,
                    message = message,
                    success = true
                )
            )
        }
    }

    fun showErrorSnackbar(
        messageResId: Int? = null,
        message: String? = null,
        onDismiss: () -> Unit = {}
    ) {
        scope.launch {
            _showSnackBarEvent.emit(
                SnackbarData(
                    messageResId = messageResId,
                    message = message,
                    success = false,
                    onDismiss = onDismiss
                )
            )
        }
    }
}

data class SnackbarData(
    val messageResId: Int? = null,
    val message: String? = null,
    val success: Boolean,
    val onDismiss: () -> Unit = {}
)

/**
 * Calculate snackbar duration basing on message length
 */
private fun calculateSnackbarDuration(message: String) = (message.length * 35L)
    .coerceAtLeast(2000L)
    .coerceAtMost(4000L)


@Composable
fun CustomSnackbar(
    message: String,
    snackbarColor: Color,
) {
    Box(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(snackbarColor)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}