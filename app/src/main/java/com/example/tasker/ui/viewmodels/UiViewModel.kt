package com.example.tasker.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tasker.ui.viewmodels.data_models.AddedTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class UiViewModel : ViewModel() {
    private val _addedTasks = MutableStateFlow<List<AddedTask>>(emptyList())
    val addedTasks = _addedTasks.asStateFlow()
    var taskName by mutableStateOf("")
    var taskDescription by mutableStateOf("")

    var tasksListName by mutableStateOf("")
    var tasksListDescription by mutableStateOf("")
    var currentLazyListPosition by mutableIntStateOf(0)
    var confirmationDeleteAllTasksListDialogState by mutableStateOf(false)
    var confirmationDeleteCurrentTasksListDialogState by mutableStateOf(false)
    var editTaskDialogState by mutableStateOf(false)
    var editTaskContentState by mutableStateOf("")
    var editTaskDescriptionState by mutableStateOf("")

    fun addTaskToList(content: String, description: String?) {
        _addedTasks.value += AddedTask(
            taskId = UUID.randomUUID().toString(),
            content = content,
            description = description
        )
    }
    fun clearTasksList() { _addedTasks.value = emptyList() }
    fun updateTaskName(text: String) { taskName = text }
    fun updateTaskDescription(text: String) { taskDescription = text }

    fun updateTasksListName(text: String) { tasksListName = text }
    fun updateTasksListDescription(text: String) { tasksListDescription = text }
    fun updateCurrentLazyListPosition(position: Int) { currentLazyListPosition = position }
    fun updateConfirmationDeleteAllTasksListDialogState(state: Boolean) { confirmationDeleteAllTasksListDialogState = state }
    fun updateConfirmationDeleteCurrentTasksListDialogState(state: Boolean) { confirmationDeleteCurrentTasksListDialogState = state }
    fun updateEditTaskDialogState(state: Boolean) { editTaskDialogState = state }
    fun updateEditTaskContentState(text: String) { editTaskContentState = text }
    fun updateEditTaskDescriptionState(text: String) { editTaskDescriptionState = text }
}