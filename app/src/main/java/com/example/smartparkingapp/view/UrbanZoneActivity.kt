package com.example.smartparkingapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartparkingapp.R
import com.example.smartparkingapp.NavigationUtils
import com.example.smartparkingapp.controller.ObjectController
import com.example.smartparkingapp.controller.UserController
import com.example.smartparkingapp.databinding.ActivityUrbanZoneBinding
import com.example.smartparkingapp.model.ParkingSpotModel
import com.example.smartparkingapp.model.UrbanZoneModel
import com.example.smartparkingapp.model.User
import com.example.smartparkingapp.services.impl.ObjectServiceImpl
import com.example.smartparkingapp.services.impl.UserServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class UrbanZoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUrbanZoneBinding
    private lateinit var parkingSpotAdapter: ParkingSpotAdapter
    private var currentUser: User? = null
    private lateinit var userController: UserController
    private lateinit var objectController: ObjectController

    // List of urban zones from server
    private var allUrbanZoneArray: List<UrbanZoneModel> = emptyList()
    private var selectedUrbanZone: UrbanZoneModel? = null

    // Sample parking spots list
    private val sampleParkingSpots = listOf(
        ParkingSpotModel(
            id = "P1",
            restrictions = "2 hour maximum",
            occupied = false,
            turnoverRate = "High",
            address = "12 Dizengoff Street",
            zoneId = "1",
            isCovered = true,
            pricePerHour = "8.00"
        ),
        ParkingSpotModel(
            id = "P2",
            restrictions = "No restrictions",
            occupied = false,
            turnoverRate = "Medium",
            address = "25 Ben Yehuda Street",
            zoneId = "1",
            isCovered = false,
            pricePerHour = "8.00"
        ),
        ParkingSpotModel(
            id = "P3",
            restrictions = "4 hour maximum",
            occupied = false,
            turnoverRate = "Low",
            address = "7 Allenby Road",
            zoneId = "1",
            isCovered = false,
            pricePerHour = "7.50"
        ),
        ParkingSpotModel(
            id = "P4",
            restrictions = "No restrictions",
            occupied = false,
            turnoverRate = "Medium",
            address = "33 King George Street",
            zoneId = "1",
            isCovered = true,
            pricePerHour = "9.00"
        ),
        ParkingSpotModel(
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

        // Initialize controllers
        initializeControllers()

        // Get user details from intent
        getUserFromIntent()

        setupRecyclerView()
        setupButtons()

        // Load urban zones from server
        loadUrbanZonesFromServer()
    }

    /**
     * Initialize controllers
     */
    private fun initializeControllers() {
        val userService = UserServiceImpl()
        userController = UserController(userService)

        val objectService = ObjectServiceImpl()
        objectController = ObjectController(objectService)
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

    /**
     * Load urban zones from server
     */
    private fun loadUrbanZonesFromServer() {
        val userEmail = currentUser?.email
        if (userEmail == null) {
            Log.e("UrbanZoneActivity", "No user email available")
            setupDefaultUrbanZone()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("UrbanZoneActivity", "Loading urban zones for user: $userEmail")

                // Fetch zones from server
                val zones = objectController.getAllUrbanZones(userEmail)
                Log.d("UrbanZoneActivity", "Received zones from controller: ${zones.size}")

                withContext(Dispatchers.Main) {
                    if (zones.isNotEmpty()) {
                        Log.d("UrbanZoneActivity", "Processing ${zones.size} zones")

                        // All zones should already be UrbanZoneModel type
                        allUrbanZoneArray = zones
                        selectedUrbanZone = zones.first()

                        Log.d("UrbanZoneActivity", "Setting up dropdown with ${zones.size} zones")
                        setupUrbanZoneDropdown()
                        updateUrbanZoneUI()

                        zones.forEach { zone ->
                            Log.d("UrbanZoneActivity", "Zone: ${zone.getName()}, Spots: ${zone.getTotalParkingSpots()}")
                        }

                        Toast.makeText(
                            this@UrbanZoneActivity,
                            "Loaded ${zones.size} urban zones from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.w("UrbanZoneActivity", "No urban zones found")
                        Toast.makeText(this@UrbanZoneActivity, "No urban zones found", Toast.LENGTH_SHORT).show()
                        setupDefaultUrbanZone()
                    }
                }
            } catch (e: Exception) {
                Log.e("UrbanZoneActivity", "Error loading urban zones", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UrbanZoneActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    setupDefaultUrbanZone()
                }
            }
        }
    }

    /**
     * Setup dropdown for urban zone selection
     */
    private fun setupUrbanZoneDropdown() {
        if (allUrbanZoneArray.isEmpty()) return

        // Create list of names from UrbanZone objects
        val zoneNames = allUrbanZoneArray.map { it.getName() }

        // Set up the dropdown adapter
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, zoneNames)
        binding.urbanZoneDropdown.setAdapter(adapter)

        // Set default selection (first zone)
        if (zoneNames.isNotEmpty()) {
            binding.urbanZoneDropdown.setText(zoneNames[0], false)
        }

        // Handle item selection
        binding.urbanZoneDropdown.setOnItemClickListener { _, _, position, _ ->
            if (position < allUrbanZoneArray.size) {
                selectedUrbanZone = allUrbanZoneArray[position]
                updateUrbanZoneUI()
                Toast.makeText(this, "Selected: ${zoneNames[position]}", Toast.LENGTH_SHORT).show()
                Log.d("UrbanZoneActivity", "Zone selected: ${zoneNames[position]}")
            }
        }
    }

    private fun setupRecyclerView() {
        parkingSpotAdapter = ParkingSpotAdapter(emptyList())
        binding.rvParkingSpots.apply {
            layoutManager = LinearLayoutManager(this@UrbanZoneActivity)
            adapter = parkingSpotAdapter
        }
    }

    /**
     * Update UI with selected zone data
     */
    private fun updateUrbanZoneUI() {
        val zone = selectedUrbanZone
        if (zone != null) {
            // Now we know it's always UrbanZoneModel, so we can access all properties
            binding.tvCityDescription.text = zone.getDescription()
            binding.tvTotalSpots.text = zone.getTotalParkingSpots().toString()
            binding.tvAvailableSpots.text = zone.getAvailableParkingSpots().toString()
            binding.tvHourlyRate.text = zone.getFormattedHourlyRate()

            Log.d("UrbanZoneActivity", "Updated UI with UrbanZone: ${zone.getName()}")
            Log.d("UrbanZoneActivity", "Zone occupancy rate: ${zone.getOccupancyRate()}%")
            Log.d("UrbanZoneActivity", "Has available spots: ${zone.hasAvailableSpots()}")

            // Update parking spots list
            updateParkingSpotsList(sampleParkingSpots.filter { !it.occupied })
        }
    }

    /**
     * Setup default urban zone (demo data)
     */
    private fun setupDefaultUrbanZone() {
        binding.urbanZoneDropdown.setText("Tel Aviv (Default)", false)
        binding.tvCityDescription.text = "City Streets (Demo Data)"
        binding.tvTotalSpots.text = "120"
        binding.tvAvailableSpots.text = "55"
        binding.tvHourlyRate.text = "₪8.00"
        updateParkingSpotsList(sampleParkingSpots.filter { !it.occupied })
    }

    // Updates the list of parking spaces displayed in RecyclerView
    private fun updateParkingSpotsList(parkingSpots: List<ParkingSpotModel>) {
        parkingSpotAdapter.updateData(parkingSpots)
    }

    private fun setupButtons() {
        // Logout button
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
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
    private fun showParkingSpotDetailsDialog(parkingSpot: ParkingSpotModel) {
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
//            .setPositiveButton("Navigate") { _, _ ->
//                Toast.makeText(this, "Navigation will be implemented later", Toast.LENGTH_SHORT)
//                    .show()
//            }
//            .setNegativeButton("Reserve") { _, _ ->
//                Toast.makeText(this, "Reservation will be implemented later", Toast.LENGTH_SHORT)
//                    .show()
//            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    // Logout confirmation dialog
    private fun showLogoutConfirmationDialog() {
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
    private fun navigateToWelcome() {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // Adapter for the parking spots RecyclerView
    inner class ParkingSpotAdapter(
        private var parkingSpots: List<ParkingSpotModel>
    ) : RecyclerView.Adapter<ParkingSpotAdapter.ParkingSpotViewHolder>() {

        fun updateData(newParkingSpots: List<ParkingSpotModel>) {
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
            private val btnNavigate: com.google.android.material.button.MaterialButton =
                itemView.findViewById(R.id.btnNavigate)

            fun bind(parkingSpot: ParkingSpotModel) {
                tvParkingAddress.text = parkingSpot.address
                tvParkingRestrictions.text = parkingSpot.restrictions
                tvParkingPrice.text = "₪${parkingSpot.pricePerHour}/hr"

                // Set turnover rate text
                tvTurnoverRate.text = parkingSpot.turnoverRate

                // Show/hide covered badge
                tvParkingCovered.visibility = if (parkingSpot.isCovered) View.VISIBLE else View.GONE

                // Navigate button click - Opens navigation dialog
                btnNavigate.setOnClickListener {
                    NavigationUtils.showNavigationDialog(this@UrbanZoneActivity, parkingSpot.address)
                }

                // Item click for details
                itemView.setOnClickListener {
                    showParkingSpotDetailsDialog(parkingSpot)
                }
            }
        }
    }
}