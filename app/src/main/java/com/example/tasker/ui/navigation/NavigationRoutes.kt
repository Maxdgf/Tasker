package com.example.tasker.ui.navigation

sealed class NavigationRoutes(val route: String) {
    object MainScreen: NavigationRoutes("main_screen")
    object TasksListCreationScreen: NavigationRoutes("tasks_list_creation_screen")
    object TasksListViewScreen: NavigationRoutes("tasks_list_view_screen")
}