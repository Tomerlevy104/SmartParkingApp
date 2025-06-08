package com.example.smartparkingapp.model

/**
 * ParkingSpot data model representing an individual parking spot in an urban zone.
 */

data class ParkingSpotModel(
    val id: String = "",
    val restrictions: String = "",
    val occupied: Boolean = false,
    val turnoverRate: String = "",
    val address: String = "",
    val zoneId: String = "",
    val isCovered: Boolean = false,
    val pricePerHour: String = ""
)