package com.example.tasker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasker.databases.tasks_database.entities.TaskEntity
import com.example.tasker.databases.tasks_database.entities.TasksListEntity
import com.example.tasker.databases.tasks_database.repository.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(private val tasksRepository: TasksRepository) : ViewModel() {
    private val _taskId = MutableStateFlow<String?>(null)
    val taskId = _taskId.asStateFlow()

    val allTasksLists = tasksRepository.getAllTasks().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val allTasksById = _taskId
        .filterNotNull()
        .flatMapLatest { param ->
            tasksRepository.getAllTasksById(param)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )

    fun setTaskId(id: String) { _taskId.value = id }

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

    fun setCompletedTasksCountById(count: Int) {
        viewModelScope.launch {
            _taskId.value?.let {
                tasksRepository.setCompletedTasksCountById(count, it)
            }
        }
    }

    fun manageTasksListCompletionStateById(state: Boolean) {
        viewModelScope.launch {
            _taskId.value?.let {
                tasksRepository.manageTasksListCompletionStateById(state, it)
            }
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            tasksRepository.deleteAllData()
        }
    }

    fun deleteAllTasksById() {
        viewModelScope.launch {
            _taskId.value?.let {
                tasksRepository.deleteAllTasksById(it)
            }
        }
    }
}