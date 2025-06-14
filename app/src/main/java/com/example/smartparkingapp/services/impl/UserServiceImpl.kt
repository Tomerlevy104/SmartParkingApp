package com.example.smartparkingapp.services.impl

import android.util.Log
import com.example.smartparkingapp.ValidationException
import com.example.smartparkingapp.model.UserModel
import com.example.smartparkingapp.services.IUserService
import com.example.smartparkingapp.api.NewUserBoundary
import com.example.smartparkingapp.api.RetrofitClient
import com.example.smartparkingapp.api.UserBoundary
import com.example.smartparkingapp.model.utils.UserId
import com.example.smartparkingapp.ObjectAndUserConverter

/**
 * User service implementation for API operations
 */
class UserServiceImpl : IUserService {

    private var currentUser: UserModel? = null
    private val converter = ObjectAndUserConverter()

    /**
     * Registers a new user
     */
    override fun register(
        email: String,
        role: String,
        username: String,
        avatar: String
    ): UserModel {
        // Validation Input
        validateRegistrationInput(email, username, role)

        try {
            // Create the newUserBoundary object
            val newUserBoundary = NewUserBoundary(
                email = email,
                role = role,
                username = username,
                avatar = avatar
            )

            // Send POST request to server
            val serverResponse = RetrofitClient.apiService.register(newUserBoundary).execute()
            Log.d("UserServiceImpl", "user boundary: ${serverResponse}")

            // Check if request was successful
            if (serverResponse.isSuccessful) {
                val userBoundary = serverResponse.body()
                    ?: throw Exception("Registration failed: Server returned empty response")

                // Convert server response to UserModel
                val user = converter.convertServerResponseToUserModel(userBoundary)

                // Save the new user
                currentUser = user
                Log.d("UserServiceImpl", "User registered successfully: ${user.email}")

                return user
            } else {
                // Handle server errors
                when (serverResponse.code()) {
                    400 -> throw Exception("Invalid registration data - check your input")
                    409 -> throw ValidationException("email", "User already exists with this email")
                    500 -> throw Exception("Server error - please try again later")
                    else -> throw Exception("Registration failed: ${serverResponse.code()} - ${serverResponse.message()}")
                }
            }
        } catch (e: ValidationException) {
            // Re-throw validation exceptions
            throw e
        } catch (e: Exception) {
            // Throw error with clear message
            throw Exception("Registration failed: ${e.message}")
        }
    }

    /**
     * User login function
     */
    override fun login(systemId: String, email: String): UserModel {
        try {
            Log.d("UserServiceImpl", "Attempting login for: $email with systemId: $systemId")

            // Send GET request to server
            val serverResponse = RetrofitClient.apiService.login(systemId, email).execute()

            if (serverResponse.isSuccessful) {
                val userBoundary = serverResponse.body()
                    ?: throw Exception("Login failed: Server returned empty response")

                Log.d("UserServiceImpl", "Received response from server: $userBoundary")

                // Convert server response to UserModel
                val user = converter.convertServerResponseToUserModel(userBoundary)

                // Save the logged in user
                currentUser = user
                Log.d("UserServiceImpl", "User logged in successfully: ${user.email}")

                return user
            } else {
                // Handle server errors
                val errorBody = serverResponse.errorBody()?.string()
                Log.e(
                    "UserServiceImpl",
                    "Login failed: ${serverResponse.code()}, Error: $errorBody"
                )

                when (serverResponse.code()) {
                    400 -> throw Exception("Invalid login data - check your input")
                    401 -> throw Exception("Authentication failed - invalid credentials")
                    404 -> throw Exception("User not found")
                    500 -> throw Exception("Server error - please try again later")
                    else -> throw Exception("Login failed: ${serverResponse.code()} - ${serverResponse.message()}")
                }
            }
        } catch (e: Exception) {
            Log.e("UserServiceImpl", "Login exception: ${e.message}", e)
            // Throw error with clear message
            throw Exception("Login failed: ${e.message}")
        }
    }

    /**
     * Update user details function
     */
    override fun updateUser(
        userEmail: String,
        systemID: String,
        role: String,
        username: String,
        avatar: String
    ) {
        try {
            val userBoundary = UserBoundary(
                userId = UserId(email = userEmail, systemID = systemID),
                role = role,
                username = username,
                avatar = avatar
            )

            Log.d("UserServiceImpl", "Updating user: $userEmail with data: $userBoundary")

            // Send PUT request to server
            val serverResponse = RetrofitClient.apiService.updateUser(
                systemID = systemID,
                userEmail = userEmail,
                updateRequest = userBoundary
            ).execute()
            Log.d("UserServiceImpl", "$serverResponse")

            // Response from server
            if (serverResponse.isSuccessful) {
                Log.d("UserServiceImpl", "User updated successfully, refreshing profile")
                currentUser?.avatar ?: userBoundary.avatar
                currentUser?.username ?: userBoundary.username
                currentUser?.role ?: userBoundary.role
            } else {
                // Print error
                val errorBody = serverResponse.errorBody()?.string()
                Log.e(
                    "UserServiceImpl",
                    "Update failed: ${serverResponse.code()}, Error: $errorBody"
                )

                when (serverResponse.code()) {
                    400 -> throw Exception("Invalid update data - check your input")
                    401 -> throw Exception("Unauthorized - please login again")
                    404 -> throw Exception("User not found")
                    500 -> throw Exception("Server error - please try again later")
                    else -> throw Exception("Failed to update user profile: ${serverResponse.code()} - ${serverResponse.message()}")
                }
            }
        } catch (e: Exception) {
            Log.e("UserServiceImpl", "Update exception: ${e.message}", e)
            throw Exception("Failed to update user profile: ${e.message}")
        }
    }

    /**
     * Function for input validation
     */
    private fun validateRegistrationInput(email: String, username: String, role: String) {
        // Email validation
        if (email.isBlank()) {
            throw ValidationException("email", "Email is required")
        }
        if (!isValidEmail(email)) {
            throw ValidationException("email", "Please enter a valid email address")
        }

        // Username validation
        if (username.isBlank()) {
            throw ValidationException("username", "Username is required")
        }
        if (username.length < 3) {
            throw ValidationException("username", "Username must be at least 3 characters long")
        }

        // Role validation
        if (role.isBlank()) {
            throw ValidationException("role", "Please select a role")
        }

        // Check if role is valid
        val validRoles = listOf("ADMIN", "OPERATOR", "END_USER")
        if (role !in validRoles) {
            throw ValidationException(
                "role",
                "Invalid role. Must be one of: ${validRoles.joinToString(", ")}"
            )
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
