package com.example.smartparkingapp.utils

import android.util.Log
import com.example.smartparkingapp.api.ObjectBoundaryResponse
import com.example.smartparkingapp.model.UrbanZoneModel
import com.example.smartparkingapp.model.util.CreatedBy
import com.example.smartparkingapp.model.util.ObjectId
import com.example.smartparkingapp.model.util.UserId

class ObjectConverter {

    /**
     * Converts ObjectBoundaryResponse from the server to UrbanZoneModel
     */
    fun convertToUrbanZone(boundary: ObjectBoundaryResponse): UrbanZoneModel? {
        try {
            // Check that it is indeed UrbanZone
            if (boundary.type.lowercase() != "urbanzone") {
                Log.w("ObjectConverter", "Object type is not urbanzone: ${boundary.type}")
                return null
            }

            // Create ObjectId
            val objectId = ObjectId(
                systemId = boundary.objectId.systemId,
                objectId = boundary.objectId.objectId
            )

            // Create CreatedBy
            val userId = UserId(
                email = boundary.createdBy.userId.email,
                systemId = boundary.createdBy.userId.systemId
            )
            val createdBy = CreatedBy(userId = userId)

            // Create UrbanZone
            val urbanZone = UrbanZoneModel(
                objectId = objectId,
                type = boundary.type,
                alias = boundary.alias,
                status = boundary.status,
                active = boundary.active,
                creationTimestamp = boundary.creationTimestamp,
                createdBy = createdBy
            )

            // Extract object details and set specific fields
            val details = boundary.objectDetails

            try {
                urbanZone.setName(details["name"]?.toString() ?: boundary.alias)
                Log.d("ObjectConverter", "urbanZone Name: ${urbanZone.getName()}")

                urbanZone.setDescription(details["description"]?.toString() ?: "")
                urbanZone.setLatitude(details["latitude"]?.toString()?.toDouble() ?: 0.0)
                urbanZone.setLongitude(details["longitude"]?.toString()?.toDouble() ?: 0.0)
                urbanZone.setRadius(details["radius"]?.toString()?.toFloat() ?: 0f)
                urbanZone.setZoneType(details["zoneType"]?.toString() ?: "")
                urbanZone.setTotalParkingSpots(details["totalParkingSpots"]?.toString()?.toInt() ?: 0)
                urbanZone.setAvailableParkingSpots(details["availableParkingSpots"]?.toString()?.toInt() ?: 0)
                urbanZone.setBaseHourlyRate(details["baseHourlyRate"]?.toString()?.toDouble() ?: 0.0)

            } catch (e: NumberFormatException) {
                Log.w("ObjectConverter", "Error parsing number from objectDetails: ${e.message}")
                // Use default values - already handled by elvis operator (?:)
            }

            Log.d("ObjectConverter", "Successfully converted urban zone: ${urbanZone.getName()} with ${urbanZone.getTotalParkingSpots()} spots")
            return urbanZone

        } catch (e: Exception) {
            Log.e("ObjectConverter", "Error converting ObjectBoundary to UrbanZone", e)
            return null
        }
    }
}