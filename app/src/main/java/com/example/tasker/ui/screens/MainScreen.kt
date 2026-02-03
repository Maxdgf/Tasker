package com.example.tasker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tasker.R
import com.example.tasker.databases.tasks_database.entities.TasksListEntity
import com.example.tasker.ui.components.ActionUiDialog
import com.example.tasker.ui.components.SquaredUiButton
import com.example.tasker.ui.components.TasksListHeaderUiItem
import com.example.tasker.ui.navigation.NavigationRoutes
import com.example.tasker.ui.navigation.Navigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    allTasksList: List<TasksListEntity>,
    navigator: Navigator,
    setTaskId: (String) -> Unit,
    lazyListState: LazyListState,
    deleteAllTasksLists: () -> Unit,
    confirmationDeleteAllTasksListDialogState: Boolean,
    updateConfirmationDeleteAllTasksListDialogState: (Boolean) -> Unit,
    taskId: String?,
    deleteAllTasksById: () -> Unit,
    confirmationDeleteCurrentTasksListDialogState: Boolean,
    updateConfirmationDeleteCurrentTasksListDialogState: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {}
            )
        },
        floatingActionButton = {
            // add tasks list floating button
            Box {
                FloatingActionButton(
                    onClick = { navigator.navigateTo(NavigationRoutes.TasksListCreationScreen.route) },
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(65.dp)
                        .offset(y = 50.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_add_24),
                        contentDescription = null
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { updateConfirmationDeleteAllTasksListDialogState(true) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_delete_forever_24),
                            contentDescription = null
                        )
                    }

                    IconButton(
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_query_stats_24),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // delete all tasks lists confirmation dialog
        ActionUiDialog(
            state = confirmationDeleteAllTasksListDialogState,
            onDismissRequestFunction = { updateConfirmationDeleteAllTasksListDialogState(false) }
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = buildAnnotatedString {
                        append("Are you sure, want to ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("delete all") }
                        append(" tasks lists?")
                    }
                )

                Row {
                    SquaredUiButton(onClick = { updateConfirmationDeleteAllTasksListDialogState(false) }) {
                        Text(text = "no")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    SquaredUiButton(
                        onClick = {
                            deleteAllTasksLists()
                            updateConfirmationDeleteAllTasksListDialogState(false)
                        }
                    ) { Text(text = "yes") }
                }
            }
        }

        // delete current tasks list confirmation dialog
        ActionUiDialog(
            state = confirmationDeleteCurrentTasksListDialogState,
            onDismissRequestFunction = { updateConfirmationDeleteCurrentTasksListDialogState(false) }
        ) {
            val currentTasksList = allTasksList.find { list -> list.tasksId == taskId }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = buildAnnotatedString {
                        append("Are you sure, want to ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("delete") }
                        append(" ${currentTasksList?.name}?")
                        append(" ⚠️Completed ${currentTasksList?.completedTasksCount} / ${currentTasksList?.tasksCount} tasks!")
                    }
                )

                Row {
                    SquaredUiButton(onClick = { updateConfirmationDeleteCurrentTasksListDialogState(false) }) {
                        Text(text = "no")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    SquaredUiButton(
                        onClick = {
                            deleteAllTasksById()
                            updateConfirmationDeleteCurrentTasksListDialogState(false)
                        }
                    ) { Text(text = "yes") }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = lazyListState,
                contentPadding = PaddingValues(horizontal = 5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(
                    items = allTasksList,
                    key = { tasks -> tasks.tasksId }
                ) { tasks ->
                    TasksListHeaderUiItem(
                        name = tasks.name,
                        description = tasks.description,
                        tasksCount = tasks.tasksCount,
                        tasksCompletedCount = tasks.completedTasksCount,
                        createdAt = tasks.time,
                        isCompleted = tasks.isCompleted,
                        deadline = tasks.deadline,
                        onClick = {
                            setTaskId(tasks.tasksId)
                            navigator.navigateTo(NavigationRoutes.TasksListViewScreen.route)
                        },
                        deleteAllTasksById = {
                            setTaskId(tasks.tasksId) // set current task id, if tasks list not completed show confirmation dialog, else delete now
                            if (!tasks.isCompleted) updateConfirmationDeleteCurrentTasksListDialogState(true)
                            else deleteAllTasksById()
                        }
                    )
                }
            }
        }
    }
}