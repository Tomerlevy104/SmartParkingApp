package com.example.smartparkingapp.services

import com.example.smartparkingapp.model.User

interface IUserService {
    /**
     * Authenticates a user with the given system ID and email
     *
     * @param systemId The system ID to authenticate with
     * @param email The email to authenticate with
     * @return User object if authentication is successful
     * @throws IllegalArgumentException if parameters are invalid
     * @throws Exception if authentication fails for other reasons
     */
    fun login(systemId: String, email: String): User

    /**
     * Registers a new user with the provided information
     *
     * @param email Email for the new user
     * @param username Username for the new user
     * @param role Role for the new user
     * @param avatar Avatar for the new user
     * @return The newly created User object
     * @throws IllegalArgumentException if parameters are invalid
     * @throws Exception if registration fails for other reasons
     */
    fun register(email: String, username: String, role: String, avatar: String): User

    /**
     * Gets the currently logged in user, if any
     *
     * @return The current User object, or null if no user is logged in
     */
    fun getCurrentUser(): User?

    /**
     * Logs out the current user
     *
     * @return Boolean indicating if the logout was successful
     */
    fun logout(): Boolean
}