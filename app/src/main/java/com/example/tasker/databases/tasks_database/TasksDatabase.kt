package com.example.tasker.databases.tasks_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tasker.databases.tasks_database.entities.TaskEntity
import com.example.tasker.databases.tasks_database.entities.TasksListEntity

@Database(
    entities = [
        TasksListEntity::class,
        TaskEntity::class
    ], // entities
    version = 2, // db version index
    exportSchema = false
)
abstract class TasksDatabase : RoomDatabase() {
    abstract fun getAllTasksDao(): TasksDao
}