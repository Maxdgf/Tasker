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
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

import com.example.tasker.R
import com.example.tasker.databases.tasks_database.entities.TaskEntity
import com.example.tasker.ui.components.ActionUiDialog
import com.example.tasker.ui.components.LoadingUiBlock
import com.example.tasker.ui.components.NoDataUiDescriptionBlock
import com.example.tasker.ui.components.SquaredUiButton
import com.example.tasker.ui.components.TaskUiItem
import com.example.tasker.ui.components.TextUiField
import com.example.tasker.ui.screens.navigation.NavigationRoutes
import com.example.tasker.ui.states.TasksListDataResult
import com.example.tasker.ui.states.TasksResult
import com.example.tasker.ui.viewmodels.TasksViewModel
import com.example.tasker.ui.viewmodels.screens.TasksListViewScreenViewModel

@Composable
private fun TasksListView(
    paddingValues: PaddingValues,
    taskId: String?,
    tasksList: List<TaskEntity>,
    onTaskStateChanged: (Boolean, Long) -> Unit,
    onDeleteTaskById: (Long) -> Unit,
    onSetAllTasksCountById: (String, Int) -> Unit,
    onUpdateTaskById: (Long, String, String?) -> Unit,
    onSetCompletedTasksCountById: (String, Int) -> Unit,
    onManageTasksListCompletionStateById: (String, Boolean) -> Unit
) {
    val tasksState: TasksListViewScreenViewModel = viewModel()

    var currentId: Long? by rememberSaveable { mutableStateOf(0L) }
    var currentTaskId: String? by rememberSaveable { mutableStateOf(null) }
    var updateTaskByIdDialogState by rememberSaveable { mutableStateOf(false) }
    var deleteTaskByIdDialogState by rememberSaveable { mutableStateOf(false) }
    var isScreenHasJustStarted by rememberSaveable { mutableStateOf(false) }

    // update completed tasks count when tasks list changing
    LaunchedEffect(tasksList) {
        // check, is screen has just started
        // ------------------------------------------------------------------
        // This boolean flag prevents the task list data from being
        // overwritten in the database when the LaunchedEffect
        // is initially launched (the first time the composition is entered).
        if (!isScreenHasJustStarted) {
            val id = taskId ?: return@LaunchedEffect

            val allCompletedTasksCount = tasksList.count { task -> task.isCompleted } // count completed tasks
            onSetCompletedTasksCountById(
                id,
                allCompletedTasksCount
            ) // set completed tasks count to state

            if (allCompletedTasksCount == tasksList.size)
                onManageTasksListCompletionStateById(id, true)
            else
                onManageTasksListCompletionStateById(id, false)

            delay(10)
        } else isScreenHasJustStarted = false
    }

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
                    currentTaskId?.let { onSetAllTasksCountById(it, tasksList.size - 1) }
                    currentId?.let { onDeleteTaskById(it) }

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
                value = tasksState.text,
                onValueChange = { newValue -> tasksState.setContent(newValue) },
                placeholder = "Edit your task content..."
            )

            TextUiField(
                value = tasksState.desc,
                onValueChange = { newValue -> tasksState.setDescription(newValue) },
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
                    val id = currentId ?: return@SquaredUiButton

                    if (tasksState.contentNotEmpty()) {
                        onUpdateTaskById(
                            id,
                            tasksState.text,
                            if (tasksState.descriptionNotEmpty()) tasksState.desc
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
            .padding(paddingValues),
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
                    currentTaskId = task.taskId
                    currentId = task.id

                    tasksState.setContent(task.content)
                    tasksState.setDescription(task.description ?: "")

                    updateTaskByIdDialogState = true
                },
                onDelete = {
                    currentTaskId = task.taskId
                    currentId = task.id

                    deleteTaskByIdDialogState = true
                },
                description = task.description
            )

            if (index < tasksList.lastIndex) HorizontalDivider()
        }
    }
}

@Composable
private fun TasksListDataField(
    name: String,
    description: String?
) {
    Column {
        // name
        Text(
            text = name,
            modifier = Modifier.basicMarquee(Int.MAX_VALUE),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )

        // description (optional)
        description?.let {
            Text(
                text = it,
                fontWeight = FontWeight.Light,
                modifier = Modifier.basicMarquee(Int.MAX_VALUE),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksListViewAppScreen(
    onNavigateTo: (String) -> Unit,
    taskId: String?,
    tasksViewModel: TasksViewModel
) {
    LaunchedEffect(Unit) {
        taskId?.let {
            tasksViewModel.setTasks(it)
        }
    }

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
                title = {
                    val tasksListHeader by tasksViewModel.tasksListById.collectAsState()

                    when (val data = tasksListHeader) {
                        is TasksListDataResult.ListData ->
                            TasksListDataField(
                                name = data.tasksListEntity.name,
                                description = data.tasksListEntity.description
                            )
                        is TasksListDataResult.Loading -> Text(text = "loading...")
                        else -> {}
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->
        val tasksList by tasksViewModel.allTasksById.collectAsState()

        when (val list = tasksList) {
            is TasksResult.ContentList ->
                TasksListView(
                    paddingValues = innerPadding,
                    taskId = taskId,
                    tasksList = list.tasks,
                    onTaskStateChanged = tasksViewModel::setTaskStateById,
                    onDeleteTaskById = tasksViewModel::deleteTaskById,
                    onSetAllTasksCountById = tasksViewModel::setAllTasksCountById,
                    onUpdateTaskById = tasksViewModel::updateTaskById,
                    onSetCompletedTasksCountById = tasksViewModel::setCompletedTasksCountById,
                    onManageTasksListCompletionStateById = tasksViewModel::manageTasksListCompletionStateById
                )
            is TasksResult.Exception ->
                NoDataUiDescriptionBlock(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    description = list.message
                )
            TasksResult.Loading ->
                LoadingUiBlock(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    description = "Loading tasks, please wait."
                )
        }
    }
}