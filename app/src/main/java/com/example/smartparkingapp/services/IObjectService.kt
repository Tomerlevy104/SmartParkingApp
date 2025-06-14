package com.example.smartparkingapp.services

import com.example.smartparkingapp.model.ParkingSpotModel
import com.example.smartparkingapp.model.UrbanZoneModel

/**
 * Service interface for urban zone-related operations.
 * This service handles business logic related to urban zones.
 */

interface IObjectService {

    /**
     * Get all urban zones
     * @return List of all urban zones
     */
    fun getAllUrbanZones(userEmail: String): List<UrbanZoneModel>

    /**
     * Get all non-occupied parking spots from a specific urban zone
     * @param userEmail The user's email
     * @param urbanZoneId The ID of the urban zone
     * @return List of non-occupied parking spots
     */
    fun getAllNonOccupiedParkingSpotsFromUrbanZone(
        userEmail: String,
        urbanZoneId: String
    ): List<ParkingSpotModel>
}