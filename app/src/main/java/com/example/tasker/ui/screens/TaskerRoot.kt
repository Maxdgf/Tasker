package com.example.tasker.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tasker.ui.navigation.NavigationRoutes
import com.example.tasker.ui.navigation.Navigator
import com.example.tasker.ui.viewmodels.TasksViewModel

@Composable
fun TaskerAppRoot(
    tasksViewModel: TasksViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navigator = remember { Navigator(navController) }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = NavigationRoutes.MainScreen.route
        ) {
            // main screen
            composable(route = NavigationRoutes.MainScreen.route) {
                MainAppScreen(navigator = navigator)
            }

            // tasks list creation screen
            composable(route = NavigationRoutes.TasksListCreationScreen.route) {
                TasksListCreationAppScreen(navigator = navigator)
            }

            // tasks list view screen
            composable(
                route = "${NavigationRoutes.TasksListViewScreen.route}/{tasksListId}/{taskId}",
                arguments = listOf(
                    navArgument("tasksListId") { type = NavType.LongType },
                    navArgument("taskId") { type = NavType.StringType }
                )
            ) { navBackStackEntry ->
                val tasksListId = navBackStackEntry.arguments?.getLong("tasksListId")
                val taskId = navBackStackEntry.arguments?.getString("taskId")
                
                TasksListViewAppScreen(
                    navigator = navigator,
                    onTaskStateChanged = tasksViewModel::setTaskStateById,
                    tasksListId = tasksListId,
                    taskId = taskId
                )
            }
        }
    }
}