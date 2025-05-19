package com.example.smartparkingapp.model

/**
 * User data model representing a user in the application.
 */

data class User(
    val email: String = "",
    val username: String = "",
    val role: String = "",
    private val avatar: String = ""
)