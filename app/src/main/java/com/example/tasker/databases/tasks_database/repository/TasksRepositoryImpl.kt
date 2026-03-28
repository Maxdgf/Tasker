package com.example.tasker.databases.tasks_database.repository

import com.example.tasker.databases.tasks_database.TasksDao
import com.example.tasker.databases.tasks_database.entities.TaskEntity
import com.example.tasker.databases.tasks_database.entities.TasksListEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(private val tasksDao: TasksDao) : TasksRepository {
    override fun getAllTasks(): Flow<List<TasksListEntity>> = tasksDao.getAllTasksLists()

    override fun getTasksListById(id: Long): Flow<TasksListEntity> = tasksDao.getTasksListById(id)

    override suspend fun addTasksList(tasksList: TasksListEntity) = tasksDao.addTasksList(tasksList)

    override suspend fun addTask(task: TaskEntity) = tasksDao.addTask(task)

    override fun getAllTasksById(id: String): Flow<List<TaskEntity>> = tasksDao.getAllTasksById(id)

    override suspend fun setStateToTaskById(state: Boolean, id: Long) = tasksDao.setStateToTaskById(state, id)

    override suspend fun setCompletedTasksCountById(count: Int, id: String) = tasksDao.setCompletedTasksCountById(count, id)

    override suspend fun manageTasksListCompletionStateById(
        state: Boolean,
        id: String
    ) = tasksDao.manageTasksListCompletionStateById(state, id)

    override suspend fun updateTaskById(
        content: String,
        description: String?,
        id: Long
    ) = tasksDao.updateTaskById(content, description, id)

    override suspend fun setAllTasksCountById(id: Long, count: Int) = tasksDao.setAllTasksCountById(id, count)

    override suspend fun deleteTaskById(id: Long) = tasksDao.deleteTaskById(id)

    override suspend fun deleteAllData() = tasksDao.deleteAllData()

    override suspend fun deleteAllTasksById(id: String) = tasksDao.deleteAllTasksById(id)
}