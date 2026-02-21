package com.example.taskmanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.taskmanager.data.local.dao.DashboardDao
import com.example.taskmanager.data.local.dao.TaskDao
import com.example.taskmanager.data.local.dao.UserDao
import com.example.taskmanager.data.local.entity.DashboardEntity
import com.example.taskmanager.data.local.entity.TaskEntity
import com.example.taskmanager.data.local.entity.UserEntity

@Database(
    entities = [
        TaskEntity::class,
        UserEntity::class,
        DashboardEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun dashboardDao(): DashboardDao
}