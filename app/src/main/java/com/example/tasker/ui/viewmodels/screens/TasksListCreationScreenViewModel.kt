package com.example.tasker.ui.viewmodels.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class AddedTask(
    val id: Long,
    val content: String,
    val description: String? = null
)

class TasksListCreationScreenViewModel : ViewModel() {
    var tListName by mutableStateOf("")
    var tListDescription by  mutableStateOf("")
    var taskText by mutableStateOf("")
    var taskDesc by mutableStateOf("")

    var addedTasks by mutableStateOf<List<AddedTask>>(emptyList())

    fun setListName(name: String) { tListName = name }
    fun setListDescription(description: String) { tListDescription = description }

    fun setTask(text: String) { taskText = text }
    fun setTaskDescription(description: String) { taskDesc = description }

    fun taskNotEmpty() = taskText.isNotEmpty()
    fun taskDescriptionNotEmpty() = taskDesc.isNotEmpty()

    fun addTask(task: AddedTask) {
        addedTasks = addedTasks + task
    }

    fun deleteTaskById(id: Long) {
        addedTasks = addedTasks.filterNot { task -> task.id == id }
    }

    fun clearAllTasks() {
        addedTasks = emptyList()
    }
}