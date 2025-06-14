package com.example.smartparkingapp.services.impl

import android.util.Log
import com.example.smartparkingapp.api.CommandRequest
import com.example.smartparkingapp.api.ObjectBoundaryResponse
import com.example.smartparkingapp.api.RetrofitClient
import com.example.smartparkingapp.model.ParkingSpotModel
import com.example.smartparkingapp.model.UrbanZoneModel
import com.example.smartparkingapp.model.utils.CommandId
import com.example.smartparkingapp.model.utils.InvokedBy
import com.example.smartparkingapp.model.utils.ObjectId
import com.example.smartparkingapp.model.utils.TargetObject
import com.example.smartparkingapp.model.utils.UserId
import com.example.smartparkingapp.services.IObjectService
import com.example.smartparkingapp.ObjectAndUserConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Implementation of object service for managing parking-related entities.
 * Handles communication with the server API for urban zones and parking spots.
 */
class ObjectServiceImpl : IObjectService {

    private val SYSTEMID = "2025b.integrative.smartParking"
    private val objectConverter = ObjectAndUserConverter()
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
                    // Convert array to UrbanZoneModel
                    val urbanZones = rawJsonResponse.mapNotNull { jsonObject ->
                        try {
                            // Convert JSON object to ObjectBoundaryResponse using Gson
                            val jsonString = gson.toJson(jsonObject)
                            val boundaryResponse = gson.fromJson(jsonString,
                                object : TypeToken<Map<String, Any>>() {}.type) as Map<String, Any>

                            // Create ObjectBoundaryResponse from Map
                            val objectBoundary = createObjectBoundaryFromMap(boundaryResponse)

                            // Convert to UrbanZoneModel
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

    override fun getAllNonOccupiedParkingSpotsFromUrbanZone(
        userEmail: String,
        urbanZoneId: String
    ): List<ParkingSpotModel> {
        return try {
            Log.d("ObjectService", "Fetching non-occupied parking spots for zone: $urbanZoneId")

            // Create command request
            val commandRequest = CommandRequest(
                id = CommandId(
                    systemID = SYSTEMID,
                    commandId = "" // Server will generate this
                ),
                command = "getAllNonOccupiedParkingFromZone",
                targetObject = TargetObject(
                    id = ObjectId(
                        systemId = SYSTEMID,
                        objectId = urbanZoneId
                    )
                ),
                invocationTimestamp = null,
                invokedBy = InvokedBy(
                    userid = UserId(
                        email = userEmail,
                        systemID = SYSTEMID
                    )
                ),
                commandAttributes = emptyMap()
            )

            Log.d("ObjectService", "Sending command request: $commandRequest")

            // Execute command
            val response = RetrofitClient.apiService.executeCommand(commandRequest).execute()

            Log.d("ObjectServiceImpl","{$response}")
            if (response.isSuccessful) {
                val result = response.body()
                Log.d("ObjectService", "Command response received: $result")

                if (result != null) {
                    // The result should be a List of parking spots
                    val parkingSpotsList = when (result) {
                        is List<*> -> {
                            result.mapNotNull { item ->
                                try {
                                    // Convert each item to a Map
                                    val jsonString = gson.toJson(item)
                                    val boundaryMap = gson.fromJson(jsonString,
                                        object : TypeToken<Map<String, Any>>() {}.type) as Map<String, Any>

                                    // Create ObjectBoundaryResponse from Map
                                    val objectBoundary = createObjectBoundaryFromMap(boundaryMap)

                                    // Convert to ParkingSpotModel
                                    objectConverter.convertToParkingSpot(objectBoundary)
                                } catch (e: Exception) {
                                    Log.e("ObjectService", "Failed to convert parking spot", e)
                                    null
                                }
                            }
                        }
                        else -> {
                            Log.e("ObjectService", "Unexpected response type: ${result.javaClass}")
                            emptyList()
                        }
                    }

                    Log.d("ObjectService", "Successfully converted ${parkingSpotsList.size} parking spots")
                    parkingSpotsList
                } else {
                    Log.w("ObjectService", "Response body is null")
                    emptyList()
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("ObjectService", "Failed to fetch parking spots: ${response.code()}, Error: $errorBody")

                when (response.code()) {
                    400 -> throw Exception("Bad request - check your input data")
                    401 -> throw Exception("Unauthorized - please login again")
                    403 -> throw Exception("Access denied")
                    404 -> throw Exception("Urban zone not found")
                    500 -> throw Exception("Server error - please try again later")
                    else -> throw Exception("Failed to fetch non-occupied parking spots: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Log.e("ObjectService", "Error fetching non-occupied parking spots from urban zone", e)
            throw Exception("Failed to fetch non-occupied parking spots: ${e.message}")
        }
    }

    private fun createObjectBoundaryFromMap(map: Map<String, Any>): ObjectBoundaryResponse {
        val objectIdMap = map["objectId"] as? Map<String, Any> ?: mapOf()
        val objectIdResponse = ObjectId(
            systemId = objectIdMap["systemId"]?.toString() ?: "",
            objectId = objectIdMap["objectId"]?.toString() ?: ""
        )

        val createdByMap = map["createdBy"] as? Map<String, Any> ?: mapOf()
        val userIdMap = createdByMap["userId"] as? Map<String, Any> ?: mapOf()
        val userIdResponse = UserId(
            email = userIdMap["email"]?.toString() ?: "",
            systemID = userIdMap["systemId"]?.toString() ?: ""
        )
        val createdByResponse = com.example.smartparkingapp.model.utils.CreatedBy(
            userId = userIdResponse
        )

        val objectDetails = map["objectDetails"] as? Map<String, Any> ?: mapOf()

        return ObjectBoundaryResponse(
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