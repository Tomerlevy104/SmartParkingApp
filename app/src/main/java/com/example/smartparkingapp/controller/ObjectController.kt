package com.example.smartparkingapp.controller

import com.example.smartparkingapp.model.BaseObjectModel
import com.example.smartparkingapp.model.UrbanZoneModel
import com.example.smartparkingapp.services.IObjectService

/**
 * Controller class for handling urban zone-related operations.
 * This controller serves as an intermediary between the UI and business logic.
 */
class ObjectController(private val objectService: IObjectService) {

    /**
     * Get all urban zones
     *
     * @return List of all urban zones
     * @throws Exception if retrieval fails
     */
    fun getAllUrbanZones(userEmail: String): List<UrbanZoneModel> {
        return objectService.getAllUrbanZones(userEmail)
    }

    /**
     * Creates a new urban zone
     *
     * @param urbanZone The urban zone to create
     * @return The created urban zone with assigned ID
     * @throws IllegalArgumentException if parameters are invalid
     * @throws Exception if creation fails
     */
//    fun createUrbanZone(urbanZone: UrbanZone): UrbanZone {
//        return urbanZoneService.createUrbanZone(urbanZone)
//    }

    /**
     * Updates the available parking spots count for an urban zone
     *
     * @param urbanZoneId The ID of the urban zone to update
     * @param availableSpots The new number of available spots
     * @return The updated urban zone
     * @throws IllegalArgumentException if parameters are invalid
     * @throws NoSuchElementException if urban zone not found
     * @throws Exception if update fails
     */
//    fun updateAvailableParkingSpots(urbanZoneId: String, availableSpots: Int): UrbanZone {
//        return objectService.updateAvailableParkingSpots(urbanZoneId, availableSpots)
//    }

}