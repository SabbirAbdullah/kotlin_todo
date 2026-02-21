package com.example.taskmanager.data.local.dao

import androidx.room.*
import com.example.taskmanager.data.local.entity.DashboardEntity
import com.example.taskmanager.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user LIMIT 1")
    fun getUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Query("DELETE FROM user")
    suspend fun clearUser()
}

@Dao
interface DashboardDao {

    @Query("SELECT * FROM dashboard_cache LIMIT 1")
    fun getDashboardFlow(): Flow<DashboardEntity?>

    @Upsert
    suspend fun upsertDashboard(dashboard: DashboardEntity)

    @Query("DELETE FROM dashboard_cache")
    suspend fun clear()
}