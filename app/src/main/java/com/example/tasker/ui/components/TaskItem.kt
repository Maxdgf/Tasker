package com.example.tasker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.tasker.R

/**
 * Creates a task item.
 *
 * @param id task item id.
 * @param number task order number.
 * @param task task text.
 * @param description task description (optional).
 * @param state completion state.
 * @param onStateChanged on state changed function.
 */
@Composable
fun TaskUiItem(
    id: Long,
    number: Int,
    task: String,
    description: String?,
    state: Boolean,
    onStateChanged: (state: Boolean, id: Long) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                color =
                    if (state) Color.Green.copy(alpha = 0.5f)
                    else Color.Transparent
            ), // set green background if task completed
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AnimatedVisibility(visible = state) {
            // if completed, set green check icon
            Icon(
                painter = painterResource(R.drawable.outline_check_small_24),
                contentDescription = null,
                tint = Color(0xFF4CAF50)
            )
        }

        if (!state)
            // if not completed, set task number
            Text(
                text = number.toString(),
                fontStyle = FontStyle.Italic
            )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = task,
                style =
                    if (state) TextStyle(textDecoration = TextDecoration.LineThrough)
                    else TextStyle.Default, // line through text if task completed
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            description?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (!state) {
            IconButton(
                onClick = {
                    onDelete()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_delete_24),
                    contentDescription = null
                )
            }

            IconButton(
                onClick = {
                    onEdit()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_edit_24),
                    contentDescription = null
                )
            }
        }

        Checkbox(
            checked = state,
            onCheckedChange = { state ->
                onStateChanged(state, id)
                if (state)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            },
            colors = CheckboxDefaults.colors(checkedColor = Color.Green) // green checkbox when task completed
        )
    }
}