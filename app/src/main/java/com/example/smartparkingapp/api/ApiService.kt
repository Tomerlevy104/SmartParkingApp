package com.example.smartparkingapp.api

import com.example.smartparkingapp.model.utils.CommandId
import com.example.smartparkingapp.model.utils.CreatedBy
import com.example.smartparkingapp.model.utils.InvokedBy
import com.example.smartparkingapp.model.utils.ObjectId
import com.example.smartparkingapp.model.utils.TargetObject
import com.example.smartparkingapp.model.utils.UserId
import retrofit2.Call
import retrofit2.http.*
import java.util.Date

/**
 * API Service Interface for Smart Parking Application
 * Provides RESTful API endpoints
 */
interface ApiService {

    @GET("ambient-intelligence/users/login/{systemID}/{userEmail}")
    fun login(
        @Path("systemID") systemID: String,
        @Path("userEmail") userEmail: String
    ): Call<UserBoundary>

    @POST("ambient-intelligence/users")
    fun register(@Body newUserBoundary: NewUserBoundary): Call<UserBoundary>

    @PUT("ambient-intelligence/users/{systemID}/{userEmail}")
    fun updateUser(
        @Path("systemID") systemID: String,
        @Path("userEmail") userEmail: String,
        @Body updateRequest: UserBoundary
    ): Call<Void>

    @GET("ambient-intelligence/objects/search/byType/{type}")
    fun getObjectsByType(
        @Path("type") type: String,
        @Query("userSystemID") userSystemID: String,
        @Query("userEmail") userEmail: String,
        @Query("size") size: Int = 15,
        @Query("page") page: Int = 0
    ): Call<Array<Any>>

    @POST("ambient-intelligence/commands")
    fun executeCommand(
        @Body commandRequest: CommandRequest
    ): Call<Any>
}

/**
 * Data Class
 */

data class NewUserBoundary(
    val email: String,
    val username: String,
    val role: String,
    val avatar: String
)

data class UserBoundary(
    val userId: UserId,
    val role: String,
    val username: String,
    val avatar: String
)

data class ObjectBoundaryResponse(
    val objectId: ObjectId,
    val type: String,
    val alias: String,
    val status: String,
    val active: Boolean,
    val creationTimestamp: String,
    val createdBy: CreatedBy,
    val objectDetails: Map<String, Any>
)

data class CommandRequest(
    val id: CommandId,
    val command: String,
    val targetObject: TargetObject,
    val invocationTimestamp: Date?,
    val invokedBy: InvokedBy,
    val commandAttributes: Map<String, Any>
)