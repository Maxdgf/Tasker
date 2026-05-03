package com.example.tasker.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.tasker.R
import com.example.tasker.ui.components.HelpUiInfoBlock
import com.example.tasker.ui.components.NoDataUiDescriptionBlock
import com.example.tasker.ui.components.ProtoTaskUiItem
import com.example.tasker.ui.components.SquaredUiButton
import com.example.tasker.ui.components.TextUiField
import com.example.tasker.ui.screens.navigation.NavigationRoutes
import com.example.tasker.ui.viewmodels.TasksViewModel
import com.example.tasker.ui.viewmodels.screens.AddedTask
import com.example.tasker.ui.viewmodels.screens.TasksListCreationScreenViewModel
import java.util.UUID

/**
 * Creates tasks list creation screen.
 * @param onNavigateTo navigate to specific screen function.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksListCreationAppScreen(
    onNavigateTo: (String) -> Unit,
    tasksViewModel: TasksViewModel
) {
    /**
     * Checks input user data.
     * @return bool state.
     */
    val checkData: (String, List<AddedTask>) -> Boolean = remember {
        { name, tasks ->
            name.isNotEmpty() && tasks.size > 1
        }
    }

    var hintsVisibilityState by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigateTo(NavigationRoutes.MainScreen.route)
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_arrow_back_24),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { hintsVisibilityState = !hintsVisibilityState }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_info_outline_24),
                            contentDescription = null,
                            tint = if (hintsVisibilityState) Color.Green else Color.Unspecified
                        )
                    }
                },
                title = {
                    Text(
                        text = "Create tasks list",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->
        val listState: TasksListCreationScreenViewModel = viewModel()
        val scrollState = rememberScrollState()

        Column(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        top = 10.dp,
                        start = 5.dp,
                        end = 5.dp
                    )
                    .verticalScroll(scrollState)
            ) {
                HelpUiInfoBlock(
                    text = "Type name and description(optional) of your tasks list.",
                    visibility = hintsVisibilityState
                )

                // tasks list name input field
                TextUiField(
                    value = listState.tListName,
                    onValueChange = { newValue -> listState.setListName(newValue) },
                    placeholder = "Enter your tasks list name..."
                )

                Spacer(modifier = Modifier.height(10.dp))

                // tasks list description input field
                TextUiField(
                    value = listState.tListDescription,
                    onValueChange = { newValue -> listState.setListDescription(newValue) },
                    placeholder = "Enter your tasks list description... (optional)"
                )

                Spacer(modifier = Modifier.height(50.dp))

                HelpUiInfoBlock(
                    text = "Add tasks here.",
                    visibility = hintsVisibilityState
                )

                // added tasks view
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(10.dp)
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        val addedTasks = listState.addedTasks
                        if (addedTasks.isNotEmpty())
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentPadding = PaddingValues(horizontal = 5.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                itemsIndexed(
                                    items = addedTasks,
                                    key = { index, task -> task.id }
                                ) { index, task ->
                                    ProtoTaskUiItem(
                                        number = index + 1,
                                        task = task.content,
                                        description = task.description,
                                        deleteThis = {
                                            listState.deleteTaskById(task.id)
                                        }
                                    )

                                    if (index < addedTasks.lastIndex) HorizontalDivider()
                                }
                            }
                        else NoDataUiDescriptionBlock(
                            modifier = Modifier.fillMaxSize(),
                            description = "no tasks added :("
                        )
                    }

                    // task creation panel
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(0.8f)) {
                            TextUiField(
                                value = listState.taskText,
                                onValueChange = { newValue -> listState.setTask(newValue) },
                                placeholder = "Enter your task..."
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            TextUiField(
                                value = listState.taskDesc,
                                onValueChange = { newValue -> listState.setTaskDescription(newValue) },
                                placeholder = "Enter your task description... (optional)"
                            )
                        }

                        Column(
                            modifier = Modifier.weight(0.2f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (listState.taskNotEmpty()) {
                                        listState.addTask(
                                            AddedTask(
                                                id = listState.addedTasks.size + 1L,
                                                content = listState.taskText,
                                                description =
                                                    if (listState.taskDescriptionNotEmpty())
                                                        listState.taskDesc
                                                    else null
                                            )
                                        )

                                        listState.setTask("")
                                        listState.setTaskDescription("")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.rounded_add_24),
                                    contentDescription = null
                                )
                            }

                            Button(
                                onClick = { listState.clearAllTasks() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.outline_delete_24),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }

            // create tasks list button
            SquaredUiButton(
                onClick = {
                    val addedTasksList = listState.addedTasks
                    if (checkData(listState.tListName, addedTasksList)) {
                        val taskId = UUID.randomUUID().toString()

                        // add tasks
                        addedTasksList.forEach { task ->
                            tasksViewModel.addTask(taskId, task.content, task.description)
                        }

                        // add tasks list header
                        tasksViewModel.addTasksList(
                            taskId,
                            listState.tListName,
                            if (listState.tListDescription.isNotEmpty())
                                listState.tListDescription
                            else null,
                            addedTasksList.count()
                        )

                        onNavigateTo(NavigationRoutes.MainScreen.route) // navigate to main screen
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) { Text(text = "create tasks list") }
        }
    }
}