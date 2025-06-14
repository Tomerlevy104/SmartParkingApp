package com.example.smartparkingapp.controller

import com.example.smartparkingapp.model.UserModel
import com.example.smartparkingapp.services.IUserService

/**
 * Controller for managing user operations.
 * Handles user registration, login, and profile updates.
 */
class UserController(private val userService: IUserService) {

    fun register(email: String, role: String, username: String, avatar: String): UserModel {
        return userService.register(email, role, username, avatar)
    }

    fun login(systemId: String, email: String): UserModel {
        return userService.login(systemId, email)
    }

    fun updateUser(
        userEmail: String,
        systemID: String,
        role: String,
        username: String,
        avatar: String
    ) {
        userService.updateUser(userEmail, systemID, role, username, avatar)
    }
}