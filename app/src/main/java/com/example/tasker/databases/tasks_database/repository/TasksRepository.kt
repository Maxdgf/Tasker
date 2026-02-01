package com.example.tasker.databases.tasks_database.repository

import com.example.tasker.databases.tasks_database.entities.TaskEntity
import com.example.tasker.databases.tasks_database.entities.TasksListEntity
import kotlinx.coroutines.flow.Flow

interface TasksRepository {
    fun getAllTasks(): Flow<List<TasksListEntity>>
    suspend fun addTasksList(tasksList: TasksListEntity)
    suspend fun addTask(taskEntity: TaskEntity)
    fun getAllTasksById(id: String): Flow<List<TaskEntity>>
    suspend fun setStateToTaskById(state: Boolean, id: Long)
    suspend fun setCompletedTasksCountById(count: Int, id: String)
    suspend fun manageTasksListCompletionStateById(state: Boolean, id: String)
    suspend fun updateTaskById(content: String, description: String?, id: Long)
    suspend fun deleteAllData()
    suspend fun deleteAllTasksById(id: String)
}