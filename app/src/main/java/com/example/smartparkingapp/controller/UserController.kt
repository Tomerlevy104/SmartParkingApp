package com.example.smartparkingapp.controller

import com.example.smartparkingapp.model.User
import com.example.smartparkingapp.services.IUserService

/**
 * Controller class for handling user-related operations.
 * This controller serves as an intermediary between the UI and business logic.
 */
class UserController(private val userService: IUserService) {

    /**
     * Forwards login details to the user service
     *
     * @param systemId The system ID entered
     * @param email The email address entered
     * @return The logged in user
     * @throws IllegalArgumentException if parameters are invalid
     * @throws Exception if login fails
     */
    fun login(systemId: String, email: String): User {
        // Direct call to service for login
        return userService.login(systemId, email)
    }

    /**
     * Forwards registration details to the user service
     *
     * @param email Email address for the new user
     * @param username Username for the new user
     * @param role Role selected for the new user
     * @param avatar Avatar selected for the new user
     * @return The registered user
     * @throws IllegalArgumentException if parameters are invalid
     * @throws Exception if registration fails
     */
    fun register(email: String, username: String, role: String, avatar: String): User {
        // Direct call to service for registration
        return userService.register(email, username, role, avatar)
    }

    /**
     * Checks if a user is currently logged in
     *
     * @return Boolean value indicating if a user is logged in
     */
    fun isLoggedIn(): Boolean {
        return userService.getCurrentUser() != null
    }

    /**
     * Logs out the current user
     *
     * @return Boolean value indicating if logout was successful
     */
    fun logout(): Boolean {
        return userService.logout()
    }

    /**
     * Returns the current user, if any
     *
     * @return The current user or null if no user is logged in
     */
    fun getCurrentUser(): User? {
        return userService.getCurrentUser()
    }
}