package com.example.smartparkingapp.model

/**
 * UrbanZone data model representing an urban zone with parking information.
 */

data class UrbanZone(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radius: Float = 0f,
    val zoneType: String = "",
    val totalParkingSpots: Int = 0,
    val availableParkingSpots: Int = 0,
    val baseHourlyRate: Double = 0.0,
    val parkingSpots: List<ParkingSpot> = emptyList()
)