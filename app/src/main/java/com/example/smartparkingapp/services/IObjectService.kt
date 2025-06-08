package com.example.smartparkingapp.services

import com.example.smartparkingapp.model.BaseObjectModel
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
     * Creates a new urban zone
     *
     * @param urbanZone The urban zone to create
     * @return The created urban zone with assigned ID
     * @throws IllegalArgumentException if parameters are invalid
     * @throws Exception if creation fails
     */
//    fun createUrbanZone(urbanZone: UrbanZone): UrbanZone

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
     * Finds urban zones near a specific location
     *
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param radiusInMeters Search radius in meters
     * @return List of urban zones within the specified radius
     * @throws IllegalArgumentException if parameters are invalid
     * @throws Exception if search fails
     */
//    fun findNearbyUrbanZones(latitude: Double, longitude: Double, radiusInMeters: Float): List<UrbanZone>

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

    /**
     * Updates the hourly rate for an urban zone
     *
     * @param urbanZoneId The ID of the urban zone to update
     * @param hourlyRate The new hourly rate
     * @return The updated urban zone
     * @throws IllegalArgumentException if parameters are invalid
     * @throws NoSuchElementException if urban zone not found
     * @throws Exception if update fails
     */
//    fun updateHourlyRate(urbanZoneId: String, hourlyRate: Double): UrbanZone

    /**
     * Retrieves statistics for an urban zone
     *
     * @param urbanZoneId The ID of the urban zone
     * @return Map of statistical data for the urban zone
     * @throws IllegalArgumentException if ID is invalid
     * @throws NoSuchElementException if urban zone not found
     * @throws Exception if retrieval fails
     */
//    fun getUrbanZoneStatistics(urbanZoneId: String): Map<String, Any>
}