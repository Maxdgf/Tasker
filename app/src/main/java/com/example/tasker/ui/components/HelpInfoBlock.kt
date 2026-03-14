package com.example.tasker.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.example.tasker.R

/**
 * Creates ui block with help info text.
 * @param text help info.
 */
@Composable
fun HelpUiInfoBlock(text: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        val color =
            if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.5f)
            else Color.Black.copy(alpha = 0.5f)

        Icon(
            painter = painterResource(R.drawable.baseline_info_outline_24),
            contentDescription = null,
            tint = color
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            fontStyle = FontStyle.Italic,
            color = color
        )
    }
}