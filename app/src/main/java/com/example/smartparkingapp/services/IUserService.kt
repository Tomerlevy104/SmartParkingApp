package com.example.smartparkingapp.services

import com.example.smartparkingapp.model.UserModel

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
    fun login(systemId: String, email: String): UserModel

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
    fun register(email: String, role: String, username: String, avatar: String): UserModel

    /**
     * Updates a specific user's profile
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
        userEmail: String,
        systemID: String,
        role: String? = null,
        username: String? = null,
        avatar: String? = null
    )

//    fun refreshCurrentUserProfile(currentUser: User): User
    fun refreshCurrentUserProfile(): UserModel

    /**
     * Gets the currently logged in user, if any
     *
     * @return The current User object, or null if no user is logged in
     */
    fun getCurrentUser(): UserModel?

    /**
     * Logs out the current user
     *
     * @return Boolean indicating if the logout was successful
     */
    fun logout(): Boolean

}