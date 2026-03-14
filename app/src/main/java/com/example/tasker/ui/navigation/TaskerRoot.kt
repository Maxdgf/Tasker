package com.example.tasker.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tasker.ui.screens.MainAppScreen
import com.example.tasker.ui.screens.TasksListCreationAppScreen
import com.example.tasker.ui.screens.TasksListViewAppScreen
import com.example.tasker.ui.utils.Toaster
import com.example.tasker.ui.viewmodels.TasksViewModel
import com.example.tasker.ui.viewmodels.UiViewModel
import java.util.UUID

@Composable
fun TaskerAppRoot(
    uiViewModel: UiViewModel = viewModel(),
    tasksViewModel: TasksViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val toaster = remember { Toaster(context) }
    val navController = rememberNavController()
    val navigator = remember { Navigator(navController) }
    val tasksListsLazyState = rememberLazyListState()

    val allTasksList by tasksViewModel.allTasksLists.collectAsState()
    val addedTasksList by uiViewModel.addedTasks.collectAsState()
    val allTasksById by tasksViewModel.allTasksById.collectAsState()
    val currentTaskId by tasksViewModel.taskId.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = NavigationRoutes.MainScreen.route
        ) {
            // main screen
            composable(route = NavigationRoutes.MainScreen.route) {
                MainAppScreen(
                    allTasksList = allTasksList,
                    navigator = navigator,
                    setTaskId = tasksViewModel::setTaskId,
                    lazyListState = tasksListsLazyState,
                    deleteAllTasksLists = tasksViewModel::deleteAllData,
                    confirmationDeleteAllTasksListDialogState = uiViewModel.confirmationDeleteAllTasksListDialogState,
                    updateConfirmationDeleteAllTasksListDialogState = uiViewModel::updateConfirmationDeleteAllTasksListDialogState,
                    deleteAllTasksById = tasksViewModel::deleteAllTasksById,
                    taskId = currentTaskId,
                    confirmationDeleteCurrentTasksListDialogState = uiViewModel.confirmationDeleteCurrentTasksListDialogState,
                    updateConfirmationDeleteCurrentTasksListDialogState = uiViewModel::updateConfirmationDeleteCurrentTasksListDialogState,
                    mainDropDownMenuState = uiViewModel.mainDropdownMenuState,
                    updateMainDropdownMenuState = uiViewModel::updateDropdownMenuState,
                )
            }

            // tasks list creation screen
            composable(route = NavigationRoutes.TasksListCreationScreen.route) {
                TasksListCreationAppScreen(
                    toaster = toaster,
                    navigator = navigator,
                    tasksListName = uiViewModel.tasksListName,
                    updateTasksListName = uiViewModel::updateTasksListName,
                    tasksListDescription = uiViewModel.tasksListDescription,
                    updateTasksListDescription = uiViewModel::updateTasksListDescription,
                    task = uiViewModel.taskName,
                    updateTask = uiViewModel::updateTaskName,
                    taskDescription = uiViewModel.taskDescription,
                    updateTaskDescription = uiViewModel::updateTaskDescription,
                    addedTasksList = addedTasksList,
                    addTaskToAddedTasksList = uiViewModel::addTaskToList,
                    createTasksList = {
                        val taskId = UUID.randomUUID().toString() // generated task id

                        // add tasks
                        addedTasksList.forEach { task ->
                            tasksViewModel.addTask(taskId, task.content, task.description)
                        }

                        // add tasks list header
                        tasksViewModel.addTasksList(
                            taskId,
                            uiViewModel.tasksListName,
                            if (uiViewModel.tasksListDescription.isNotEmpty()) uiViewModel.tasksListDescription else null,
                            addedTasksList.count()
                        )
                    },
                    deleteTask = uiViewModel::deleteTaskById,
                    clearTasksList = uiViewModel::clearTasksList
                )
            }

            // tasks list view screen
            composable(
                route = "${NavigationRoutes.TasksListViewScreen.route}/{tasksListName}?tasksListDescription={tasksListDescription}",
                arguments = listOf(
                    navArgument("tasksListName") { type = NavType.StringType }, // tasks list name
                    navArgument("tasksListDescription") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    } // tasks list description (nullable - optional)
                )
            ) { navBackStackEntry ->
                val name = navBackStackEntry.arguments?.getString("tasksListName") // get name argument
                val description = navBackStackEntry.arguments?.getString("tasksListDescription") // get description argument
                
                TasksListViewAppScreen(
                    name = name ?: "Tasks",
                    description = description,
                    navigator = navigator,
                    tasksList = allTasksById,
                    onTaskStateChanged = tasksViewModel::setTaskStateById,
                    setCompletedTasksCountById = tasksViewModel::setCompletedTasksCountById,
                    manageTasksListCompletionStateById = tasksViewModel::manageTasksListCompletionStateById,
                    updateTaskByIdDialogState = uiViewModel.editTaskDialogState,
                    updateTaskByIdDialog = uiViewModel::updateEditTaskDialogState,
                    updateTaskById = tasksViewModel::updateTaskById,
                    editContent = uiViewModel.editTaskContentState,
                    updateEditContent = uiViewModel::updateEditTaskContentState,
                    editDescription = uiViewModel.editTaskDescriptionState,
                    updateEditDescription = uiViewModel::updateEditTaskDescriptionState,
                    updateEditTaskId = tasksViewModel::setEditTaskId,
                    updateTaskId = tasksViewModel::setTaskId
                )
            }
        }
    }
}