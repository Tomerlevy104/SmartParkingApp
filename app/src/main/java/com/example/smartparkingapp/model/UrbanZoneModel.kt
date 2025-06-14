package com.example.smartparkingapp.model

import com.example.smartparkingapp.model.utils.CreatedBy
import com.example.smartparkingapp.model.utils.ObjectId

/**
 * Model representing an urban zone.
 */
class UrbanZoneModel(
    objectId: ObjectId,
    type: String,
    alias: String,
    status: String,
    active: Boolean,
    creationTimestamp: String,
    createdBy: CreatedBy,
) : BaseObjectModel(objectId, type, alias, status, active, creationTimestamp, createdBy) {

    private var name: String = ""
    private var description: String = ""
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var radius: Float = 0f
    private var zoneType: String = ""
    private var totalParkingSpots: Int = 0
    private var availableParkingSpots: Int = 0
    private var baseHourlyRate: Double = 0.0

    // Getters
    fun getName(): String {
        return this.name
    }

    fun getDescription(): String {
        return this.description
    }

    fun getTotalParkingSpots(): Int {
        return this.totalParkingSpots
    }

    fun getAvailableParkingSpots(): Int {
        return this.availableParkingSpots
    }

    fun getBaseHourlyRate(): Double {
        return this.baseHourlyRate
    }

    fun getLatitude(): Double {
        return this.latitude
    }

    fun getLongitude(): Double {
        return this.longitude
    }

    fun getRadius(): Float {
        return this.radius
    }

    fun getZoneType(): String {
        return this.zoneType
    }

    // Setters
    fun setName(name: String) {
        this.name = name
    }

    fun setDescription(description: String) {
        this.description = description
    }

    fun setLatitude(latitude: Double) {
        this.latitude = latitude
    }

    fun setLongitude(longitude: Double) {
        this.longitude = longitude
    }

    fun setRadius(radius: Float) {
        this.radius = radius
    }

    fun setZoneType(zoneType: String) {
        this.zoneType = zoneType
    }

    fun setTotalParkingSpots(total: Int) {
        this.totalParkingSpots = total
    }

    fun setAvailableParkingSpots(available: Int) {
        this.availableParkingSpots = available
    }

    fun setBaseHourlyRate(rate: Double) {
        this.baseHourlyRate = rate
    }


    fun getOccupancyRate(): Double {
        if (this.totalParkingSpots == 0) {
            return 0.0
        }
        val occupied = this.totalParkingSpots - this.availableParkingSpots
        return (occupied.toDouble() / this.totalParkingSpots.toDouble()) * 100
    }

    fun hasAvailableSpots(): Boolean {
        return this.availableParkingSpots > 0
    }

    fun getFormattedHourlyRate(): String {
        return "₪" + String.format("%.2f", this.baseHourlyRate)
    }

    override fun toString(): String {
        return "UrbanZone(id=" + getObjectId().objectId + ", name='" + this.name + "', totalSpots=" + this.totalParkingSpots + ", availableSpots=" + this.availableParkingSpots + ")"
    }
}