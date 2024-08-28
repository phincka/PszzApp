package com.example.pszzapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pszzapp.ui.theme.AppTheme
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

@Composable
fun DatePicker(
    pickedDate: TemporalAccessor,
    setPickedDate: (LocalDate) -> Unit,
    dateDialogState: MaterialDialogState
) {
    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        MaterialDialog(
            dialogState = dateDialogState,
            buttons = {
                positiveButton(text = "Zapisz")
                negativeButton(text = "Anuluj")
            },
        ) {
            datepicker(
                initialDate = LocalDate.from(pickedDate),
                title = "Wybierz datę",
            ) {
                setPickedDate(it)
            }
        }
    }
}

fun formattedDate(pickedDate: LocalDate): String = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(pickedDate)
