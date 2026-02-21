package com.example.taskmanager.data.remote.api

import com.example.taskmanager.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────────────

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("api/auth/profile")
    suspend fun getProfile(): Response<UserDto>

    @PUT("api/auth/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<MessageResponse>

    @POST("api/auth/logout")
    suspend fun logout(): Response<MessageResponse>

    @POST("api/auth/refresh-token")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<RefreshTokenResponse>

    // ── Tasks ─────────────────────────────────────────────────────────────────

    @GET("api/todos")
    suspend fun getTasks(): Response<TaskListResponse>

    @GET("api/todos/{id}")
    suspend fun getTask(
        @Path("id") id: Int
    ): Response<TaskDto>

    @POST("api/todos")
    suspend fun createTask(
        @Body request: CreateTaskRequest
    ): Response<TaskDto>

    @PUT("api/todos/{id}")
    suspend fun updateTask(
        @Path("id") id: Int,
        @Body request: UpdateTaskRequest
    ): Response<MessageResponse>

    @DELETE("api/todos/{id}")
    suspend fun deleteTask(
        @Path("id") id: Int
    ): Response<MessageResponse>

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @GET("api/dashboard")
    suspend fun getDashboard(): Response<DashboardResponse>
}