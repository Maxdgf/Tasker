package com.example.tasker.ui.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasker.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Converts value to percent of max value.
 *
 * @param value input value.
 * @param max max value.
 *
 * @return calculated percent.
 */
private fun calculatePercent(value: Int, max: Int) = value * 1f / max

/**
 * Formats time millis to datetime.
 *
 * @param time time millis.
 * @return datetime string.
 */
private fun formatLongToStringDatetime(time: Long): String {
    val instant = Instant.ofEpochMilli(time)
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}

/**
 * Creates tasks list header item.
 *
 * @param name name of list.
 * @param description description of list (optional).
 * @param tasksCount all tasks count in list.
 * @param tasksCompletedCount completed tasks count in list.
 * @param createdAt tasks list creation time.
 * @param isCompleted is tasks list completed state.
 * @param onClick on click function.
 * @param deleteAllTasksById delete all tasks by id function.
 */
@Composable
fun TasksListHeaderUiItem(
    name: String,
    description: String?,
    tasksCount: Int,
    tasksCompletedCount: Int,
    createdAt: Long,
    isCompleted: Boolean,
    onClick: () -> Unit,
    deleteAllTasksById: () -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .padding(5.dp)
                .clickable(onClick = { onClick() }),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.basicMarquee(Int.MAX_VALUE),
                    style =
                        if (isCompleted) TextStyle(textDecoration = TextDecoration.LineThrough)
                        else TextStyle.Default // line through text if tasks list completed
                )

                description?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.basicMarquee(Int.MAX_VALUE)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_event_24),
                        contentDescription = null
                    )

                    Text(
                        text = formatLongToStringDatetime(createdAt),
                        fontStyle = FontStyle.Italic,
                        fontSize = 15.sp
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_check_small_24),
                        contentDescription = null,
                        tint =
                            if (isCompleted) Color.Green
                            else LocalContentColor.current // if tasks list completed set green color tint to check icon, else default color
                    )

                    Text(
                        text = "$tasksCompletedCount / $tasksCount",
                        fontSize = 12.sp
                    )

                    val percent = calculatePercent(tasksCompletedCount, tasksCount)
                    LinearProgressIndicator(
                        progress = { percent },
                        color =
                            if (percent == 1f) Color.Green
                            else Color.Red,
                        drawStopIndicator = {} // without stop indicator
                    )
                }
            }

            IconButton(onClick = { deleteAllTasksById() }) {
                Icon(
                    painter = painterResource(R.drawable.outline_delete_24),
                    contentDescription = null
                )
            }
        }
    }
}