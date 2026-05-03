package com.example.tasker.ui.states

import com.example.tasker.databases.tasks_database.entities.TasksListEntity

sealed class TasksListDataResult {
    data class ListData(val tasksListEntity: TasksListEntity) : TasksListDataResult()
    data class Exception(val message: String) : TasksListDataResult()
    object NotFound : TasksListDataResult()
    object Loading : TasksListDataResult()
}