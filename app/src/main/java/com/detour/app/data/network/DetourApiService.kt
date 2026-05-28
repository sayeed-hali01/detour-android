package com.detour.app.data.network

import com.detour.app.data.model.DetourRequest
import com.detour.app.data.model.DetourResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface DetourApiService {

    @POST("/api/detour-plan")
    suspend fun getDetourPlan(@Body request: DetourRequest): DetourResponse

    companion object {
        const val BASE_URL = "http://192.168.1.3:8000/"
    }
}
