package com.example.taskmanager.data.repository

import com.example.taskmanager.data.local.dao.DashboardDao
import com.example.taskmanager.data.remote.api.ApiService
import com.example.taskmanager.domain.model.Dashboard
import com.example.taskmanager.domain.model.Resource
import com.example.taskmanager.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val dashboardDao: DashboardDao
) : DashboardRepository {

    override fun getDashboardFlow(): Flow<Dashboard?> =
        dashboardDao.getDashboardFlow().map { it?.toDomain() }

    override suspend fun fetchDashboard(): Resource<Dashboard> {
        return try {
            val response = api.getDashboard()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                dashboardDao.upsertDashboard(body.toEntity())
                Resource.Success(body.toDomain())
            } else {
                Resource.Error("Failed to load dashboard: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage}")
        }
    }
}