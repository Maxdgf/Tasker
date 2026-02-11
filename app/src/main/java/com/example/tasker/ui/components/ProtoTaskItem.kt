package com.example.tasker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasker.R

/**
 * Creates a task item, which displayed on tasks list creation screen.
 *
 * @param number order number.
 * @param task task text.
 * @param description task description (optional).
 * @param deleteThis delete task from list function.
 */
@Composable
fun ProtoTaskUiItem(
    number: Int,
    task: String,
    description: String?,
    deleteThis: () -> Unit
) {
    val haptic = LocalHapticFeedback.current // get haptic

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // task order number
        Text(
            text = number.toString(),
            fontStyle = FontStyle.Italic
        )

        // task data
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(text = task) // task text
            Text(
                text = description ?: "description not provided",
                fontSize = 12.sp,
                fontWeight = FontWeight.Light
            ) // task description
        }

        // delete this task item button
        IconButton(
            onClick = {
                deleteThis()
                haptic.performHapticFeedback(HapticFeedbackType.LongPress) // haptic
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_clear_24),
                contentDescription = null
            )
        }
    }
}