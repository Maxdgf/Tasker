package com.example.tasker.ui.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasker.R

@Composable
fun TextUiField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp),
        value = value,
        onValueChange = onValueChange,
        trailingIcon = {
            IconButton(onClick = { onValueChange("") }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_clear_24),
                    contentDescription = null
                )
            }
        },
        textStyle = TextStyle(fontSize = 15.sp),
        placeholder = {
            Text(
                text = placeholder,
                modifier = Modifier.basicMarquee(Int.MAX_VALUE),
                fontSize = 15.sp
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.onSecondary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.onSecondary
        )
    )
}