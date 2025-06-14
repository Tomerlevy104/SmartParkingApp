package com.example.smartparkingapp.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.example.smartparkingapp.model.UserModel
import com.example.smartparkingapp.services.impl.ObjectServiceImpl
import com.example.smartparkingapp.services.impl.UserServiceImpl
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Main screen activity
 */
class UrbanZoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUrbanZoneBinding
    private lateinit var parkingSpotAdapter: ParkingSpotAdapter
    private var currentUser: UserModel? = null
    private lateinit var userController: UserController
    private lateinit var objectController: ObjectController

    // List of urban zones from server
    private var allUrbanZoneArray: List<UrbanZoneModel> = emptyList()
    private var selectedUrbanZone: UrbanZoneModel? = null

    // Current parking spots list from server
    private var currentParkingSpots: MutableList<ParkingSpotModel> = mutableListOf()

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

            Log.d(
                "UrbanZoneActivity",
                "Intent extras: email=$email, username=$username, role=$role, avatar=$avatar"
            )

            if (email != null && username != null && role != null) {
                currentUser = UserModel(
                    email = email,
                    username = username,
                    role = role,
                    avatar = avatar ?: "default"
                )
                Log.d(
                    "UrbanZoneActivity",
                    "User created: ${currentUser?.email}, ${currentUser?.username}"
                )
            }
            Log.d(
                "UserDetailsActivity",
                "Received from intent: email=$email, username=$username, role=$role, avatar=$avatar"
            )
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

                        allUrbanZoneArray = zones
                        selectedUrbanZone = zones.first()

                        Log.d("UrbanZoneActivity", "Setting up dropdown with ${zones.size} zones")
                        setupUrbanZoneDropdown()
                        updateUrbanZoneUI()

                        zones.forEach { zone ->
                            Log.d(
                                "UrbanZoneActivity",
                                "Zone: ${zone.getName()}, Spots: ${zone.getTotalParkingSpots()}"
                            )
                        }

                        // Load parking spots for the selected zone
                        selectedUrbanZone?.let { zone ->
                            loadParkingSpotsForSelectedZone(zone)
                        }
                    } else {
                        Log.d("UrbanZoneActivity", "No urban zones found")
                        Toast.makeText(
                            this@UrbanZoneActivity,
                            "No urban zones found",
                            Toast.LENGTH_SHORT
                        ).show()
                        setupDefaultUrbanZone()
                    }
                }
            } catch (e: Exception) {
                Log.e("UrbanZoneActivity", "Error loading urban zones", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UrbanZoneActivity, "Error: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                    setupDefaultUrbanZone()
                }
            }
        }
    }

    /**
     * Load parking spots for the selected urban zone
     */
    private fun loadParkingSpotsForSelectedZone(selectedUrbanZone: UrbanZoneModel) {
        val userEmail = currentUser?.email
        Log.d("UrbanZoneActivity", "User email: $userEmail")
        if (userEmail == null) {
            Log.e("UrbanZoneActivity", "No user email available")
            return
        }

        val urbanZoneId = selectedUrbanZone.getObjectId().objectId
        Log.d(
            "UrbanZoneActivity",
            "Loading parking spots for zone: ${selectedUrbanZone.getName()} (ID: $urbanZoneId)"
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Fetch non-occupied parking spots from server
                val parkingSpots = objectController.getAllNonOccupiedParkingSpotsFromUrbanZone(
                    userEmail,
                    urbanZoneId
                )
                val numberOfAvailableSpots = parkingSpots.size
                Log.d(
                    "UrbanZoneActivity",
                    "Received ${numberOfAvailableSpots} non-occupied parking spots"
                )

                withContext(Dispatchers.Main) {
                    // Update the global variable with the new data
                    currentParkingSpots.clear()
                    currentParkingSpots.addAll(parkingSpots)

                    // Update UI
                    binding.tvAvailableSpots.text = numberOfAvailableSpots.toString()
                    updateParkingSpotsList()

                    // Show/hide empty state
                    if (parkingSpots.isEmpty()) {
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.rvParkingSpots.visibility = View.GONE
                    } else {
                        binding.tvEmptyState.visibility = View.GONE
                        binding.rvParkingSpots.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.e("UrbanZoneActivity", "Error loading parking spots", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@UrbanZoneActivity,
                        "Error loading parking spots: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    // Show empty list on error
                    currentParkingSpots.clear()
                    updateParkingSpotsList()
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
        val urbanZoneNames = allUrbanZoneArray.map { it.getName() }

        // Set up the dropdown adapter
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, urbanZoneNames)
        binding.urbanZoneDropdown.setAdapter(adapter)

        // Set default selection (first zone)
        if (urbanZoneNames.isNotEmpty()) {
            binding.urbanZoneDropdown.setText(urbanZoneNames[0], false)
        }

        // Handle item selection
        binding.urbanZoneDropdown.setOnItemClickListener { _, _, position, _ ->
            if (position < allUrbanZoneArray.size) {
                val previousZone = selectedUrbanZone
                selectedUrbanZone = allUrbanZoneArray[position]

                Log.d("UrbanZoneActivity", "Zone selected: ${urbanZoneNames[position]}")
                Log.d(
                    "UrbanZoneActivity",
                    "Selected zone ObjectId: ${selectedUrbanZone?.getObjectId()?.objectId}"
                )

                // Update UI
                updateUrbanZoneUI()

                // Load parking spots for the newly selected zone
                if (selectedUrbanZone != previousZone) {
                    val urbanZone = selectedUrbanZone
                    if (urbanZone is UrbanZoneModel) {
                        loadParkingSpotsForSelectedZone(urbanZone)
                    }
                }
                Log.d("UrbanZoneActivity","Selected: ${urbanZoneNames[position]}")
            }
        }
    }

    private fun setupRecyclerView() {
        parkingSpotAdapter = ParkingSpotAdapter(currentParkingSpots)
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
            binding.tvCityDescription.text = zone.getDescription()
            binding.tvTotalSpots.text = zone.getTotalParkingSpots().toString()
            binding.tvAvailableSpots.text = zone.getAvailableParkingSpots().toString()
            binding.tvHourlyRate.text = zone.getFormattedHourlyRate()

            Log.d("UrbanZoneActivity", "Updated UI with UrbanZone: ${zone.getName()}")
            Log.d("UrbanZoneActivity", "Has available spots: ${zone.hasAvailableSpots()}")
        }
    }

    /**
     * Setup default urban zone (demo data)
     */
    private fun setupDefaultUrbanZone() {
        binding.urbanZoneDropdown.setText(getString(R.string.no_details), false)
        binding.tvCityDescription.text = getString(R.string.no_details)
        binding.tvCityDescription.text = getString(R.string.no_details)
        binding.tvTotalSpots.text = getString(R.string.no_details)
        binding.tvAvailableSpots.text = getString(R.string.no_details)
        binding.tvHourlyRate.text = getString(R.string.no_details)

        currentParkingSpots.clear()
        updateParkingSpotsList()
        // Show empty state for default setup
        binding.tvEmptyState.visibility = View.VISIBLE
        binding.rvParkingSpots.visibility = View.GONE
    }

    /**
     * Updates the list of parking spaces displayed in RecyclerView
     */
    private fun updateParkingSpotsList() {
        parkingSpotAdapter.updateData(currentParkingSpots)
        Log.d("UrbanZoneActivity", "Updated parking spots list with ${currentParkingSpots.size} spots")
    }

    /**
     * Refresh data from server
     */
    private fun refreshData() {
        Toast.makeText(this, "Refreshing Data...", Toast.LENGTH_SHORT).show()
        Log.d("UrbanZoneActivity", "Refreshing data for selected zone")

        // If we have a selected zone, reload its parking spots
        selectedUrbanZone?.let { zone ->
            Log.d("UrbanZoneActivity", "Refreshing parking spots for zone: ${zone.getName()}")
            loadParkingSpotsForSelectedZone(zone)
        } ?: run {
            Log.w("UrbanZoneActivity", "No selected zone to refresh")
            Toast.makeText(this, "No zone selected to refresh", Toast.LENGTH_SHORT).show()
        }

        // Update UI with current zone data
        updateUrbanZoneUI()
    }

    private fun setupButtons() {
        // Logout button
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        // Navigate to UserDetailsActivity when user details button is clicked
        binding.userDetailsBTN.setOnClickListener {
            Log.d("UrbanZoneActivity", "Navigating to UserDetailsActivity")

            val intent = Intent(this, UserDetailsActivity::class.java)

            // Only pass the email - UserDetailsActivity will load full user data from server
            currentUser?.let { user ->
                intent.putExtra("USER_EMAIL", user.email)
            }

            startActivity(intent)
        }

        // Refresh button
        binding.btnRefresh.setOnClickListener {
            Log.d("UrbanZoneActivity", "Refresh button clicked")
            refreshData()
        }
    }

    private fun showParkingSpotDetailsDialog(parkingSpot: ParkingSpotModel) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_parking_spot_details)

        // Set the window size
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Finding the components
        val tvAddress = dialog.findViewById<TextView>(R.id.tvDialogAddress)
        val tvRate = dialog.findViewById<TextView>(R.id.tvDialogRate)
        val tvCoverage = dialog.findViewById<TextView>(R.id.tvDialogCoverage)
        val tvRestrictions = dialog.findViewById<TextView>(R.id.tvDialogRestrictions)
        val btnClose = dialog.findViewById<MaterialButton>(R.id.btnDialogClose)
        val btnCloseX = dialog.findViewById<ImageView>(R.id.btnClose)
        val ivCoverageIcon = dialog.findViewById<ImageView>(R.id.ivCoverageIcon)
        val coverageCard = tvCoverage.parent.parent as MaterialCardView

        tvAddress.text = parkingSpot.address
        tvRate.text = "₪${selectedUrbanZone?.getBaseHourlyRate()}"
        tvRestrictions.text = parkingSpot.restrictions

        if (parkingSpot.isCovered) {
//            tvCoverage.text = "Covered"
            tvCoverage.text = getString(R.string.covered)

            tvCoverage.setTextColor(ContextCompat.getColor(this, R.color.white))
            ivCoverageIcon.setImageResource(android.R.drawable.ic_menu_directions)
            ivCoverageIcon.setColorFilter(ContextCompat.getColor(this, R.color.white))
            coverageCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.app_green))
        } else {
            tvCoverage.text = getString(R.string.not_covered)
            tvCoverage.setTextColor(ContextCompat.getColor(this, R.color.app_blue))
            ivCoverageIcon.setImageResource(android.R.drawable.ic_menu_directions)
            ivCoverageIcon.setColorFilter(ContextCompat.getColor(this, R.color.app_blue))
            coverageCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            coverageCard.strokeColor = ContextCompat.getColor(this, R.color.app_blue)
            coverageCard.strokeWidth = 2
        }

        // Close buttons
        btnClose.setOnClickListener { dialog.dismiss() }
        btnCloseX.setOnClickListener { dialog.dismiss() }

        // Display the dialog
        dialog.show()
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
            Log.d("ParkingSpotAdapter", "Data updated with ${newParkingSpots.size} spots")
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
            private val btnNavigate: MaterialButton =
                itemView.findViewById(R.id.btnNavigate)

            fun bind(parkingSpot: ParkingSpotModel) {
                tvParkingAddress.text = parkingSpot.address
                tvParkingRestrictions.text = parkingSpot.restrictions

                // Navigate button click - Opens navigation dialog
                btnNavigate.setOnClickListener {
                    NavigationUtils.showNavigationDialog(
                        this@UrbanZoneActivity,
                        parkingSpot.address
                    )
                }

                // Item click for details
                itemView.setOnClickListener {
                    showParkingSpotDetailsDialog(parkingSpot)
                }
            }
        }
    }
}