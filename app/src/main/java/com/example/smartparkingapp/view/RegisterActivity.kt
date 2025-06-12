package com.example.smartparkingapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartparkingapp.R
import com.example.smartparkingapp.controller.UserController
import com.example.smartparkingapp.databinding.ActivityRegisterBinding
import com.example.smartparkingapp.model.UserModel
import com.example.smartparkingapp.services.impl.UserServiceImpl
import com.example.smartparkingapp.services.impl.ValidationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var avatarString: String = "default"
    private lateinit var userController: UserController
    private var currentUser: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the Controller
        initializeController()

        setupRoleDropdown()
        setupAvatarDropdown()
        setupListeners()
    }

    private fun initializeController() {
        val userService = UserServiceImpl()
        userController = UserController(userService)
    }

    private fun setupRoleDropdown() {
        val roles = arrayOf("ADMIN", "OPERATOR", "END_USER")
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, roles)
        binding.dropdownRole.setAdapter(adapter)
        // Set default selection
        binding.dropdownRole.setText("Please select Role", false)
    }

    private fun setupAvatarDropdown() {
        val avatars = arrayOf("boy", "girl", "man", "woman")
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, avatars)
        binding.dropdownAvatar.setAdapter(adapter)

        binding.dropdownAvatar.setOnItemClickListener { _, _, position, _ ->
            val selectedAvatar = avatars[position]
            avatarString = selectedAvatar

            // Update image based on selection
            val resourceId = when (selectedAvatar) {
                "boy" -> R.drawable.avatar_boy
                "girl" -> R.drawable.avatar_girl
                "man" -> R.drawable.avatar_man
                "woman" -> R.drawable.avatar_woman
                else -> R.drawable.avatar_default
            }

            binding.ivAvatarPreview.setImageResource(resourceId)
            binding.tvAvatarSelected.text = "Selected avatar: $selectedAvatar"
        }

        // Set default selection
        binding.dropdownAvatar.setText("Please select Avatar", false)
        binding.ivAvatarPreview.setImageResource(R.drawable.avatar_default)
        binding.tvAvatarSelected.text = "Selected avatar: default"
    }

    private fun setupListeners() {
        // Register button
        binding.btnRegister.setOnClickListener {
            performRegistration()
        }

        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun performRegistration() {
        // Collect data from form
        val email = binding.etEmail.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val role = binding.dropdownRole.text.toString().trim()
        val avatar = binding.dropdownAvatar.text.toString().trim()

        // Clear previous errors
        clearFieldErrors()

        // Show loading indicator
        showLoading(true)

        // Perform registration in background
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Call Controller - it will handle validation
                val user = userController.register(email, role, username, avatarString)

                // Store the registered user
                currentUser = user


                // Return to Main thread to update UI
                withContext(Dispatchers.Main) {
                    hideLoading()
                    handleRegistrationSuccess(email, username, role, avatar)

                }
            } catch (e: ValidationException) {
                // Field-specific validation errors from service
                withContext(Dispatchers.Main) {
                    hideLoading()
                    handleFieldValidationError(e.field, e.message ?: "Invalid input")
                }
            } catch (e: IllegalArgumentException) {
                // General validation errors from service
                withContext(Dispatchers.Main) {
                    hideLoading()
                    handleValidationError(e.message)
                }
            } catch (e: Exception) {
                // Network or server errors
                withContext(Dispatchers.Main) {
                    hideLoading()
                    handleRegistrationError(e.message)
                }
            }
        }
    }

    // Clear previous errors from form
    private fun clearFieldErrors() {
        binding.etEmail.error = null
        binding.etUsername.error = null
        binding.dropdownRole.error = null
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            binding.btnRegister.isEnabled = false
            binding.btnRegister.text = "Registering..."
        }
    }

    private fun hideLoading() {
        binding.btnRegister.isEnabled = true
        binding.btnRegister.text = getString(R.string.register)
    }

    private fun handleRegistrationSuccess(
        email: String,
        username: String,
        role: String,
        avatar: String
    ) {

        // Show success message
        Toast.makeText(this, "Registration successful! Welcome ${username}!", Toast.LENGTH_LONG)
            .show()

        // Pass user details to UrbanZoneActivity
        val intent = Intent(this, UrbanZoneActivity::class.java)
        intent.putExtra("USER_EMAIL", email)
        intent.putExtra("USER_USERNAME", username)
        intent.putExtra("USER_ROLE", role)
        intent.putExtra("USER_AVATAR", avatar)
        startActivity(intent)
        finish() // Close this activity
    }

    private fun handleFieldValidationError(field: String, message: String) {
        // Clear all previous errors
        clearFieldErrors()

        // Set error on specific field with red border
        when (field) {
            "email" -> binding.etEmail.error = message
            "username" -> binding.etUsername.error = message
            "role" -> binding.dropdownRole.error = message
        }
    }

    private fun handleValidationError(message: String?) {
        val errorMessage = message ?: "Please check your input"
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun handleRegistrationError(message: String?) {
        val errorMessage = message ?: "Registration failed. Please try again."
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
}