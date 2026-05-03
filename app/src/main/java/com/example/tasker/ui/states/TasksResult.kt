package com.example.tasker.ui.states

import com.example.tasker.databases.tasks_database.entities.TaskEntity

sealed class TasksResult {
    data class ContentList(val tasks: List<TaskEntity>) : TasksResult()
    data class Exception(val message: String) : TasksResult()
    object Loading : TasksResult()
}