// api/ApiService.kt - Fix the login method and adapt register for Spring server
package com.example.smartparkingapp.api

import com.example.smartparkingapp.model.User
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // Login - Fixed to return Call<User>
    @GET("ambient-intelligence/users/login/{systemID}/{userEmail}")
    fun login(@Path("systemID") systemID: String, @Path("userEmail") userEmail: String): Call<User>

    // Register - Keep your current structure but ensure it matches Spring expectations
    @POST("ambient-intelligence/users")
    fun register(@Body registerRequest: RegisterRequest): Call<User>

    // Other endpoints... (keep as is for now)
    @GET("api/users/profile")
    fun getCurrentUserProfile(): Call<User>

    @PUT("api/users/profile")
    fun updateUserProfile(@Body updateRequest: UpdateUserRequest): Call<User>

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

data class UpdateUserRequest(
    val email: String? = null,
    val username: String? = null,
    val role: String? = null,
    val avatar: String? = null
)