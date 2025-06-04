// api/ApiService.kt - Fix the login method and adapt register for Spring server
package com.example.smartparkingapp.api

import com.example.smartparkingapp.model.User
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

    @POST("api/users/logout")
    fun logout(): Call<Void>
}

// Keep your current RegisterRequest - it should work with Spring
data class RegisterRequest(
    val email: String,
    val username: String,
    val role: String,
    val avatar: String
)

data class UserId(
    var email: String,
    var systemID: String
)

data class UpdateUserRequest(
    val userId: UserId? = null,
    val role: String? = null,
    val username: String? = null,
    val avatar: String? = null
)