package com.example.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.taskmanager.data.local.entity.DashboardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DashboardDao {

    @Query("SELECT * FROM dashboard_cache LIMIT 1")
    fun getDashboardFlow(): Flow<DashboardEntity?>

    @Upsert
    suspend fun upsertDashboard(dashboard: DashboardEntity)

    @Query("DELETE FROM dashboard_cache")
    suspend fun clear()
}