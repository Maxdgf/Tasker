package com.example.tasker.di

import android.content.Context
import androidx.room.Room
import com.example.tasker.databases.tasks_database.TasksDao
import com.example.tasker.databases.tasks_database.TasksDatabase
import com.example.tasker.databases.tasks_database.repository.TasksRepository
import com.example.tasker.databases.tasks_database.repository.TasksRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideTasksAppDatabase(@ApplicationContext context: Context): TasksDatabase =
        Room.databaseBuilder(
            context,
            TasksDatabase::class.java,
            "tasks_database"
        ).fallbackToDestructiveMigration(false).build()

    @Provides
    fun tasksDao(tasksDatabase: TasksDatabase): TasksDao =
        tasksDatabase.getAllTasksDao()

    @Singleton
    @Provides
    fun provideTaskRepository(tasksDao: TasksDao): TasksRepository =
        TasksRepositoryImpl(tasksDao)
}