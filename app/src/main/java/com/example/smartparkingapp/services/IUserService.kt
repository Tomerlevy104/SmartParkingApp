package com.example.smartparkingapp.services

import com.example.smartparkingapp.model.UserModel

interface IUserService {

    /**
     * Registers a new user with the provided information
     *
     * @param email Email for the new user
     * @param role Role for the new user
     * @param username Username for the new user
     * @param avatar Avatar for the new user
     * @return The newly created User object
     */
    fun register(email: String, role: String, username: String, avatar: String): UserModel

    /**
     * Authenticates a user with the given system ID and email
     *
     * @param systemId The system ID to authenticate with
     * @param email The email to authenticate with
     * @return User object if authentication is successful
     */
    fun login(systemId: String, email: String): UserModel

    /**
     * Updates a specific user's profile
     *
     * @param userId The ID of the user to update
     * @param email New email (optional)
     * @param username New username (optional)
     * @param role New role (optional)
     * @param avatar New avatar (optional)
     * @return The updated User object
     */
    fun updateUser(
        userEmail: String,
        systemID: String,
        role: String? = null,
        username: String? = null,
        avatar: String? = null
    )
}