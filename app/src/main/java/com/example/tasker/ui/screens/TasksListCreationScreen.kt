package com.example.tasker.ui.screens

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tasker.R
import com.example.tasker.ui.components.TextUiField
import com.example.tasker.ui.navigation.NavigationRoutes
import com.example.tasker.ui.utils.Toaster
import com.example.tasker.ui.viewmodels.data_models.AddedTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksListCreationAppScreen(
    toaster: Toaster,
    navController: NavController,
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
    createTasksList: () -> Unit
) {
    var coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(NavigationRoutes.MainScreen.route) }) {
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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {})
                        ) {
                            Text(text = task.content)
                            task.description?.let { Text(text = it) }
                        }

                        if (index < addedTasksList.lastIndex) HorizontalDivider()
                    }
                }

                // task creation panel
                Column(modifier = Modifier.fillMaxWidth()) {
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

                    Button(
                        onClick = { addTaskToAddedTasksList(task, if (taskDescription.isNotEmpty()) taskDescription else null) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "add task")
                    }
                }
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            createTasksList()
                            withContext(Dispatchers.Main) {
                                toaster.showToast("Tasks list created!") // show toast message
                                navController.navigate(NavigationRoutes.MainScreen.route) // navigate to main screen
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "create tasks list")
            }
        }
    }
}