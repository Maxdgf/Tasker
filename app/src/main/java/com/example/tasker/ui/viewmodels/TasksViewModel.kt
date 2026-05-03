package com.example.tasker.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasker.databases.tasks_database.entities.TaskEntity
import com.example.tasker.databases.tasks_database.entities.TasksListEntity
import com.example.tasker.databases.tasks_database.repository.TasksRepository
import com.example.tasker.ui.states.TasksListDataResult
import com.example.tasker.ui.states.TasksListResult
import com.example.tasker.ui.states.TasksResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val CURRENT_TASK_ID_KEY = "currentTaskId"
    }

    private val _taskId: StateFlow<String?> = savedStateHandle.getStateFlow(CURRENT_TASK_ID_KEY, null)

    val allTasksLists = tasksRepository.getAllTasks()
        .map<List<TasksListEntity>, TasksListResult> { list ->
            // map tasks list state
            if (list.isNotEmpty())
                TasksListResult.ContentList(list)
            else
                TasksListResult.EmptyList
        }
        .catch { exception ->
            // emit exception state
            emit(
                TasksListResult.Exception(
                    exception.message ?:
                    "An unexpected error occurred, the task lists was not loaded."
                )
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            TasksListResult.Loading // loading (initial state)
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksListById = _taskId
        .filterNotNull()
        .flatMapLatest { id ->
            flow<TasksListDataResult> {
                tasksRepository.getTasksListById(id)
                    .onStart { emit(TasksListDataResult.Loading) }
                    .collect { tasksList ->
                        // null check
                        if (tasksList != null)
                            emit(TasksListDataResult.ListData(tasksList))
                        else
                            emit(TasksListDataResult.NotFound)
                    }
            }
        }
        .catch { exception ->
            // emit exception state
            emit(
                TasksListDataResult.Exception(
                    exception.message ?:
                    "An unexpected error occurred, the tasks list data was not loaded."
                )
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            TasksListDataResult.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val allTasksById = _taskId
        .filterNotNull()
        .flatMapLatest { taskId ->
            flow<TasksResult> {
                tasksRepository.getAllTasksById(taskId)
                    .onStart { emit(TasksResult.Loading) }
                    .collect { tasks ->
                        emit(TasksResult.ContentList(tasks))
                    }
            }
        }
        .catch { exception ->
            // emit exception state
            emit(
                TasksResult.Exception(
                    exception.message ?:
                    "An unexpected error occurred, the tasks was not loaded."
                )
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            TasksResult.Loading
        )

    fun setTasks(id: String) { savedStateHandle[CURRENT_TASK_ID_KEY] = id }

    fun addTasksList(id: String, name: String, description: String?, tasksCount: Int) =
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

    fun addTask(id: String, content: String, description: String?) =
        viewModelScope.launch {
            tasksRepository.addTask(
                TaskEntity(
                    taskId = id,
                    content = content,
                    description = description
                )
            )
        }

    fun setTaskStateById(state: Boolean, id: Long) =
        viewModelScope.launch {
            tasksRepository.setStateToTaskById(state, id)
        }

    fun setCompletedTasksCountById(id: String, count: Int) =
        viewModelScope.launch {
            tasksRepository.setCompletedTasksCountById(count, id)
        }

    fun manageTasksListCompletionStateById(id: String, state: Boolean) =
        viewModelScope.launch {
            tasksRepository.manageTasksListCompletionStateById(state, id)
        }

    fun updateTaskById(id: Long, content: String, description: String?) =
        viewModelScope.launch {
            tasksRepository.updateTaskById(content, description, id)
        }

    fun setAllTasksCountById(id: String, count: Int) =
        viewModelScope.launch {
            tasksRepository.setAllTasksCountById(id, count)
        }

    fun deleteTaskById(id: Long) =
        viewModelScope.launch {
            tasksRepository.deleteTaskById(id)
        }

    fun deleteAllData() =
        viewModelScope.launch {
            tasksRepository.deleteAllData()
        }

    fun deleteAllTasksById(id: String) =
        viewModelScope.launch {
            tasksRepository.deleteAllTasksById(id)
        }
}