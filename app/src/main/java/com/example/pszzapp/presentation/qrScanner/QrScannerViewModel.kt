package com.example.pszzapp.presentation.qrScanner

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.domain.usecase.hive.GetHiveByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class QrScannerViewModel(
    private val getHiveByIdUseCase: GetHiveByIdUseCase,
) : ViewModel() {
    private val _qrScannerState: MutableStateFlow<QrScannerState> = MutableStateFlow(QrScannerState.None)
    val qrScannerState: StateFlow<QrScannerState> = _qrScannerState

    fun getHiveById(id: String) {
        _qrScannerState.value = QrScannerState.Loading
        Log.d("LOG_QR", "hive".toString())

        viewModelScope.launch {
            try {
                val hive = getHiveByIdUseCase(id)

                if (hive != null) {
                    _qrScannerState.value = QrScannerState.Success(hive = hive)
                } else {
                    _qrScannerState.value = QrScannerState.Error("Failed: Nie znaleziono ula o podanym ID")
                }
            } catch (e: Exception) {
                _qrScannerState.value = QrScannerState.Error("Failed: ${e.message}")
            }
        }
    }
}

sealed class QrScannerState {
    data object None : QrScannerState()
    data object Loading : QrScannerState()
    data class Success(
        val hive: HiveModel,
    ) : QrScannerState()
    data class Error(val message: String) : QrScannerState()
}