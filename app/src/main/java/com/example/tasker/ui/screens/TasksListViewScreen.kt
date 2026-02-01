package com.example.tasker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tasker.R
import com.example.tasker.databases.tasks_database.entities.TaskEntity
import com.example.tasker.ui.components.TaskUiItem
import com.example.tasker.ui.navigation.NavigationRoutes
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksListViewAppScreen(
    navController: NavController,
    tasksList: List<TaskEntity>,
    onTaskStateChanged: (state: Boolean, id: Long) -> Unit,
    setCompletedTasksCountById: (count: Int) -> Unit,
    manageTasksListCompletionStateById: (Boolean) -> Unit,
    updateTaskByIdDialogState: Boolean,
    updateTaskByIdDialog: (Boolean) -> Unit,
    updateTaskById: (content: String, description: String?, id: Long) -> Unit,
    editContent: String,
    updateEditContent: (String) -> Unit,
    editDescription: String,
    updateEditDescription: (String) -> Unit,
) {
    // update completed tasks count when tasks list changing
    LaunchedEffect(tasksList) {
        val allCompletedTasksCount = tasksList.count { task -> task.isCompleted }
        setCompletedTasksCountById(allCompletedTasksCount)

        if (allCompletedTasksCount == tasksList.count()) manageTasksListCompletionStateById(true)
        else manageTasksListCompletionStateById(false)

        delay(10)
    }

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
                title = { Text(text = "Tasks") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            itemsIndexed(items = tasksList) { index, task ->
                TaskUiItem(
                    id = task.id,
                    number = index + 1,
                    task = task.content,
                    description = task.description,
                    state = task.isCompleted,
                    onStateChanged = onTaskStateChanged
                )

                if (index < tasksList.lastIndex) HorizontalDivider()
            }
        }
    }
}