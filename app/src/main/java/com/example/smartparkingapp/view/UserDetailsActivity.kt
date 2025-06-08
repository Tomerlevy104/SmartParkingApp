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
import com.example.smartparkingapp.model.User
import com.example.smartparkingapp.services.impl.UserServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var userController: UserController
    private var currentUser: User? = null
    private var selectedAvatar: String = "default"
    private val SYSTEMID : String = "2025b.integrative.smartParking"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeController()
        setupRoleDropdown()
        setupAvatarDropdown()
        setupListeners()

        // Getting user data from the Intent
        getUserFromIntent()

        // הצגת נתוני המשתמש בממשק ושמירה בשירות
        if (currentUser != null) {
//            // שמירת המשתמש הנוכחי בשירות
//            userController.setCurrentUser(currentUser!!)
            Log.d("UserDetailsActivity", "onCreate before displayUserDetails")
            displayUserDetails(currentUser!!)
        } else {
            // No user loged in
            Toast.makeText(
                this,
                "Error: No user information found in Intent",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    /**
     * Initialize the controller with user service
     */
    private fun initializeController() {
        val userService = UserServiceImpl()
        userController = UserController(userService)
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
     * Get user details from intent extras
     */
    private fun getUserFromIntent() {
        // Getting all the data from the Intent
        val email = intent.getStringExtra("USER_EMAIL")
        val username = intent.getStringExtra("USER_USERNAME")
        val role = intent.getStringExtra("USER_ROLE")
        val avatar = intent.getStringExtra("USER_AVATAR")

        if (email != null && username != null && role != null) {
            currentUser = User(
                email = email,
                username = username,
                role = role,
                avatar = avatar ?: "default"
            )

            Log.d(
                "UserDetailsActivity",
                "Received from intent: email=$email, username=$username, role=$role, avatar=$avatar"
            )
        } else {
            Log.d(
                "UserDetailsActivity",
                "Missing user data in Intent: email=$email, username=$username, role=$role"
            )
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
    private fun displayUserDetails(user: User) {
        // Email
        binding.tvEmailValue.text = user.email
        // Username
        binding.tvUsernameValue.text = user.username
        // Role
        binding.dropdownRole.setText(user.role, false)
        // Avatar
        selectedAvatar = user.avatar
        binding.dropdownAvatar.setText(user.avatar, false)
        updateAvatarPreview(user.avatar)
    }

    /**
     * Update user details to server
     */
    private fun updateUser() {
        val updatedRole = binding.dropdownRole.text.toString().trim()
        val updatedUsername = binding.tvUsernameValue.text.toString().trim()
        val updatedAvatar = selectedAvatar

        if (updatedRole.isEmpty()) {
            binding.dropdownRole.error = "Please select a role"
            return
        }

        val userEmail = currentUser?.email
        if (userEmail == null) {
            Toast.makeText(
                this@UserDetailsActivity,
                "Error: User email is missing",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        showLoading(true)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d(
                    "UserDetailActivity",
                    "Saving user details - email: $userEmail, role: $updatedRole, avatar: $updatedAvatar"
                )

                // עדכון פרופיל המשתמש
                val updatedUser = userController.updateUser(
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

                    // סגירת אקטיביטי וחזרה למסך הקודם
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    hideLoading()
                    Toast.makeText(
                        this@UserDetailsActivity,
                        "Failed to update profile: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * Show loading indicator
     */
    private fun showLoading(show: Boolean) {
        if (show) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSave.isEnabled = false
            binding.btnSave.text = "Saving..."
        }
    }

    /**
     * Hide loading indicator
     */
    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnSave.isEnabled = true
        binding.btnSave.text = getString(R.string.save)
    }
}