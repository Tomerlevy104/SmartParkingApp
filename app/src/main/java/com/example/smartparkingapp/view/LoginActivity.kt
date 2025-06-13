package com.example.smartparkingapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartparkingapp.R
import com.example.smartparkingapp.controller.UserController
import com.example.smartparkingapp.databinding.ActivityLoginBinding
import com.example.smartparkingapp.services.impl.UserServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userController: UserController
    private lateinit var userService: UserServiceImpl
    private val SYSTEMID = "2025b.integrative.smartParking"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the service and controller
        userService = UserServiceImpl()
        userController = UserController(userService)

        // Set the default system ID and make it read-only (gray)
        binding.etSystemId.setText(SYSTEMID)
        binding.etSystemId.isEnabled = false

        setupListeners()
    }

    private fun setupListeners() {
        // Setup login button
        binding.btnLogin.setOnClickListener {
            binding.btnLogin.isEnabled = false
            binding.btnLogin.text = "Logging in..."
            val systemId = SYSTEMID
            val userEmail = binding.etUserEmail.text.toString().trim()

            // Perform login in background
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Call the controller to handle login
                    val user = userController.login(systemId, userEmail)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()

                        // Navigate to UrbanZoneActivity after successful login
                        val intent = Intent(this@LoginActivity, UrbanZoneActivity::class.java)
                        intent.putExtra("USER_EMAIL", user.email)
                        intent.putExtra("USER_USERNAME", user.username)
                        intent.putExtra("USER_ROLE", user.role)
                        intent.putExtra("USER_AVATAR", user.avatar)
                        Log.d("LoginActivity","User logged in - Email: ${user.email}, Username: ${user.username}, Role: ${user.role}, Avatar: ${user.avatar}")
                        startActivity(intent)
                        finish() // Close LoginActivity
                    }

                } catch (e: IllegalArgumentException) {
                    withContext(Dispatchers.Main) {
                        binding.btnLogin.isEnabled = true
                        binding.btnLogin.text = getString(R.string.Login)
                        Toast.makeText(this@LoginActivity, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
                        // Handle validation errors
                        when {
                            e.message?.contains("email", ignoreCase = true) == true -> {
                                binding.etUserEmail.error = e.message
                            }
                            else -> {
                                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        // Handle other errors
                        Toast.makeText(this@LoginActivity, getString(R.string.error_login_failed), Toast.LENGTH_SHORT).show()
                    }
                }
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