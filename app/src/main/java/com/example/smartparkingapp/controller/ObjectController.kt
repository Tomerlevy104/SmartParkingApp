package com.example.smartparkingapp.controller

import com.example.smartparkingapp.model.ParkingSpotModel
import com.example.smartparkingapp.model.UrbanZoneModel
import com.example.smartparkingapp.services.IObjectService

/**
 * Controller class for handling urban zone-related operations.
 * This controller serves as an intermediary between the UI and business logic.
 */
class ObjectController(private val objectService: IObjectService) {

    /**
     * Get all urban zones
     * @return List of all urban zones
     */
    fun getAllUrbanZones(userEmail: String): List<UrbanZoneModel> {
        return objectService.getAllUrbanZones(userEmail)
    }

    /**
     * Get all non-occupied parking spots from a specific urban zone
     * @param userEmail The user's email
     * @param urbanZoneId The ID of the urban zone
     * @return List of non-occupied parking spots
     */
    fun getAllNonOccupiedParkingSpotsFromUrbanZone(
        userEmail: String,
        urbanZoneId: String
    ): List<ParkingSpotModel> {
        return objectService.getAllNonOccupiedParkingSpotsFromUrbanZone(userEmail, urbanZoneId)
    }
}