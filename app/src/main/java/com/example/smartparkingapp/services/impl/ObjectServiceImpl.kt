package com.example.smartparkingapp.services.impl

import android.util.Log
import com.example.smartparkingapp.api.RetrofitClient
import com.example.smartparkingapp.model.UrbanZoneModel
import com.example.smartparkingapp.services.IObjectService
import com.example.smartparkingapp.utils.ObjectConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ObjectServiceImpl : IObjectService {

    private val SYSTEMID = "2025b.integrative.smartParking"
    private val objectConverter = ObjectConverter()
    private val gson = Gson()

    override fun getAllUrbanZones(userEmail: String): List<UrbanZoneModel> {
        return try {
            Log.d("ObjectService", "Fetching urban zones for user: $userEmail")

            val response = RetrofitClient.apiService.getObjectsByType(
                type = "urbanzone",
                userSystemID = SYSTEMID,
                userEmail = userEmail
            ).execute()

            if (response.isSuccessful) {
                val rawJsonResponse = response.body()
                Log.d("ObjectService", "Raw response received: ${rawJsonResponse?.size} objects")

                if (rawJsonResponse != null) {
                    // המרת המערך ל-UrbanZoneModel
                    val urbanZones = rawJsonResponse.mapNotNull { jsonObject ->
                        try {
                            // המרת JSON object ל-ObjectBoundaryResponse באמצעות Gson
                            val jsonString = gson.toJson(jsonObject)
                            val boundaryResponse = gson.fromJson(jsonString,
                                object : TypeToken<Map<String, Any>>() {}.type) as Map<String, Any>

                            // יצירת ObjectBoundaryResponse מה-Map
                            val objectBoundary = createObjectBoundaryFromMap(boundaryResponse)

                            // המרה ל-UrbanZoneModel
                            objectConverter.convertToUrbanZone(objectBoundary)
                        } catch (e: Exception) {
                            Log.e("ObjectService", "Failed to convert object to UrbanZone", e)
                            null
                        }
                    }

                    Log.d("ObjectService", "Successfully converted ${urbanZones.size} urban zones")
                    urbanZones
                } else {
                    Log.w("ObjectService", "Response body is null")
                    emptyList()
                }
            } else {
                Log.e("ObjectService", "Failed to fetch urban zones: ${response.code()}")
                when (response.code()) {
                    401 -> throw Exception("Unauthorized - please login again")
                    403 -> throw Exception("Access denied")
                    404 -> throw Exception("No urban zones found")
                    500 -> throw Exception("Server error - please try again later")
                    else -> throw Exception("Failed to fetch urban zones: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Log.e("ObjectService", "Error fetching urban zones", e)
            throw Exception("Failed to fetch urban zones: ${e.message}")
        }
    }

    private fun createObjectBoundaryFromMap(map: Map<String, Any>): com.example.smartparkingapp.api.ObjectBoundaryResponse {
        // חילוץ objectId
        val objectIdMap = map["objectId"] as? Map<String, Any> ?: mapOf()
        val objectIdResponse = com.example.smartparkingapp.model.util.ObjectId(
            systemId = objectIdMap["systemId"]?.toString() ?: "",
            objectId = objectIdMap["objectId"]?.toString() ?: ""
        )

        // חילוץ createdBy
        val createdByMap = map["createdBy"] as? Map<String, Any> ?: mapOf()
        val userIdMap = createdByMap["userId"] as? Map<String, Any> ?: mapOf()
        val userIdResponse = com.example.smartparkingapp.model.util.UserId(
            email = userIdMap["email"]?.toString() ?: "",
            systemId = userIdMap["systemId"]?.toString() ?: ""
        )
        val createdByResponse = com.example.smartparkingapp.model.util.CreatedBy(
            userId = userIdResponse
        )

        // חילוץ objectDetails
        val objectDetails = map["objectDetails"] as? Map<String, Any> ?: mapOf()

        return com.example.smartparkingapp.api.ObjectBoundaryResponse(
            objectId = objectIdResponse,
            type = map["type"]?.toString() ?: "",
            alias = map["alias"]?.toString() ?: "",
            status = map["status"]?.toString() ?: "",
            active = map["active"] as? Boolean ?: false,
            creationTimestamp = map["creationTimestamp"]?.toString() ?: "",
            createdBy = createdByResponse,
            objectDetails = objectDetails
        )
    }
}