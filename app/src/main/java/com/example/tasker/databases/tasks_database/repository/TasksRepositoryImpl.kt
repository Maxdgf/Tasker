package com.example.tasker.databases.tasks_database.repository

import com.example.tasker.databases.tasks_database.TasksDao
import com.example.tasker.databases.tasks_database.entities.TaskEntity
import com.example.tasker.databases.tasks_database.entities.TasksListEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(private val tasksDao: TasksDao) : TasksRepository {
    override fun getAllTasks(): Flow<List<TasksListEntity>> {
        return tasksDao.getAllTasksLists()
    }

    override suspend fun addTasksList(tasksList: TasksListEntity) {
        return tasksDao.addTasksList(tasksList)
    }

    override suspend fun addTask(task: TaskEntity) {
        return tasksDao.addTask(task)
    }

    override fun getAllTasksById(id: String): Flow<List<TaskEntity>> {
        return tasksDao.getAllTasksById(id)
    }

    override suspend fun setStateToTaskById(state: Boolean, id: Long) {
        return tasksDao.setStateToTaskById(state, id)
    }

    override suspend fun setCompletedTasksCountById(count: Int, id: String) {
        return tasksDao.setCompletedTasksCountById(count, id)
    }

    override suspend fun manageTasksListCompletionStateById(
        state: Boolean,
        id: String
    ) {
        return tasksDao.manageTasksListCompletionStateById(state, id)
    }

    override suspend fun updateTaskById(
        content: String,
        description: String?,
        id: Long
    ) {
        return tasksDao.updateTaskById(content, description, id)
    }

    override suspend fun deleteAllData() {
        return tasksDao.deleteAllData()
    }

    override suspend fun deleteAllTasksById(id: String) {
        return tasksDao.deleteAllTasksById(id)
    }
}