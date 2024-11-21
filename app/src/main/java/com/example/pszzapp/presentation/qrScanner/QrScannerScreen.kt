package com.example.pszzapp.presentation.qrScanner

import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.ImageFormat
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.pszzapp.presentation.auth.base.Button
import com.example.pszzapp.presentation.auth.base.VerticalSpacer
import com.example.pszzapp.presentation.components.LoadingDialog
import com.example.pszzapp.presentation.components.TopBar
import com.example.pszzapp.presentation.destinations.HiveScreenDestination
import com.example.pszzapp.presentation.main.SnackbarHandler
import com.example.pszzapp.presentation.main.bottomBarPadding
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus.Denied
import com.google.accompanist.permissions.PermissionStatus.Granted
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@kotlin.OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("StateFlowValueCalledInComposition", "PermissionLaunchedDuringComposition")
@Destination
//@RootNavGraph(start = true)
@Composable
fun QrScannerScreen(
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Boolean>,
    viewModel: QrScannerViewModel = koinViewModel(),
    navController: NavController,
    snackbarHandler: SnackbarHandler,
) {
    val qrScannerState = viewModel.qrScannerState.collectAsState().value

    var isCreateItemDialogVisible by remember { mutableStateOf(false) }
    var hiveId by remember { mutableStateOf("") }

    if (qrScannerState is QrScannerState.Error) isCreateItemDialogVisible = false

    LaunchedEffect(key1 = hiveId) {
        launch {
            delay(10000L)
            hiveId = ""
        }
    }

    LaunchedEffect(qrScannerState) {
        launch {
            if (qrScannerState is QrScannerState.Error) snackbarHandler.showErrorSnackbar(message = qrScannerState.message)
        }
    }

    val cameraPermissionState = rememberPermissionState(permission = CAMERA)

    LaunchedEffect(cameraPermissionState.status) {
        when (cameraPermissionState.status) {
            Granted -> { }
            is Denied -> { cameraPermissionState.launchPermissionRequest() }
        }
    }

    Box(
        modifier = Modifier
            .bottomBarPadding(navController = navController)
            .fillMaxSize()
    ) {
        Column {
            TopBar(
                backNavigation = { navigator.navigateUp() },
                title = "Zeskanuj ul",
            )

            QRCodeScanner(
                showBottomSheet = {
                    if (!isCreateItemDialogVisible && hiveId != it) {
                        hiveId = it
                        viewModel.getHiveById(hiveId)
                        isCreateItemDialogVisible = true
                    }
                }
            )
        }

        BottomSheet(
            isVisible = isCreateItemDialogVisible,
            setVisible = { isCreateItemDialogVisible = it },
        ) {
            if (qrScannerState is QrScannerState.Loading) LoadingDialog()

            if (qrScannerState is QrScannerState.Success) {
                val hive = qrScannerState.hive

                Text(
                    text = "Informacje o ulu:",
                    style = Typography.h5,
                    fontWeight = FontWeight.SemiBold
                )
                VerticalSpacer(8.dp)

                Text(
                    text = "ID: ${hive.id}",
                    style = Typography.small,
                    color = AppTheme.colors.neutral60,
                )

                Text(
                    text = "Nazwa: ${hive.name}",
                    style = Typography.small,
                    color = AppTheme.colors.neutral70,
                )

                VerticalSpacer(32.dp)

                Button(
                    text = "Zobacz więcej",
                    onClick = { navigator.navigate(HiveScreenDestination(id = hive.id)) },
                    showIcon = true,
                )
            }
        }
    }
}


@OptIn(ExperimentalGetImage::class)
@Composable
fun QRCodeScanner(
    showBottomSheet: (String) -> Unit,
) {
    val context = LocalContext.current
    var code by remember { mutableStateOf<Result?>(null) }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { ctx ->
            val previewView = PreviewView(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(android.util.Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    val image = imageProxy.image ?: return@setAnalyzer

                    if (image.format == ImageFormat.YUV_420_888) {
                        val buffer = image.planes[0].buffer
                        val bytes = ByteArray(buffer.capacity()).also { buffer.get(it) }

                        val source = YUVLuminanceSource(
                            bytes,
                            image.width,
                            image.height,
                            0,
                            0,
                            image.width,
                            image.height
                        )

                        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
                        try {
                            val result = QRCodeReader().decode(binaryBitmap)
                            code = result
                        } catch (e: Exception) {
                            // No result found
                        }
                    }

                    imageProxy.close()
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        ctx as ComponentActivity,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (exc: Exception) {
                    // Handle exception
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }, modifier = Modifier.fillMaxSize())

        code?.let {
            showBottomSheet(it.text)
        }
    }
}

class YUVLuminanceSource(
    private val yuvData: ByteArray,
    private val dataWidth: Int,
    private val dataHeight: Int,
    private val left: Int,
    private val top: Int,
    private val width: Int,
    private val height: Int
) : LuminanceSource(width, height) {

    override fun getRow(y: Int, row: ByteArray?): ByteArray {
        val rowBytes = row ?: ByteArray(width)
        System.arraycopy(yuvData, (y + top) * dataWidth + left, rowBytes, 0, width)
        return rowBytes
    }

    override fun getMatrix(): ByteArray {
        val area = width * height
        val matrix = ByteArray(area)
        for (y in 0 until height) {
            System.arraycopy(yuvData, (y + top) * dataWidth + left, matrix, y * width, width)
        }
        return matrix
    }
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    isVisible: Boolean,
    setVisible: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) {
    if (!isVisible) return

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    ModalBottomSheet(
        onDismissRequest = {
            setVisible(false)
        },
        sheetState = sheetState,
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 64.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

fun Context.openAppSettings() {
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        addCategory(Intent.CATEGORY_DEFAULT)
        data = Uri.parse("package:$packageName")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    }
    startActivity(intent)
}