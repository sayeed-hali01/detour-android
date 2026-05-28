package com.detour.app.data.model

import com.google.gson.annotations.SerializedName

data class DetourRequest(
    @SerializedName("origin") val origin: String,
    @SerializedName("destination") val destination: String,
    @SerializedName("categories") val categories: List<String>,
    @SerializedName("max_detour_minutes") val max_detour_minutes: Float,
    @SerializedName("user_preferences") val user_preferences: String = ""
)

data class DetourResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("baseline_route") val baseline_route: BaselineRoute,
    @SerializedName("proposed_detours") val proposed_detours: List<Detour> = emptyList()
)

data class BaselineRoute(
    @SerializedName("origin") val origin: String,
    @SerializedName("destination") val destination: String,
    @SerializedName("distance_km") val distance_km: Double,
    @SerializedName("duration_mins") val duration_mins: Double,
    @SerializedName("polyline") val polyline: String
)

data class Detour(
    @SerializedName("rank") val rank: Double,
    @SerializedName("name") val name: String,
    @SerializedName("place_id") val place_id: String,
    @SerializedName("address") val address: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("rating") val rating: Double,
    @SerializedName("user_ratings_total") val user_ratings_total: Double,
    @SerializedName("category") val category: String,
    @SerializedName("score") val score: Double,
    @SerializedName("reasoning") val reasoning: String,
    @SerializedName("added_distance_km") val added_distance_km: Double,
    @SerializedName("added_duration_mins") val added_duration_mins: Double,
    @SerializedName("total_route_distance_km") val total_route_distance_km: Double,
    @SerializedName("total_route_duration_mins") val total_route_duration_mins: Double,
    @SerializedName("route_leg1_polyline") val route_leg1_polyline: String,
    @SerializedName("route_leg2_polyline") val route_leg2_polyline: String
)
