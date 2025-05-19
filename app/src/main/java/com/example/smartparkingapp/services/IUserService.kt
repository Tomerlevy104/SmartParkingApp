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

    /**
     * Refreshes the current user's profile from the server
     *
     * @return The updated User object
     * @throws Exception if refresh fails
     */
    fun refreshCurrentUserProfile(): User

    /**
     * Gets a user by their ID from the server
     *
     * @param userId The ID of the user to retrieve
     * @return The User object if found
     * @throws IllegalArgumentException if userId is invalid
     * @throws Exception if retrieval fails
     */
    fun getUserById(userId: String): User

    /**
     * Updates the current user's profile
     *
     * @param email New email (optional)
     * @param username New username (optional)
     * @param role New role (optional)
     * @param avatar New avatar (optional)
     * @return The updated User object
     * @throws Exception if update fails
     */
    fun updateCurrentUserProfile(
        email: String? = null,
        username: String? = null,
        role: String? = null,
        avatar: String? = null
    ): User

    /**
     * Updates a specific user's profile (admin function)
     *
     * @param userId The ID of the user to update
     * @param email New email (optional)
     * @param username New username (optional)
     * @param role New role (optional)
     * @param avatar New avatar (optional)
     * @return The updated User object
     * @throws IllegalArgumentException if userId is invalid
     * @throws Exception if update fails
     */
    fun updateUser(
        userId: String,
        email: String? = null,
        username: String? = null,
        role: String? = null,
        avatar: String? = null
    ): User
}