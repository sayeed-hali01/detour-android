package com.detour.app.repository

import com.detour.app.data.model.DetourRequest
import com.detour.app.data.model.DetourResponse
import com.detour.app.data.network.RetrofitClient

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val cause: Throwable? = null) : Result<Nothing>()
}

class DetourRepository {

    suspend fun planDetour(
        origin: String,
        destination: String,
        categories: List<String>,
        maxDetourMinutes: Int,
        userPreferences: String = ""
    ): Result<DetourResponse> {
        return try {
            val request = DetourRequest(
                origin = origin,
                destination = destination,
                categories = categories,
                max_detour_minutes = maxDetourMinutes.toFloat(),
                user_preferences = userPreferences
            )
            val response = RetrofitClient.api.getDetourPlan(request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error", e)
        }
    }
}
