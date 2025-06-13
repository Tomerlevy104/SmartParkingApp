package com.example.smartparkingapp.services.impl

import android.util.Log
import com.example.smartparkingapp.model.UserModel
import com.example.smartparkingapp.services.IUserService
import com.example.smartparkingapp.api.RegisterRequest
import com.example.smartparkingapp.api.UpdateUserRequest
import com.example.smartparkingapp.api.RetrofitClient
import com.example.smartparkingapp.model.util.UserId
import com.example.smartparkingapp.utils.ObjectAndUserConverter

class UserServiceImpl : IUserService {

    private var currentUser: UserModel? = null
    private val converter = ObjectAndUserConverter()

    override fun register(email: String, role: String, username: String, avatar: String): UserModel {
        // Validation Input
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
                val serverResponse = response.body()
                    ?: throw Exception("Registration failed: Server returned empty response")

                // Convert server response to UserModel
                val user = converter.convertServerResponseToUserModel(serverResponse)

                // Save the new user
                currentUser = user
                Log.d("UserServiceImpl", "User registered successfully: ${user.email}")

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

    override fun login(systemId: String, email: String): UserModel {
        try {
            Log.d("UserServiceImpl", "Attempting login for: $email with systemId: $systemId")

            // Call login API endpoint
            val response = RetrofitClient.apiService.login(systemId, email).execute()

            if (response.isSuccessful) {
                val serverResponse = response.body()
                    ?: throw Exception("Login failed: Server returned empty response")

                Log.d("UserServiceImpl", "Received response from server: $serverResponse")

                // Convert server response to UserModel
                val user = converter.convertServerResponseToUserModel(serverResponse)

                // Save the logged in user
                currentUser = user
                Log.d("UserServiceImpl", "User logged in successfully: ${user.email}")

                return user
            } else {
                // Handle server errors
                val errorBody = response.errorBody()?.string()
                Log.e("UserServiceImpl", "Login failed: ${response.code()}, Error: $errorBody")

                when (response.code()) {
                    400 -> throw Exception("Invalid login data - check your input")
                    401 -> throw Exception("Authentication failed - invalid credentials")
                    404 -> throw Exception("User not found")
                    500 -> throw Exception("Server error - please try again later")
                    else -> throw Exception("Login failed: ${response.code()} - ${response.message()}")
                }
            }
        } catch (e: Exception) {
            Log.e("UserServiceImpl", "Login exception: ${e.message}", e)
            // Throw error with clear message
            throw Exception("Login failed: ${e.message}")
        }
    }

    override fun updateUser(
        userEmail: String,
        systemId: String,
        role: String?,
        username: String?,
        avatar: String?
    ) {
        try {
            val updateRequest = UpdateUserRequest(
                userId = UserId(email = userEmail, systemID = systemId),
                role = role,
                username = username,
                avatar = avatar
            )

            Log.d("UserServiceImpl", "Updating user: $userEmail with data: $updateRequest")

            // Send update request to server
            val response = RetrofitClient.apiService.updateUser(
                systemID = systemId,
                userEmail = userEmail,
                updateRequest = updateRequest
            ).execute()
            Log.d("UserServiceImpl","${response}")

            // Response from server
            if (response.isSuccessful) {
                Log.d("UserServiceImpl", "User updated successfully, refreshing profile")
                currentUser?.avatar ?: updateRequest.avatar
                currentUser?.username ?: updateRequest.username
                currentUser?.role ?: updateRequest.role
            } else {
                // Print error
                val errorBody = response.errorBody()?.string()
                Log.e("UserServiceImpl", "Update failed: ${response.code()}, Error: $errorBody")

                when (response.code()) {
                    400 -> throw Exception("Invalid update data - check your input")
                    401 -> throw Exception("Unauthorized - please login again")
                    404 -> throw Exception("User not found")
                    500 -> throw Exception("Server error - please try again later")
                    else -> throw Exception("Failed to update user profile: ${response.code()} - ${response.message()}")
                }
            }
        } catch (e: Exception) {
            Log.e("UserServiceImpl", "Update exception: ${e.message}", e)
            throw Exception("Failed to update user profile: ${e.message}")
        }
    }

    // Function for input validation
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

/**
 * Custom exception for field-specific validation errors
 */
class ValidationException(
    val field: String,
    message: String
) : Exception(message)