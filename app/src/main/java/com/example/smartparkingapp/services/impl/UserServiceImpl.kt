package com.example.smartparkingapp.services.impl

import android.util.Log
import com.example.smartparkingapp.model.User
import com.example.smartparkingapp.services.IUserService
import com.example.smartparkingapp.api.RegisterRequest
import com.example.smartparkingapp.api.UpdateUserRequest
import com.example.smartparkingapp.api.RetrofitClient
import com.example.smartparkingapp.api.UserId

/**
 * Custom exception for field-specific validation errors
 */
class ValidationException(
    val field: String,
    message: String
) : Exception(message)

class UserServiceImpl : IUserService {

    private var currentUser: User? = null
    private val SYSTEMID: String = "2025b.integrative.smartParking"

    override fun register(email: String, role: String, username: String, avatar: String): User {
        // Complete input validation with field-specific errors
        validateRegistrationInput(email, username, role)

        try {
            // Create the request object
            val registerRequest = RegisterRequest(
                email = email,
                role = role,
                username = username,
                avatar = avatar
            )

            // Send POST request to server
            val response = RetrofitClient.apiService.register(registerRequest).execute()

            // Check if request was successful
            if (response.isSuccessful) {
                val user = response.body()
                    ?: throw Exception("Registration failed: Server returned empty response")

                // Save the new user (optional - if you want them to stay logged in after registration)
                currentUser = user

                return user
            } else {
                // Handle server errors
                when (response.code()) {
                    400 -> throw Exception("Invalid registration data - check your input")
                    409 -> throw ValidationException("email", "User already exists with this email")
                    500 -> throw Exception("Server error - please try again later")
                    else -> throw Exception("Registration failed: ${response.code()} - ${response.message()}")
                }
            }
        } catch (e: ValidationException) {
            // Re-throw validation exceptions as-is
            throw e
        } catch (e: Exception) {
            // Throw error with clear message
            throw Exception("Registration failed: ${e.message}")
        }
    }

    override fun login(systemId: String, email: String): User {
        try {
            // Call login API endpoint
            val response = RetrofitClient.apiService.login(systemId, email).execute()

            if (response.isSuccessful) {
                val user = response.body()
                    ?: throw Exception("Login failed: Server returned empty response")

                // Save the logged in user
                currentUser = user
                return user
            } else {
                // Handle server errors
                when (response.code()) {
                    400 -> throw Exception("Invalid login data - check your input")
                    401 -> throw Exception("Authentication failed - invalid credentials")
                    404 -> throw Exception("User not found")
                    500 -> throw Exception("Server error - please try again later")
                    else -> throw Exception("Login failed: ${response.code()} - ${response.message()}")
                }
            }
        } catch (e: Exception) {
            // Throw error with clear message
            throw Exception("Login failed: ${e.message}")
        }
    }

    override fun getCurrentUser(): User? {
        return currentUser
    }

    override fun logout(): Boolean {
        currentUser = null
        return true
    }

//    override fun refreshCurrentUserProfile(currentUser: User ): User {
    override fun refreshCurrentUserProfile(): User {

    currentUser?.let {
            return login(SYSTEMID, it.email)
        } ?: throw Exception("No logged in user")
    }

    override fun updateUser(
        userEmail: String,
        systemID: String,
        role: String?,
        username: String?,
        avatar: String?
    ): User {
        try {
            // יצירת אובייקט הבקשה במבנה הנכון
            val updateRequest = UpdateUserRequest(
                userId = UserId(email = userEmail, systemID = systemID),
                role = role,
                username = username,
                avatar = avatar
            )

            // הדפסת מידע לוג
            println("Updating user: $userEmail")
            println("With data: $updateRequest")

            // Send update request to server
            val response = RetrofitClient.apiService.updateUser(
                systemID = systemID,
                userEmail = userEmail,
                updateRequest = updateRequest
            ).execute()

            // Response from server
            if (response.isSuccessful) {
                // שרת ה-Spring מחזיר 204 No Content בהצלחה
//                currentUser?.email ?: userEmail
//                currentUser?.username ?: username
//                currentUser?.avatar ?: avatar
//                currentUser?.role ?: role
                return refreshCurrentUserProfile()
            } else {
                // Print error
                Log.d("UserServiceImpl","Server responded with error code: ${response.code()}")
                Log.d("UserServiceImpl","Response message: ${response.message()}")
                Log.d("UserServiceImpl","Response errorBody: ${response.errorBody()?.string()}")

                when (response.code()) {
                    400 -> throw Exception("Invalid update data - check your input")
                    401 -> throw Exception("Unauthorized - please login again")
                    404 -> throw Exception("User not found")
                    500 -> throw Exception("Server error - please try again later")
                    else -> throw Exception("Failed to update user profile: ${response.code()} - ${response.message()}")
                }
            }
        } catch (e: Exception) {
            println("Exception in updateUser: ${e.message}")
            e.printStackTrace()
            throw Exception("Failed to update user profile: ${e.message}")
        }
    }

//    override fun updateUser(
//        email: String?,
//        username: String?,
//        role: String?,
//        avatar: String?
//    ): User {
//
//    }

    // Helper functions for input validation - throws field-specific errors
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