package com.example.tasker.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasker.databases.tasks_database.entities.TaskEntity
import com.example.tasker.databases.tasks_database.entities.TasksListEntity
import com.example.tasker.databases.tasks_database.repository.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val CURRENT_TASKS_LIST_ID_KEY = "currentTasksListId"
        private const val CURRENT_TASK_ID_KEY = "currentTaskId"
    }

    private val _taskId: StateFlow<String?> = savedStateHandle.getStateFlow(CURRENT_TASK_ID_KEY, null)
    private val _tasksListId: StateFlow<Long?> = savedStateHandle.getStateFlow(CURRENT_TASKS_LIST_ID_KEY, null)

    val allTasksLists = tasksRepository.getAllTasks().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksListById = _tasksListId
        .filterNotNull()
        .flatMapLatest { id ->
            tasksRepository.getTasksListById(id)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val allTasksById = _taskId
        .filterNotNull()
        .flatMapLatest { param ->
            tasksRepository.getAllTasksById(param)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun setTasks(id: String) { savedStateHandle[CURRENT_TASK_ID_KEY] = id }
    fun setTasksList(id: Long) { savedStateHandle[CURRENT_TASKS_LIST_ID_KEY] = id }

    fun addTasksList(id: String, name: String, description: String?, tasksCount: Int) {
        viewModelScope.launch {
            tasksRepository.addTasksList(
                TasksListEntity(
                    tasksId = id,
                    name = name,
                    description = description,
                    tasksCount = tasksCount
                )
            )
        }
    }

    fun addTask(id: String, content: String, description: String?) {
        viewModelScope.launch {
            tasksRepository.addTask(
                TaskEntity(
                    taskId = id,
                    content = content,
                    description = description
                )
            )
        }
    }

    fun setTaskStateById(state: Boolean, id: Long) {
        viewModelScope.launch {
            tasksRepository.setStateToTaskById(state, id)
        }
    }

    fun setCompletedTasksCountById(id: String, count: Int) {
        viewModelScope.launch {
            tasksRepository.setCompletedTasksCountById(count, id)
        }
    }

    fun manageTasksListCompletionStateById(id: String, state: Boolean) {
        viewModelScope.launch {
            tasksRepository.manageTasksListCompletionStateById(state, id)
        }
    }

    fun updateTaskById(id: Long, content: String, description: String?) {
        viewModelScope.launch {
            tasksRepository.updateTaskById(content, description, id)
        }
    }

    fun setAllTasksCountById(id: Long, count: Int) {
        viewModelScope.launch {
            tasksRepository.setAllTasksCountById(id, count)
        }
    }

    fun deleteTaskById(id: Long) {
        viewModelScope.launch {
            tasksRepository.deleteTaskById(id)
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            tasksRepository.deleteAllData()
        }
    }

    fun deleteAllTasksById(id: String) {
        viewModelScope.launch {
            tasksRepository.deleteAllTasksById(id)
        }
    }
}