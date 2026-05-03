package com.example.tasker.ui.states

import com.example.tasker.databases.tasks_database.entities.TasksListEntity

sealed class TasksListResult {
    data class ContentList(val taskLists: List<TasksListEntity>) : TasksListResult() // loaded successfully
    data class Exception(val message: String) : TasksListResult() // loaded with exception
    object EmptyList : TasksListResult() // empty list
    object Loading : TasksListResult() // loading now
}