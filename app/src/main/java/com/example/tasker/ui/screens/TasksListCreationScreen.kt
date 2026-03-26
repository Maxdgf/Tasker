package com.example.tasker.ui.screens

import android.os.Parcelable
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import com.example.tasker.R
import com.example.tasker.ui.components.HelpUiInfoBlock
import com.example.tasker.ui.components.NoDataUiDescriptionBlock
import com.example.tasker.ui.components.ProtoTaskUiItem
import com.example.tasker.ui.components.SquaredUiButton
import com.example.tasker.ui.components.TextUiField
import com.example.tasker.ui.navigation.NavigationRoutes
import com.example.tasker.ui.navigation.Navigator
import com.example.tasker.ui.viewmodels.TasksViewModel
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class AddedTask(
    val id: String = UUID.randomUUID().toString(),
    val taskId: String,
    val content: String,
    val description: String? = null
) : Parcelable

/**
 * Creates tasks list creation screen.
 * @param navigator utility for screen navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksListCreationAppScreen(
    navigator: Navigator,
    tasksViewModel: TasksViewModel = hiltViewModel()
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

    // screen states
    var tasksListName by rememberSaveable { mutableStateOf("") }
    var tasksListDescription by rememberSaveable { mutableStateOf("") }
    var task by rememberSaveable { mutableStateOf("") }
    var taskDescription by rememberSaveable { mutableStateOf("") }
    val addedTasksList = rememberSaveable { mutableStateListOf<AddedTask>() }
    val taskId by rememberSaveable { mutableStateOf(UUID.randomUUID().toString()) }

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
                    Text(
                        text = "Create tasks list",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->
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
                HelpUiInfoBlock(text = "Type name and description(optional) of your tasks list.")

                // tasks list name input field
                TextUiField(
                    value = tasksListName,
                    onValueChange = { newValue -> tasksListName = newValue },
                    placeholder = "Enter your tasks list name..."
                )

                Spacer(modifier = Modifier.height(10.dp))

                // tasks list description input field
                TextUiField(
                    value = tasksListDescription,
                    onValueChange = { newValue -> tasksListDescription = newValue },
                    placeholder = "Enter your tasks list description... (optional)"
                )

                Spacer(modifier = Modifier.height(50.dp))

                HelpUiInfoBlock(text = "Add tasks here.")

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
                        if (addedTasksList.isNotEmpty())
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
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
                                        deleteThis = {
                                            addedTasksList.removeIf { currentTask -> currentTask.id == task.id }
                                        }
                                    )

                                    if (index < addedTasksList.lastIndex) HorizontalDivider()
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
                                value = task,
                                onValueChange = { newValue -> task = newValue },
                                placeholder = "Enter your task..."
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            TextUiField(
                                value = taskDescription,
                                onValueChange = { newValue -> taskDescription = newValue },
                                placeholder = "Enter your task description... (optional)"
                            )
                        }

                        Column(
                            modifier = Modifier.weight(0.2f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (task.isNotEmpty()) {
                                        addedTasksList.add(
                                            AddedTask(
                                                taskId = taskId,
                                                content = task,
                                                description =
                                                    if (taskDescription.isNotEmpty()) taskDescription
                                                    else null
                                            )
                                        )

                                        task = ""
                                        taskDescription = ""
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
                                onClick = { addedTasksList.clear() },
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
                    if (checkData(tasksListName, addedTasksList)) {
                        // add tasks
                        addedTasksList.forEach { task ->
                            tasksViewModel.addTask(taskId, task.content, task.description)
                        }

                        // add tasks list header
                        tasksViewModel.addTasksList(
                            taskId,
                            tasksListName,
                            if (tasksListDescription.isNotEmpty()) tasksListDescription else null,
                            addedTasksList.count()
                        )

                        navigator.navigateTo(NavigationRoutes.MainScreen.route) // navigate to main screen
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