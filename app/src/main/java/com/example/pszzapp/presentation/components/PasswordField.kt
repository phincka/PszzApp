package com.example.pszzapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pszzapp.R
import com.example.pszzapp.presentation.auth.base.VerticalSpacer
import com.example.pszzapp.ui.theme.AppTheme
import com.example.pszzapp.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(
    label: String? = null,
    placeholder: String = "********",
    value: String,
    setValue: (String) -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column {
        label?.let {
            Text(
                text = label,
                style = Typography.label,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.neutral90,
            )
        }

        VerticalSpacer(6.dp)

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = { setValue(it) },
            placeholder = {
                Text(placeholder)
            },
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppTheme.colors.primary40,
                unfocusedBorderColor = AppTheme.colors.neutral30,
                unfocusedPlaceholderColor = AppTheme.colors.neutral30,
            ),
            textStyle = Typography.label.copy(
                color = AppTheme.colors.neutral90,
            ),
            maxLines = 1,
            keyboardActions = KeyboardActions(
                onDone = {}
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = null

                IconButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ) {
                    Icon(imageVector = image, description)
                }
            },
        )
    }
}