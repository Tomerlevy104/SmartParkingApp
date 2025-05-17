package com.example.smartparkingapp.controller

import com.example.smartparkingapp.model.UrbanZone
import com.example.smartparkingapp.services.IUrbanZoneService

/**
 * Controller class for handling urban zone-related operations.
 * This controller serves as an intermediary between the UI and business logic.
 */
class UrbanZoneController(private val urbanZoneService: IUrbanZoneService) {

    /**
     * Get all urban zones
     *
     * @return List of all urban zones
     * @throws Exception if retrieval fails
     */
    fun getAllUrbanZones(): List<UrbanZone> {
        return urbanZoneService.getAllUrbanZones()
    }

    /**
     * Creates a new urban zone
     *
     * @param urbanZone The urban zone to create
     * @return The created urban zone with assigned ID
     * @throws IllegalArgumentException if parameters are invalid
     * @throws Exception if creation fails
     */
    fun createUrbanZone(urbanZone: UrbanZone): UrbanZone {
        return urbanZoneService.createUrbanZone(urbanZone)
    }

    /**
     * Retrieves an urban zone by its ID
     *
     * @param urbanZoneId The ID of the urban zone to retrieve
     * @return The urban zone if found
     * @throws IllegalArgumentException if ID is invalid
     * @throws NoSuchElementException if urban zone not found
     * @throws Exception if retrieval fails
     */
    fun getUrbanZoneById(urbanZoneId: String): UrbanZone {
        return urbanZoneService.getUrbanZoneById(urbanZoneId)
    }

    /**
     * Updates an existing urban zone
     *
     * @param urbanZone The urban zone with updated information
     * @return The updated urban zone
     * @throws IllegalArgumentException if parameters are invalid
     * @throws NoSuchElementException if urban zone not found
     * @throws Exception if update fails
     */
    fun updateUrbanZone(urbanZone: UrbanZone): UrbanZone {
        return urbanZoneService.updateUrbanZone(urbanZone)
    }

    /**
     * Deletes an urban zone by its ID
     *
     * @param urbanZoneId The ID of the urban zone to delete
     * @return Boolean indicating if deletion was successful
     * @throws IllegalArgumentException if ID is invalid
     * @throws NoSuchElementException if urban zone not found
     * @throws Exception if deletion fails
     */
    fun deleteUrbanZone(urbanZoneId: String): Boolean {
        return urbanZoneService.deleteUrbanZone(urbanZoneId)
    }

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
    fun findNearbyUrbanZones(latitude: Double, longitude: Double, radiusInMeters: Float = 1000f): List<UrbanZone> {
        return urbanZoneService.findNearbyUrbanZones(latitude, longitude, radiusInMeters)
    }

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
    fun updateAvailableParkingSpots(urbanZoneId: String, availableSpots: Int): UrbanZone {
        return urbanZoneService.updateAvailableParkingSpots(urbanZoneId, availableSpots)
    }

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
    fun updateHourlyRate(urbanZoneId: String, hourlyRate: Double): UrbanZone {
        return urbanZoneService.updateHourlyRate(urbanZoneId, hourlyRate)
    }

    /**
     * Retrieves statistics for an urban zone
     *
     * @param urbanZoneId The ID of the urban zone
     * @return Map of statistical data for the urban zone
     * @throws IllegalArgumentException if ID is invalid
     * @throws NoSuchElementException if urban zone not found
     * @throws Exception if retrieval fails
     */
    fun getUrbanZoneStatistics(urbanZoneId: String): Map<String, Any> {
        return urbanZoneService.getUrbanZoneStatistics(urbanZoneId)
    }
}