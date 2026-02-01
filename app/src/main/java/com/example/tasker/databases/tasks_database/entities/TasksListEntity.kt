package com.example.tasker.databases.tasks_database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks_headers_table")
data class TasksListEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // primary key
    @ColumnInfo(name = "tasks_id") val tasksId: String, // tasks id (uuid4)
    @ColumnInfo(name = "tasks_list_name") val name: String, // name of tasks list
    @ColumnInfo(name = "tasks_list_description") val description: String? = null, // description of tasks list (optional)
    @ColumnInfo(name = "tasks_list_created_at") val time: Long = System.currentTimeMillis(), // created at time
    @ColumnInfo(name = "tasks_list_tasks_count") val tasksCount: Int, // all tasks count in list
    @ColumnInfo(name = "tasks_list_completed_tasks_count") val completedTasksCount: Int = 0, // all completed tasks count in list
    @ColumnInfo(name = "tasks_list_is_completed_state") val isCompleted: Boolean = false, // is tasks list completed state
    @ColumnInfo(name = "tasks_list_deadline") val deadline: Long? = null // tasks list deadline (optional)
)