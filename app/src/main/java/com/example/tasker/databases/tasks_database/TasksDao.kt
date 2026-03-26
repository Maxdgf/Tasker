package com.example.tasker.databases.tasks_database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.example.tasker.databases.tasks_database.entities.TaskEntity
import com.example.tasker.databases.tasks_database.entities.TasksListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDao {
    @Query("SELECT * FROM tasks_headers_table")
    fun getAllTasksLists(): Flow<List<TasksListEntity>>

    @Insert(onConflict = REPLACE)
    suspend fun addTasksList(tasksList: TasksListEntity)

    @Insert(onConflict = REPLACE)
    suspend fun addTask(taskEntity: TaskEntity)

    @Query("SELECT * FROM tasks_list_table WHERE task_id = :id")
    fun getAllTasksById(id: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks_headers_table WHERE id = :id")
    fun getTasksListById(id: Long): Flow<TasksListEntity>

    @Query("UPDATE tasks_list_table SET task_is_completed = :state WHERE id = :id")
    suspend fun setStateToTaskById(state: Boolean, id: Long)

    @Query("UPDATE tasks_headers_table SET tasks_list_completed_tasks_count = :count WHERE tasks_id = :id")
    suspend fun setCompletedTasksCountById(count: Int, id: String)

    @Query("UPDATE tasks_headers_table SET tasks_list_is_completed_state = :state WHERE tasks_id = :id")
    suspend fun manageTasksListCompletionStateById(state: Boolean, id: String)

    @Query("UPDATE tasks_list_table SET task_content = :content, task_description = :description WHERE id = :id")
    suspend fun updateTaskById(content: String, description: String?, id: Long)

    @Query("DELETE FROM tasks_headers_table")
    suspend fun deleteAllTasksHeadersList()

    @Query("DELETE FROM tasks_list_table")
    suspend fun deleteAllTasksList()

    @Query("DELETE FROM tasks_headers_table WHERE tasks_id = :id")
    suspend fun deleteTasksListHeaderById(id: String)

    @Query("DELETE FROM tasks_list_table WHERE task_id = :id")
    suspend fun deleteTasksById(id: String)

    @Query("DELETE FROM tasks_list_table WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Transaction
    suspend fun deleteAllData() {
        deleteAllTasksHeadersList()
        deleteAllTasksList()
    }

    @Transaction
    suspend fun deleteAllTasksById(id: String) {
        deleteTasksListHeaderById(id)
        deleteTasksById(id)
    }
}