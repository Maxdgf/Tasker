package com.example.tasker.ui.screens

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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import com.example.tasker.R
import com.example.tasker.ui.components.ActionUiDialog
import com.example.tasker.ui.components.SquaredUiButton
import com.example.tasker.ui.components.TasksListHeaderUiItem
import com.example.tasker.ui.navigation.NavigationRoutes
import com.example.tasker.ui.navigation.Navigator
import com.example.tasker.ui.viewmodels.TasksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    navigator: Navigator,
    tasksViewModel: TasksViewModel = hiltViewModel()
) {
    var confirmationDeleteAllTasksListDialogState by rememberSaveable { mutableStateOf(false) }
    var mainDropDownMenuState by rememberSaveable { mutableStateOf(false) }
    var confirmationDeleteCurrentTasksListDialogState by rememberSaveable { mutableStateOf(false) }
    var currentTaskId: String? by rememberSaveable { mutableStateOf(null) }

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
                            // delete all button
                            DropdownMenuItem(
                                onClick = {
                                    mainDropDownMenuState = false // close dropdown menu
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
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigator.navigateTo(NavigationRoutes.TasksListCreationScreen.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_add_24),
                    contentDescription = null
                )
            }
        }
    ) { innerPadding ->
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
                            tasksViewModel.deleteAllData()
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
                    ) {
                        Text(text = "no")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    SquaredUiButton(
                        onClick = {
                            currentTaskId?.let { tasksViewModel.deleteAllTasksById(it) }
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
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
                        onClick = {
                            val arguments = "/${tasks.id}/${tasks.tasksId}"
                            navigator.navigateTo(NavigationRoutes.TasksListViewScreen.route + arguments)
                        },
                        deleteAllTasksById = {
                            currentTaskId = tasks.tasksId
                            confirmationDeleteCurrentTasksListDialogState = true
                        }
                    )
                }
            }
        }
    }
}