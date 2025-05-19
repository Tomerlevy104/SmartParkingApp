package com.example.smartparkingapp.services.impl


import com.example.smartparkingapp.model.User
import com.example.smartparkingapp.services.IUserService
import com.example.smartparkingapp.api.RegisterRequest
import com.example.smartparkingapp.api.RetrofitClient

// Custom exception for field-specific validation errors
class ValidationException(
    val field: String,
    message: String
) : Exception(message)

class UserServiceImpl : IUserService {

    private var currentUser: User? = null

    override fun register(email: String, username: String, role: String, avatar: String): User {
        // Complete input validation with field-specific errors
        validateRegistrationInput(email, username, role)

        try {
            // Create the request object
            val registerRequest = RegisterRequest(
                email = email,
                username = username,
                role = role,
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
            throw ValidationException("role", "Invalid role. Must be one of: ${validRoles.joinToString(", ")}")
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Basic implementation of other functions (not relevant for now)
    override fun login(systemId: String, email: String): User {
        TODO("Login not implemented yet")
    }

    override fun getCurrentUser(): User? {
        return currentUser
    }

    override fun logout(): Boolean {
        currentUser = null
        return true
    }

    override fun refreshCurrentUserProfile(): User {
        TODO("Not implemented yet")
    }

    override fun getUserById(userId: String): User {
        TODO("Not implemented yet")
    }

    override fun updateCurrentUserProfile(email: String?, username: String?, role: String?, avatar: String?): User {
        TODO("Not implemented yet")
    }

    override fun updateUser(userId: String, email: String?, username: String?, role: String?, avatar: String?): User {
        TODO("Not implemented yet")
    }
}