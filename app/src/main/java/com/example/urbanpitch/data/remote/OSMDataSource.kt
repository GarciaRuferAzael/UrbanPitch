package com.example.urbanpitch.data.remote

import com.example.urbanpitch.utils.Coordinates
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OSMPlace(
    @SerialName("place_id")
    val id: Int,
    @SerialName("lat")
    val latitude: Double,
    @SerialName("lon")
    val longitude: Double,
    @SerialName("display_name")
    val displayName: String
)

class OSMDataSource(
    private val httpClient: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://nominatim.openstreetmap.org"
    }

    suspend fun searchPlaces(query: String): List<OSMPlace> {
        val url = "$BASE_URL/?q=$query&format=json&limit=1"
        return httpClient.get(url).body()
    }

    suspend fun getPlace(coordinates: Coordinates): OSMPlace {
        val url = "$BASE_URL/reverse?lat=${coordinates.latitude}&lon=${coordinates.longitude}&format=json&limit=1"
        return httpClient.get(url).body()
    }
}
