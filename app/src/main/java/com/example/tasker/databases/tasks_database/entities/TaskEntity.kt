package com.example.tasker.databases.tasks_database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks_list_table")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "task_content") val content: String, // text of task
    @ColumnInfo(name = "task_description") val description: String? = null, // task description (optional)
    @ColumnInfo(name = "task_is_completed") val isCompleted: Boolean = false // is task completed state
)
