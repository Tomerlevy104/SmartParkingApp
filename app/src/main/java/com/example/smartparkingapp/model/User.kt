package com.example.smartparkingapp.model

/**
 * User data model representing a user in the application.
 */

data class User(
    val email: String = "",
    val username: String = "",
    var role: String = "",
    var avatar: String = ""
)