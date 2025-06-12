package com.example.smartparkingapp.services

import com.example.smartparkingapp.model.BaseObjectModel
import com.example.smartparkingapp.model.ParkingSpotModel
import com.example.smartparkingapp.model.UrbanZoneModel

/**
 * Service interface for urban zone-related operations.
 * This service handles business logic related to urban zones.
 */

interface IObjectService {
    /**
     * Retrieves all urban zones
     *
     * @return List of all urban zones
     * @throws Exception if retrieval fails
     */
    fun getAllUrbanZones(userEmail: String): List<UrbanZoneModel>

    /**
     * Retrieves all non-occupied parking spots from a specific urban zone
     *
     * @param userEmail The user's email
     * @param urbanZoneId The ID of the urban zone
     * @return List of non-occupied parking spots
     * @throws Exception if retrieval fails
     */
    fun getAllNonOccupiedParkingSpotsFromUrbanZone(userEmail: String, urbanZoneId: String): List<ParkingSpotModel>

    /**
     * Retrieves an urban zone by its ID
     *
     * @param urbanZoneId The ID of the urban zone to retrieve
     * @return The urban zone if found
     * @throws IllegalArgumentException if ID is invalid
     * @throws NoSuchElementException if urban zone not found
     * @throws Exception if retrieval fails
     */
//    fun getUrbanZoneById(urbanZoneId: String): UrbanZone


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
//    fun updateAvailableParkingSpots(urbanZoneId: String, availableSpots: Int): UrbanZone

}