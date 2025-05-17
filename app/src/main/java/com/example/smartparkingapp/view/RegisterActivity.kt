package com.example.smartparkingapp.view
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.smartparkingapp.R
import com.example.smartparkingapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var avatarString: String = "" // Default empty avatar string

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRoleDropdown()
        setupAvatarDropdown()
        setupListeners()
    }

    private fun setupRoleDropdown() {
        val roles = arrayOf(
            getString(R.string.role_admin),
            getString(R.string.role_operator),
            getString(R.string.role_end_user)
        )
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, roles)
        binding.dropdownRole.setAdapter(adapter)
    }

    private fun setupAvatarDropdown() {
        val avatars = arrayOf("boy", "girl", "man", "woman")
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, avatars)
        binding.dropdownAvatar.setAdapter(adapter)

        binding.dropdownAvatar.setOnItemClickListener { _, _, position, _ ->
            val selectedAvatar = avatars[position]
            avatarString = selectedAvatar

            // Set the appropriate image based on selection
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
    }

    private fun setupListeners() {
        // Setup register button
        binding.btnRegister.setOnClickListener {
            // Collect form data including the avatarString
            val email = binding.etEmail.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val role = binding.dropdownRole.text.toString()

            // Here you would pass these values to your controller
            // For now, we just finish the activity
            finish()
        }

        // Setup back button
        binding.btnBack.setOnClickListener {
            finish() // Close this activity and return to the previous one
        }
    }
}
