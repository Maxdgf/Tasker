package com.example.tasker.ui.screens.navigation

sealed class NavigationRoutes(val route: String) {
    object MainScreen: NavigationRoutes("main_screen") // main screen route
    object TasksListCreationScreen: NavigationRoutes("tasks_list_creation_screen") // tasks list creation screen route
    object TasksListViewScreen: NavigationRoutes("tasks_list_view_screen") // tasks list view screen route
}