package com.example.smartparkingapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartparkingapp.R
import com.example.smartparkingapp.controller.UserController
import com.example.smartparkingapp.databinding.ActivityLoginBinding
import com.example.smartparkingapp.services.impl.UserServiceImpl

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userController: UserController
    private lateinit var userService: UserServiceImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the service and controller
        userService = UserServiceImpl()
        userController = UserController(userService)

        setupListeners()
    }

    private fun setupListeners() {
        // Setup login button
        binding.btnLogin.setOnClickListener {
            val systemId = binding.etSystemId.text.toString().trim()
            val userEmail = binding.etUserEmail.text.toString().trim()

            try {
                // Call the controller to handle login
                userController.login(systemId, userEmail)

                Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()

                // Navigate to UrbanZoneActivity after successful login
                val intent = Intent(this, UrbanZoneActivity::class.java)
                startActivity(intent)
                finish() // Close LoginActivity
            } catch (e: IllegalArgumentException) {
                // Handle validation errors
                when {
                    e.message?.contains("System ID", ignoreCase = true) == true -> {
                        binding.etSystemId.error = e.message
                    }
                    e.message?.contains("email", ignoreCase = true) == true -> {
                        binding.etUserEmail.error = e.message
                    }
                    else -> {
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Handle other errors
                Toast.makeText(this, getString(R.string.error_login_failed), Toast.LENGTH_SHORT).show()
            }
        }

        // Setup register text click
        binding.tvRegister.setOnClickListener {
            // Navigate to RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Setup back button
        binding.btnBack.setOnClickListener {
            finish() // Close this activity and return to the previous one
        }
    }
}