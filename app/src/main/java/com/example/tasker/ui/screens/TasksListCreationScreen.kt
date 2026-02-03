package com.example.tasker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tasker.R
import com.example.tasker.ui.components.ProtoTaskUiItem
import com.example.tasker.ui.components.SquaredUiButton
import com.example.tasker.ui.components.TextUiField
import com.example.tasker.ui.navigation.NavigationRoutes
import com.example.tasker.ui.navigation.Navigator
import com.example.tasker.ui.utils.Toaster
import com.example.tasker.ui.viewmodels.data_models.AddedTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private fun checkData(name: String, tasks: List<AddedTask>) = name.isNotEmpty() && tasks.isNotEmpty()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksListCreationAppScreen(
    toaster: Toaster,
    navigator: Navigator,
    tasksListName: String,
    updateTasksListName: (String) -> Unit,
    tasksListDescription: String,
    updateTasksListDescription: (String) -> Unit,
    task: String,
    updateTask: (String) -> Unit,
    taskDescription: String,
    updateTaskDescription: (String) -> Unit,
    addedTasksList: List<AddedTask>,
    addTaskToAddedTasksList: (content: String, description: String?) -> Unit,
    createTasksList: () -> Unit,
    deleteTask: (String) -> Unit
) {
    var coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateTo(NavigationRoutes.MainScreen.route) }) {
                        Icon(
                            painter = painterResource(R.drawable.outline_arrow_back_24),
                            contentDescription = null
                        )
                    }
                },
                title = { Text(text = "Create tasks list") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // tasks list name input field
            TextUiField(
                value = tasksListName,
                onValueChange = { newValue -> updateTasksListName(newValue) },
                placeholder = "Enter your tasks list name..."
            )

            // tasks list description input field
            TextUiField(
                value = tasksListDescription,
                onValueChange = { newValue -> updateTasksListDescription(newValue) },
                placeholder = "Enter your tasks list description... (optional)"
            )

            // added tasks view
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    itemsIndexed(
                        items = addedTasksList,
                        key = { index, task -> task.id }
                    ) { index, task ->
                        ProtoTaskUiItem(
                            number = index + 1,
                            task = task.content,
                            description = task.description,
                            deleteThis = { deleteTask(task.taskId) }
                        )

                        if (index < addedTasksList.lastIndex) HorizontalDivider()
                    }
                }

                // task creation panel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        TextUiField(
                            value = task,
                            onValueChange = { newValue -> updateTask(newValue) },
                            placeholder = "Enter your task..."
                        )

                        TextUiField(
                            value = taskDescription,
                            onValueChange = { newValue -> updateTaskDescription(newValue) },
                            placeholder = "Enter your task description... (optional)"
                        )
                    }

                    Button(
                        onClick = {
                            addTaskToAddedTasksList(task, if (taskDescription.isNotEmpty()) taskDescription else null)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .size(40.dp),
                        shape = CircleShape
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_add_24),
                            contentDescription = null
                        )
                    }
                }
            }

            SquaredUiButton(
                onClick = {
                    if (checkData(tasksListName, addedTasksList))
                        coroutineScope.launch {
                            withContext(Dispatchers.IO) {
                                createTasksList()
                                withContext(Dispatchers.Main) {
                                    toaster.showToast("Tasks list created!") // show toast message
                                    navigator.navigateTo(NavigationRoutes.MainScreen.route) // navigate to main screen
                                }
                            }
                        }
                    else toaster.showToast("⚠️Little data!📃")
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