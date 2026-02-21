package com.example.taskmanager.di

import android.content.Context
import androidx.room.Room
import com.example.taskmanager.data.local.AppDatabase
import com.example.taskmanager.data.local.dao.DashboardDao
import com.example.taskmanager.data.local.dao.TaskDao
import com.example.taskmanager.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "task_manager_db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideDashboardDao(db: AppDatabase): DashboardDao = db.dashboardDao()
}