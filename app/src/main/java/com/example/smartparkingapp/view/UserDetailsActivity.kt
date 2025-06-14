package com.example.smartparkingapp.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartparkingapp.R
import com.example.smartparkingapp.controller.UserController
import com.example.smartparkingapp.databinding.ActivityUserDetailsBinding
import com.example.smartparkingapp.model.UserModel
import com.example.smartparkingapp.services.impl.UserServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * User Details screen activity
 */
class UserDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var userController: UserController
    private var currentUser: UserModel? = null
    private val SYSTEMID: String = "2025b.integrative.smartParking"
    private var selectedAvatar: String = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeController()
        setupRoleDropdown()
        setupAvatarDropdown()
        setupListeners()

        // Load user from server
        getCurrentUserFromService()
    }

    /**
     * Initialize the controller with user service
     */
    private fun initializeController() {
        val userService = UserServiceImpl()
        userController = UserController(userService)
    }

    /**
     * Get current user from UserService
     */
    private fun getCurrentUserFromService() {
        val email = intent.getStringExtra("USER_EMAIL")

        if (email == null) {
            Toast.makeText(this, "Error: No email provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        showLoading(true)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                currentUser = userController.login(SYSTEMID, email)

                withContext(Dispatchers.Main) {
                    hideLoading()

                    if (currentUser != null) {
                        Log.d("UserDetailsActivity", "Got user: ${currentUser!!.email}")
                        displayUserDetails(currentUser!!)
                    } else {
                        Toast.makeText(
                            this@UserDetailsActivity,
                            "Error: Login failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    hideLoading()
                    Log.e("UserDetailsActivity", "Login failed: ${e.message}", e)
                    Toast.makeText(
                        this@UserDetailsActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }

    /**
     * Setup click listeners for buttons
     */
    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            updateUser()
        }

        binding.btnBack.setOnClickListener {
            finish() // Close this activity
        }
    }

    /**
     * Display user details in the UI
     */
    private fun displayUserDetails(user: UserModel) {
        // Email
        binding.tvEmailValue.text = user.email

        // Username
        binding.etUsername.setText(user.username)

        // Role
        binding.dropdownRole.setText(user.role, false)

        // Avatar
        selectedAvatar = user.avatar
        binding.dropdownAvatar.setText(user.avatar, false)
        updateAvatarPreview(user.avatar)
    }

    /**
     * Update user details in server
     */
    private fun updateUser() {
        val updatedRole = binding.dropdownRole.text.toString().trim()
        val updatedUsername = binding.etUsername.text.toString().trim()
        val updatedAvatar = selectedAvatar

        // Validation
        if (updatedRole.isEmpty()) {
            binding.dropdownRole.error = "Please select a role"
            return
        }

        if (updatedUsername.isEmpty()) {
            binding.etUsername.error = "Username cannot be empty"
            return
        }

        if (updatedUsername.length < 3) {
            binding.etUsername.error = "Username must be at least 3 characters"
            return
        }

        val userEmail = currentUser?.email
        if (userEmail == null) {
            Toast.makeText(
                this@UserDetailsActivity,
                "Error: User email is missing",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        showLoading(true)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d(
                    "UserDetailsActivity",
                    "Updating user - email: $userEmail, username: $updatedUsername, role: $updatedRole, avatar: $updatedAvatar"
                )

                // Update user profile in server side
                userController.updateUser(
                    userEmail = userEmail,
                    systemID = SYSTEMID,
                    role = updatedRole,
                    username = updatedUsername,
                    avatar = updatedAvatar
                )

                withContext(Dispatchers.Main) {
                    hideLoading()
                    Toast.makeText(
                        this@UserDetailsActivity,
                        "Profile updated successfully!",
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.d(
                        "UserDetailsActivity",
                        "Profile updated successfully: ${currentUser!!.email}, ${currentUser!!.username}"
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    hideLoading()
                    Toast.makeText(
                        this@UserDetailsActivity,
                        "Failed to update profile: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("UserDetailsActivity", "Update failed: ${e.message}", e)
                }
            }
        }
    }

    /**
     * Setup the role dropdown with available roles
     */
    private fun setupRoleDropdown() {
        val roles = arrayOf("ADMIN", "OPERATOR", "END_USER")
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, roles)
        binding.dropdownRole.setAdapter(adapter)
    }

    /**
     * Update the avatar preview image based on selected avatar name
     */
    private fun updateAvatarPreview(avatarName: String) {
        val resourceId = when (avatarName) {
            "boy" -> R.drawable.avatar_boy
            "girl" -> R.drawable.avatar_girl
            "man" -> R.drawable.avatar_man
            "woman" -> R.drawable.avatar_woman
            else -> R.drawable.avatar_default
        }
        binding.ivAvatarPreview.setImageResource(resourceId)
    }

    /**
     * Setup the avatar dropdown with available avatars and preview
     */
    private fun setupAvatarDropdown() {
        val avatars = arrayOf("boy", "girl", "man", "woman")
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, avatars)
        binding.dropdownAvatar.setAdapter(adapter)

        // Handle avatar selection
        binding.dropdownAvatar.setOnItemClickListener { _, _, position, _ ->
            val selectedAvatarName = avatars[position]
            selectedAvatar = selectedAvatarName
            updateAvatarPreview(selectedAvatarName)
        }
    }

    /**
     * Show loading indicator
     */
    private fun showLoading(show: Boolean) {
        if (show) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSave.isEnabled = false
            binding.btnSave.text = "Loading..."

            // Disable all input fields during loading
            binding.etUsername.isEnabled = false
            binding.dropdownRole.isEnabled = false
            binding.dropdownAvatar.isEnabled = false
        }
    }

    /**
     * Hide loading indicator
     */
    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnSave.isEnabled = true
        binding.btnSave.text = getString(R.string.save)

        // Re-enable all input fields
        binding.etUsername.isEnabled = true
        binding.dropdownRole.isEnabled = true
        binding.dropdownAvatar.isEnabled = true
    }
}