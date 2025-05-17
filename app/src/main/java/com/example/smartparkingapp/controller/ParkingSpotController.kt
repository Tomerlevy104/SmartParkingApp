package com.example.smartparkingapp.controller

import com.example.smartparkingapp.model.ParkingSpot
import com.example.smartparkingapp.services.IParkingSpotService

/**
 * Controller class for handling parking spot-related operations.
 * This controller serves as an intermediary between the UI and business logic.
 */
class ParkingSpotController(private val parkingSpotService: IParkingSpotService) {

    /**
     * Retrieves all parking spots
     *
     * @return List of all parking spots
     * @throws Exception if retrieval fails
     */
    fun getAllParkingSpots(): List<ParkingSpot> {
        return parkingSpotService.getAllParkingSpots()
    }

    /**
     * Retrieves all parking spots for a specific urban zone
     *
     * @param urbanZoneId The ID of the urban zone
     * @return List of parking spots in the specified urban zone
     * @throws IllegalArgumentException if ID is invalid
     * @throws Exception if retrieval fails
     */
    fun getParkingSpotsByUrbanZone(urbanZoneId: String): List<ParkingSpot> {
        return parkingSpotService.getParkingSpotsByUrbanZone(urbanZoneId)
    }

    /**
     * Retrieves available (unoccupied) parking spots for a specific urban zone
     *
     * @param urbanZoneId The ID of the urban zone
     * @return List of available parking spots in the specified urban zone
     * @throws IllegalArgumentException if ID is invalid
     * @throws Exception if retrieval fails
     */
    fun getAvailableParkingSpots(urbanZoneId: String): List<ParkingSpot> {
        return parkingSpotService.getAvailableParkingSpots(urbanZoneId)
    }

    /**
     * Retrieves a parking spot by its ID
     *
     * @param parkingSpotId The ID of the parking spot to retrieve
     * @return The parking spot if found
     * @throws IllegalArgumentException if ID is invalid
     * @throws NoSuchElementException if parking spot not found
     * @throws Exception if retrieval fails
     */
    fun getParkingSpotById(parkingSpotId: String): ParkingSpot {
        return parkingSpotService.getParkingSpotById(parkingSpotId)
    }

    /**
     * Creates a new parking spot
     *
     * @param parkingSpot The parking spot to create
     * @return The created parking spot with assigned ID
     * @throws IllegalArgumentException if parameters are invalid
     * @throws Exception if creation fails
     */
    fun createParkingSpot(parkingSpot: ParkingSpot): ParkingSpot {
        return parkingSpotService.createParkingSpot(parkingSpot)
    }

    /**
     * Updates an existing parking spot
     *
     * @param parkingSpot The parking spot with updated information
     * @return The updated parking spot
     * @throws IllegalArgumentException if parameters are invalid
     * @throws NoSuchElementException if parking spot not found
     * @throws Exception if update fails
     */
    fun updateParkingSpot(parkingSpot: ParkingSpot): ParkingSpot {
        return parkingSpotService.updateParkingSpot(parkingSpot)
    }

    /**
     * Deletes a parking spot by its ID
     *
     * @param parkingSpotId The ID of the parking spot to delete
     * @return Boolean indicating if deletion was successful
     * @throws IllegalArgumentException if ID is invalid
     * @throws NoSuchElementException if parking spot not found
     * @throws Exception if deletion fails
     */
    fun deleteParkingSpot(parkingSpotId: String): Boolean {
        return parkingSpotService.deleteParkingSpot(parkingSpotId)
    }

    /**
     * Changes the occupancy status of a parking spot
     *
     * @param parkingSpotId The ID of the parking spot to update
     * @param isOccupied The new occupancy status
     * @return The updated parking spot
     * @throws IllegalArgumentException if ID is invalid
     * @throws NoSuchElementException if parking spot not found
     * @throws Exception if update fails
     */
    fun setOccupancyStatus(parkingSpotId: String, isOccupied: Boolean): ParkingSpot {
        return parkingSpotService.setOccupancyStatus(parkingSpotId, isOccupied)
    }

    /**
     * Finds the nearest available parking spots
     *
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param limit Maximum number of spots to return
     * @return List of nearest available parking spots
     * @throws IllegalArgumentException if parameters are invalid
     * @throws Exception if search fails
     */
    fun findNearestAvailableSpots(latitude: Double, longitude: Double, limit: Int = 10): List<ParkingSpot> {
        return parkingSpotService.findNearestAvailableSpots(latitude, longitude, limit)
    }

    /**
     * Updates the hourly rate for a specific parking spot
     *
     * @param parkingSpotId The ID of the parking spot
     * @param pricePerHour The new hourly rate
     * @return The updated parking spot
     * @throws IllegalArgumentException if parameters are invalid
     * @throws NoSuchElementException if parking spot not found
     * @throws Exception if update fails
     */
    fun updateHourlyRate(parkingSpotId: String, pricePerHour: String): ParkingSpot {
        return parkingSpotService.updateHourlyRate(parkingSpotId, pricePerHour)
    }

    /**
     * Updates restrictions for a specific parking spot
     *
     * @param parkingSpotId The ID of the parking spot
     * @param restrictions The new restrictions
     * @return The updated parking spot
     * @throws IllegalArgumentException if parameters are invalid
     * @throws NoSuchElementException if parking spot not found
     * @throws Exception if update fails
     */
    fun updateRestrictions(parkingSpotId: String, restrictions: String): ParkingSpot {
        return parkingSpotService.updateRestrictions(parkingSpotId, restrictions)
    }

    /**
     * Finds parking spots by address or partial address
     *
     * @param addressQuery The address query to search for
     * @return List of parking spots matching the address query
     * @throws IllegalArgumentException if query is invalid
     * @throws Exception if search fails
     */
    fun findParkingSpotsByAddress(addressQuery: String): List<ParkingSpot> {
        return parkingSpotService.findParkingSpotsByAddress(addressQuery)
    }
}