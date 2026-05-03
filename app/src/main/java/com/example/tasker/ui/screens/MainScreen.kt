package com.example.tasker.ui.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

import com.example.tasker.R
import com.example.tasker.databases.tasks_database.entities.TasksListEntity
import com.example.tasker.ui.components.ActionUiDialog
import com.example.tasker.ui.components.LoadingUiBlock
import com.example.tasker.ui.components.NoDataUiDescriptionBlock
import com.example.tasker.ui.components.SquaredUiButton
import com.example.tasker.ui.components.TasksListHeaderUiItem
import com.example.tasker.ui.screens.navigation.NavigationRoutes
import com.example.tasker.ui.states.TasksListResult
import com.example.tasker.ui.viewmodels.TasksViewModel
import com.example.tasker.utils.AppManager

@Composable
private fun TasksListView(
    paddingValues: PaddingValues,
    tasksList: List<TasksListEntity>,
    onNavigateTo: (String) -> Unit,
    onDeleteAllTasks: () -> Unit,
    onDeleteAllTasksById: (String) -> Unit
) {
    var currentTaskId: String? by rememberSaveable { mutableStateOf(null) }
    var confirmationDeleteAllTasksListDialogState by rememberSaveable { mutableStateOf(false) }
    var confirmationDeleteCurrentTasksListDialogState by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(horizontal = 5.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = tasksList,
            key = { tasks -> tasks.tasksId }
        ) { tasks ->
            TasksListHeaderUiItem(
                name = tasks.name,
                description = tasks.description,
                tasksCount = tasks.tasksCount,
                tasksCompletedCount = tasks.completedTasksCount,
                createdAt = tasks.time,
                isCompleted = tasks.isCompleted,
                onClick = {
                    onNavigateTo("${NavigationRoutes.TasksListViewScreen.route}/${tasks.tasksId}")
                },
                deleteAllTasksById = {
                    currentTaskId = tasks.tasksId
                    confirmationDeleteCurrentTasksListDialogState = true
                }
            )
        }
    }

    // delete all tasks lists confirmation dialog
    ActionUiDialog(
        state = confirmationDeleteAllTasksListDialogState,
        onDismissRequestFunction = { confirmationDeleteAllTasksListDialogState = false },
        containerColor = MaterialTheme.colorScheme.error,
        titleIcon = painterResource(R.drawable.outline_delete_forever_24),
        titleText = "Delete all tasks lists"
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
                SquaredUiButton(
                    onClick = { confirmationDeleteAllTasksListDialogState = false },
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
                        onDeleteAllTasks()
                        confirmationDeleteAllTasksListDialogState = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onError,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text(text = "yes") }
            }
        }
    }

    // delete current tasks list confirmation dialog
    ActionUiDialog(
        state = confirmationDeleteCurrentTasksListDialogState,
        onDismissRequestFunction = { confirmationDeleteCurrentTasksListDialogState = false },
        containerColor = MaterialTheme.colorScheme.error,
        titleIcon = painterResource(R.drawable.outline_delete_24),
        titleText = "Delete tasks list"
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = buildAnnotatedString {
                    append("Are you sure, want to ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("delete") }
                    append(" this tasks list?")
                }
            )

            Row {
                SquaredUiButton(
                    onClick = { confirmationDeleteCurrentTasksListDialogState = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onError,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text(text = "no") }

                Spacer(modifier = Modifier.weight(1f))

                SquaredUiButton(
                    onClick = {
                        currentTaskId?.let { onDeleteAllTasksById(it) }
                        confirmationDeleteCurrentTasksListDialogState = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onError,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text(text = "yes") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    onNavigateTo: (String) -> Unit,
    tasksViewModel: TasksViewModel
) {
    var confirmationDeleteAllTasksListDialogState by rememberSaveable { mutableStateOf(false) }
    var mainDropDownMenuState by rememberSaveable { mutableStateOf(false) }

    val allTasksList by tasksViewModel.allTasksLists.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                    // main dropdown menu
                    Box {
                        // show main dropdown menu button
                        IconButton(onClick = { mainDropDownMenuState = true }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_more_vert_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        DropdownMenu(
                            expanded = mainDropDownMenuState,
                            onDismissRequest = { mainDropDownMenuState = false }
                        ) {
                            when (val list = allTasksList) {
                                is TasksListResult.ContentList ->
                                    // delete all button
                                    DropdownMenuItem(
                                        onClick = {
                                            mainDropDownMenuState = false // close dropdown menu
                                            if (list.taskLists.isNotEmpty())
                                                confirmationDeleteAllTasksListDialogState = true
                                        },
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.outline_delete_forever_24),
                                                    contentDescription = null
                                                )

                                                Text(text = "delete all")
                                            }
                                        }
                                    )
                                else -> {}
                            }

                            val activity = LocalActivity.current
                            val appManager = remember { AppManager(activity) }

                            DropdownMenuItem(
                                onClick = { appManager.breakApp() },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.baseline_exit_to_app_24),
                                            contentDescription = null
                                        )

                                        Text(text = "exit app")
                                    }
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateTo(NavigationRoutes.TasksListCreationScreen.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_add_24),
                    contentDescription = null
                )
            }
        }
    ) { innerPadding ->
        // match tasks list state
        when (val list = allTasksList) {
            is TasksListResult.ContentList ->
                TasksListView(
                    paddingValues = innerPadding,
                    tasksList = list.taskLists,
                    onNavigateTo = onNavigateTo,
                    onDeleteAllTasks = tasksViewModel::deleteAllData,
                    onDeleteAllTasksById = tasksViewModel::deleteAllTasksById
                )
            is TasksListResult.Exception ->
                NoDataUiDescriptionBlock(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    description = list.message
                )
            TasksListResult.EmptyList ->
                NoDataUiDescriptionBlock(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    description = "No tasks lists!"
                )
            TasksListResult.Loading ->
                LoadingUiBlock(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
        }
    }
}