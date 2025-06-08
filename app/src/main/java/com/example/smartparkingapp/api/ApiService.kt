// api/ApiService.kt - Fix the login method and adapt register for Spring server
package com.example.smartparkingapp.api

import com.example.smartparkingapp.model.UrbanZoneModel
import com.example.smartparkingapp.model.User
import com.example.smartparkingapp.model.util.CreatedBy
import com.example.smartparkingapp.model.util.ObjectId
import com.example.smartparkingapp.model.util.UserId
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("ambient-intelligence/users/login/{systemID}/{userEmail}")
    fun login(@Path("systemID") systemID: String, @Path("userEmail") userEmail: String): Call<User>

    @POST("ambient-intelligence/users")
    fun register(@Body registerRequest: RegisterRequest): Call<User>

    @PUT("ambient-intelligence/users/{systemID}/{userEmail}")
    fun updateUser(
        @Path("systemID") systemID: String,
        @Path("userEmail") userEmail: String,
        @Body updateRequest: UpdateUserRequest
    ): Call<Void>

    @GET("ambient-intelligence/objects/search/byType/{type}")
    fun getObjectsByType(
        @Path("type") type: String,
        @Query("userSystemID") userSystemID: String,
        @Query("userEmail") userEmail: String,
        @Query("size") size: Int = 15,
        @Query("page") page: Int = 0
    ): Call<Array<Any>>
}

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

data class RegisterRequest(
    val email: String,
    val username: String,
    val role: String,
    val avatar: String
)

data class UpdateUserRequest(
    val userId: UserId? = null,
    val role: String? = null,
    val username: String? = null,
    val avatar: String? = null
)