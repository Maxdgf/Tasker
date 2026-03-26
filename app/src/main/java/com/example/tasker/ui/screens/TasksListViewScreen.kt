package com.example.tasker.ui.screens

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.tasker.R
import com.example.tasker.ui.components.ActionUiDialog
import com.example.tasker.ui.components.SquaredUiButton
import com.example.tasker.ui.components.TaskUiItem
import com.example.tasker.ui.components.TextUiField
import com.example.tasker.ui.navigation.NavigationRoutes
import com.example.tasker.ui.navigation.Navigator
import com.example.tasker.ui.viewmodels.TasksViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksListViewAppScreen(
    navigator: Navigator,
    tasksListId: Long?,
    taskId: String?,
    onTaskStateChanged: (state: Boolean, id: Long) -> Unit,
    tasksViewModel: TasksViewModel = hiltViewModel()
) {
    var editContent by rememberSaveable { mutableStateOf("") }
    var currentTaskId: Long? by rememberSaveable { mutableStateOf(null) }
    var editDescription by rememberSaveable { mutableStateOf("") }
    var updateTaskByIdDialogState by rememberSaveable { mutableStateOf(false) }
    var deleteTaskByIdDialogState by rememberSaveable { mutableStateOf(false) }

    val tasksList by tasksViewModel.allTasksById.collectAsState()
    val tasksListHeader by tasksViewModel.tasksListById.collectAsState()

    // update completed tasks count when tasks list changing
    LaunchedEffect(tasksList) {
        val id = taskId ?: return@LaunchedEffect

        val allCompletedTasksCount = tasksList.count { task -> task.isCompleted } // count completed tasks
        tasksViewModel.setCompletedTasksCountById(id, allCompletedTasksCount) // set completed tasks count to state

        if (allCompletedTasksCount == tasksList.size)
            tasksViewModel.manageTasksListCompletionStateById(id, true)
        else
            tasksViewModel.manageTasksListCompletionStateById(id, false)

        delay(10)
    }

    LaunchedEffect(Unit) {
        val id1 = taskId ?: return@LaunchedEffect
        val id2 = tasksListId ?: return@LaunchedEffect

        tasksViewModel.setTasks(id1)
        tasksViewModel.setTasksList(id2)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigator.navigateTo(NavigationRoutes.MainScreen.route)
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_arrow_back_24),
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Column {
                        tasksListHeader?.let { header ->
                            // name
                            Text(
                                text = header.name,
                                modifier = Modifier.basicMarquee(Int.MAX_VALUE),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )

                            // description (optional)
                            header.description?.let { text ->
                                Text(
                                    text = text,
                                    fontWeight = FontWeight.Light,
                                    modifier = Modifier.basicMarquee(Int.MAX_VALUE),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->
        ActionUiDialog(
            state = deleteTaskByIdDialogState,
            onDismissRequestFunction = { deleteTaskByIdDialogState = false },
            containerColor = MaterialTheme.colorScheme.error,
            titleIcon = painterResource(R.drawable.outline_delete_24),
            titleText = "Delete Task"
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Are you sure, want to ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("delete") }
                    append(" this tasks list?")
                }
            )

            Row {
                SquaredUiButton(
                    onClick = { deleteTaskByIdDialogState = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onError,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "no")
                }

                Spacer(modifier = Modifier.weight(1f))

                SquaredUiButton(
                    onClick = {
                        currentTaskId?.let { tasksViewModel.deleteTaskById(it) }
                        deleteTaskByIdDialogState = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onError,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text(text = "yes") }
            }
        }

        // edit task item dialog
        ActionUiDialog(
            state = updateTaskByIdDialogState,
            onDismissRequestFunction = { updateTaskByIdDialogState = false },
            titleIcon = painterResource(R.drawable.outline_edit_24),
            titleText = "Edit task"
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextUiField(
                    value = editContent,
                    onValueChange = { newValue -> editContent = newValue },
                    placeholder = "Edit your task content..."
                )

                TextUiField(
                    value = editDescription,
                    onValueChange = { newValue -> editDescription = newValue },
                    placeholder = "Edit your task description(optional)..."
                )

                Row {
                    SquaredUiButton(
                        onClick = {
                            updateTaskByIdDialogState = false
                        }
                    ) { Text(text = "cancel") }

                    Spacer(modifier = Modifier.weight(1f))

                    SquaredUiButton(onClick = {
                        val taskId = currentTaskId ?: return@SquaredUiButton

                        if (editContent.isNotEmpty()) {
                            tasksViewModel.updateTaskById(
                                taskId,
                                editContent,
                                if (editDescription.isNotEmpty()) editDescription
                                else null
                            )
                            updateTaskByIdDialogState = false
                        }
                    }) { Text(text = "edit") }
                }
            }
        }

        // tasks lazy list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            itemsIndexed(
                items = tasksList,
                key = { index, task -> task.id }
            ) { index, task ->
                TaskUiItem(
                    id = task.id,
                    number = index + 1,
                    task = task.content,
                    state = task.isCompleted,
                    onStateChanged = onTaskStateChanged,
                    onEdit = {
                        currentTaskId = task.id
                        editContent = task.content
                        task.description?.let { editDescription = it }

                        updateTaskByIdDialogState = true
                    },
                    onDelete = {
                        currentTaskId = task.id
                        editContent = task.content
                        task.description?.let { editDescription = it }

                        deleteTaskByIdDialogState = true
                    },
                    description = task.description
                )

                if (index < tasksList.lastIndex) HorizontalDivider()
            }
        }
    }
}