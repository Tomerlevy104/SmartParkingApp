package com.example.smartparkingapp.model

/**
 * User data model representing a user in the application.
 */

data class User(
    private val email: String = "",
    private val username: String = "",
    private val role: String = "",
    private val avatar: String = ""
)