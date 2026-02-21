package com.example.taskmanager.data.repository

import com.example.taskmanager.data.local.dao.UserDao
import com.example.taskmanager.data.remote.api.ApiService
import com.example.taskmanager.data.remote.dto.LoginRequest
import com.example.taskmanager.data.remote.dto.RegisterRequest
import com.example.taskmanager.data.remote.dto.UpdateProfileRequest
import com.example.taskmanager.domain.model.Resource
import com.example.taskmanager.domain.model.User
import com.example.taskmanager.domain.repository.AuthRepository
import com.example.taskmanager.utils.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val userDao: UserDao,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<Unit> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                tokenManager.saveToken(body.token)
                tokenManager.saveUserInfo(body.user.id, body.user.name, body.user.email)
                userDao.upsertUser(body.user.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage}")
        }
    }

    override suspend fun register(name: String, email: String, password: String): Resource<Unit> {
        return try {
            val response = api.register(RegisterRequest(name, email, password))
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(Unit)
            } else {
                Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage}")
        }
    }

    override suspend fun logout(): Resource<Unit> {
        return try {
            api.logout() // best-effort API call
        } catch (_: Exception) { /* ignore */ } as Resource<Unit>
        tokenManager.clearAll()
        userDao.clearUser()
        return Resource.Success(Unit)
    }

    override suspend fun getProfile(): Resource<User> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                userDao.upsertUser(user.toEntity())
                Resource.Success(user.toDomain())
            } else {
                // Return from local cache on failure
                val cached = userDao.getUser()
                if (cached != null) Resource.Success(cached.toDomain())
                else Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            val cached = userDao.getUser()
            if (cached != null) Resource.Success(cached.toDomain())
            else Resource.Error("Network error: ${e.localizedMessage}")
        }
    }

    override suspend fun updateProfile(name: String, email: String): Resource<String> {
        return try {
            val response = api.updateProfile(UpdateProfileRequest(name, email))
            if (response.isSuccessful && response.body() != null) {
                // Update local cache
                val cached = userDao.getUser()
                cached?.let { userDao.upsertUser(it.copy(name = name, email = email)) }
                Resource.Success(response.body()!!.message)
            } else {
                Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage}")
        }
    }

    override fun getLocalUser(): Flow<User?> =
        userDao.getUserFlow().map { it?.toDomain() }

    private fun parseError(code: Int, body: String?): String {
        return when (code) {
            401 -> "Invalid credentials"
            422 -> "Validation error"
            404 -> "Not found"
            500 -> "Server error"
            else -> body?.take(100) ?: "Something went wrong"
        }
    }
}