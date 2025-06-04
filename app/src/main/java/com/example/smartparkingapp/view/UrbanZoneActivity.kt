package com.example.smartparkingapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartparkingapp.R
import com.example.smartparkingapp.controller.UserController
import com.example.smartparkingapp.databinding.ActivityUrbanZoneBinding
import com.example.smartparkingapp.model.ParkingSpot
import com.example.smartparkingapp.model.UrbanZone
import com.example.smartparkingapp.model.User
import com.example.smartparkingapp.services.impl.UserServiceImpl

class UrbanZoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUrbanZoneBinding
    private lateinit var parkingSpotAdapter: ParkingSpotAdapter
    private var currentUser: User? = null
    private lateinit var userController: UserController
    private var selectedUrbanZone: UrbanZone? = null

    // רשימת חניות לדגימה
    private val sampleParkingSpots = listOf(
        ParkingSpot(
            id = "P1",
            restrictions = "2 hour maximum",
            occupied = false,
            turnoverRate = "High",
            address = "12 Dizengoff Street",
            zoneId = "1",
            isCovered = true,
            pricePerHour = "8.00"
        ),
        ParkingSpot(
            id = "P2",
            restrictions = "No restrictions",
            occupied = false,
            turnoverRate = "Medium",
            address = "25 Ben Yehuda Street",
            zoneId = "1",
            isCovered = false,
            pricePerHour = "8.00"
        ),
        ParkingSpot(
            id = "P3",
            restrictions = "4 hour maximum",
            occupied = false,
            turnoverRate = "Low",
            address = "7 Allenby Road",
            zoneId = "1",
            isCovered = false,
            pricePerHour = "7.50"
        ),
        ParkingSpot(
            id = "P4",
            restrictions = "No restrictions",
            occupied = false,
            turnoverRate = "Medium",
            address = "33 King George Street",
            zoneId = "1",
            isCovered = true,
            pricePerHour = "9.00"
        ),
        ParkingSpot(
            id = "P5",
            restrictions = "3 hour maximum",
            occupied = false,
            turnoverRate = "Medium",
            address = "14 Rothschild Boulevard",
            zoneId = "1",
            isCovered = false,
            pricePerHour = "7.00"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUrbanZoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize user controller
        initializeUserController()

        // Get user details from intent
        getUserFromIntent()

        setupRecyclerView()
        setupButtons()

        // Create sample UrbanZone with data
        selectedUrbanZone = UrbanZone(
            id = "1",
            name = "Tel Aviv",
            description = "City Streets (Zones 1,2,4,12,13)",
            latitude = 32.0853,
            longitude = 34.7818,
            radius = 5000f,
            zoneType = "Urban",
            totalParkingSpots = 120,
            availableParkingSpots = 55,
            baseHourlyRate = 8.0,
            parkingSpots = sampleParkingSpots
        )

        updateUrbanZoneUI()
    }

    /**
     * Initialize user controller
     */
    private fun initializeUserController() {
        val userService = UserServiceImpl()
        userController = UserController(userService)
    }

    /**
     * Get user details from intent extras or controller
     */
    private fun getUserFromIntent() {
        Log.d("UrbanZoneActivity", "getUserFromIntent called")

        if (currentUser == null) {
            val email = intent.getStringExtra("USER_EMAIL")
            val username = intent.getStringExtra("USER_USERNAME")
            val role = intent.getStringExtra("USER_ROLE")
            val avatar = intent.getStringExtra("USER_AVATAR")

            Log.d("UrbanZoneActivity", "Intent extras: email=$email, username=$username, role=$role, avatar=$avatar")

            if (email != null && username != null && role != null) {
                currentUser = User(
                    email = email,
                    username = username,
                    role = role,
                    avatar = avatar ?: "default"
                )
                Log.d("UrbanZoneActivity", "User created: ${currentUser?.email}, ${currentUser?.username}")

                // Store in controller
                //userController.setCurrentUser(currentUser!!)
            }
            Log.d("UserDetailsActivity", "Received from intent: email=$email, username=$username, role=$role, avatar=$avatar")

        }

        // If still no user, redirect to welcome screen
        if (currentUser == null) {
            Toast.makeText(
                this,
                "No user login information found. Please login again.",
                Toast.LENGTH_LONG
            ).show()
            navigateToWelcome()
        }
    }

    private fun setupRecyclerView() {
        parkingSpotAdapter = ParkingSpotAdapter(emptyList())

        binding.rvParkingSpots.apply {
            layoutManager = LinearLayoutManager(this@UrbanZoneActivity)
            adapter = parkingSpotAdapter
        }
    }


    // Updates the UI with the current UrbanZone data
    fun updateUrbanZoneUI() {
        selectedUrbanZone?.let { zone ->
            binding.tvSelectedCity.text = zone.name
            binding.tvCityDescription.text = zone.description
            binding.tvTotalSpots.text = zone.totalParkingSpots.toString()
            binding.tvAvailableSpots.text = zone.availableParkingSpots.toString()

            // בהנחה שיש לך textView לתעריף שעתי
            try {
                binding.tvHourlyRate.text = "₪${zone.baseHourlyRate}0"
            } catch (e: Exception) {
                // אם אין textView כזה - התעלם
            }

            // עדכון רשימת החניות
            updateParkingSpotsList(zone.parkingSpots.filter { !it.occupied })
        }
    }


    // Updates the list of parking spaces displayed in RecyclerView
    fun updateParkingSpotsList(parkingSpots: List<ParkingSpot>) {
        parkingSpotAdapter.updateData(parkingSpots)
    }

    private fun setupButtons() {
        binding.citySelectorLayout.setOnClickListener {
            Toast.makeText(this, "City selection will be implemented later", Toast.LENGTH_SHORT)
                .show()
        }

        binding.ivSearch.setOnClickListener {
            Toast.makeText(
                this,
                "Search functionality will be implemented later",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        binding.actionButtonCard.setOnClickListener {
            Toast.makeText(this, "Parking activation will be implemented later", Toast.LENGTH_SHORT)
                .show()
        }

        binding.parkingHistoryBTN.setOnClickListener {
            Toast.makeText(this, "Parking History will be implemented later", Toast.LENGTH_SHORT)
                .show()
        }

        // Navigate to UserDetailsActivity when user details button is clicked
        binding.userDetailsBTN.setOnClickListener {
            Log.d("UrbanZoneActivity", "Current user before intent: ${currentUser?.email}, ${currentUser?.username}, ${currentUser?.role}")

            val intent = Intent(this, UserDetailsActivity::class.java)

            currentUser?.let { user ->
                intent.putExtra("USER_EMAIL", user.email)
                intent.putExtra("USER_USERNAME", user.username)
                intent.putExtra("USER_ROLE", user.role)
                intent.putExtra("USER_AVATAR", user.avatar)
            }

            startActivity(intent)
        }
    }


    // Displays parking details dialog
    fun showParkingSpotDetailsDialog(parkingSpot: ParkingSpot) {
        val message = """
            Address: ${parkingSpot.address}
            Restrictions: ${parkingSpot.restrictions}
            Rate: ₪${parkingSpot.pricePerHour} per hour
            Covered: ${if (parkingSpot.isCovered) "Yes" else "No"}
            Turnover Rate: ${parkingSpot.turnoverRate}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Parking Spot Details")
            .setMessage(message)
            .setPositiveButton("Navigate") { _, _ ->
                Toast.makeText(this, "Navigation will be implemented later", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton("Reserve") { _, _ ->
                Toast.makeText(this, "Reservation will be implemented later", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    /**
     * מציג דיאלוג בחירת עיר
     * ייקרא כאשר המשתמש לוחץ על אזור בחירת העיר
     */
    fun showCitySelectionDialog(cities: Array<String>) {
        // יימומש בהמשך כשנממש את ה-Controller
    }

    /**
     * מציג דיאלוג הפעלת חניה
     * ייקרא כאשר המשתמש לוחץ על כפתור הפעלת חניה
     */
    fun showActivateParkingDialog() {
        // יימומש בהמשך כשנממש את ה-Controller
    }

    // Logout confirmation dialog
    fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.logout)
            .setMessage(R.string.logout_confirmation)
            .setPositiveButton(R.string.yes) { _, _ ->
                // The user confirmed the logout
                navigateToWelcome()
            }
            .setNegativeButton(R.string.no, null) // If the user cancels, nothing will be done
            .show()
    }

    // Go to Welcome screen after logging out
    fun navigateToWelcome() {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // Adapter for the parking spots RecyclerView
    inner class ParkingSpotAdapter(
        private var parkingSpots: List<ParkingSpot>
    ) : RecyclerView.Adapter<ParkingSpotAdapter.ParkingSpotViewHolder>() {

        fun updateData(newParkingSpots: List<ParkingSpot>) {
            parkingSpots = newParkingSpots
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingSpotViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_parking_spot, parent, false)
            return ParkingSpotViewHolder(view)
        }

        override fun onBindViewHolder(holder: ParkingSpotViewHolder, position: Int) {
            holder.bind(parkingSpots[position])
        }

        override fun getItemCount() = parkingSpots.size

        inner class ParkingSpotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvParkingAddress: TextView = itemView.findViewById(R.id.tvParkingAddress)
            private val tvParkingRestrictions: TextView =
                itemView.findViewById(R.id.tvParkingRestrictions)
            private val tvParkingPrice: TextView = itemView.findViewById(R.id.tvParkingPrice)
            private val tvParkingCovered: TextView = itemView.findViewById(R.id.tvParkingCovered)
            private val tvTurnoverRate: TextView = itemView.findViewById(R.id.tvTurnoverRate)
            private val btnNavigate: ImageButton = itemView.findViewById(R.id.btnNavigate)

            fun bind(parkingSpot: ParkingSpot) {
                tvParkingAddress.text = parkingSpot.address
                tvParkingRestrictions.text = parkingSpot.restrictions
                tvParkingPrice.text = "₪${parkingSpot.pricePerHour} per hour"
                tvTurnoverRate.text = "Turnover rate: ${parkingSpot.turnoverRate}"
                tvParkingCovered.visibility = if (parkingSpot.isCovered) View.VISIBLE else View.GONE

                // כאן יהיו אירועי לחיצה על פריטים בתוך החניה
                btnNavigate.setOnClickListener {
                    Toast.makeText(
                        this@UrbanZoneActivity,
                        "Navigating to ${parkingSpot.address}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                itemView.setOnClickListener {
                    showParkingSpotDetailsDialog(parkingSpot)
                }
            }
        }
    }
}