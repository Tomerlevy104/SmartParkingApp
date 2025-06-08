package com.example.smartparkingapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AlertDialog

object NavigationUtils {

    /**
     * Shows dialog to choose navigation app (Google Maps or Waze) and opens the selected app
     * @param context Activity context
     * @param address The destination address
     */
    fun showNavigationDialog(context: Context, address: String) {
        val options = mutableListOf<String>()
        val availableApps = mutableListOf<NavigationApp>()

        // Check if Google Maps is available
        if (isAppInstalled(context, "com.google.android.apps.maps")) {
            options.add("Google Maps")
            availableApps.add(NavigationApp.GOOGLE_MAPS)
        }

        // Check if Waze is available
        if (isAppInstalled(context, "com.waze")) {
            options.add("Waze")
            availableApps.add(NavigationApp.WAZE)
        }

        // If no navigation apps are installed
        if (options.isEmpty()) {
            showNoNavigationAppsDialog(context)
            return
        }

        // If only one app is available, open it directly
        if (options.size == 1) {
            openNavigationApp(context, availableApps[0], address)
            return
        }

        // Show selection dialog if multiple apps are available
        AlertDialog.Builder(context)
            .setTitle("Choose Navigation App")
            .setItems(options.toTypedArray()) { _, which ->
                openNavigationApp(context, availableApps[which], address)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Opens the specified navigation app with the given address
     */
    private fun openNavigationApp(context: Context, app: NavigationApp, address: String) {
        try {
            val intent = when (app) {
                NavigationApp.GOOGLE_MAPS -> createGoogleMapsIntent(address)
                NavigationApp.WAZE -> createWazeIntent(address)
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            // If the app fails to open, show error dialog
            showNavigationErrorDialog(context, app.displayName)
        }
    }

    /**
     * Creates intent for Google Maps navigation
     */
    private fun createGoogleMapsIntent(address: String): Intent {
        val encodedAddress = Uri.encode(address)
        val uri = Uri.parse("google.navigation:q=$encodedAddress")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        return intent
    }

    /**
     * Creates intent for Waze navigation
     */
    private fun createWazeIntent(address: String): Intent {
        val encodedAddress = Uri.encode(address)
        val uri = Uri.parse("waze://?q=$encodedAddress&navigate=yes")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.waze")
        return intent
    }

    /**
     * Checks if a specific app is installed on the device
     */
    private fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * Shows dialog when no navigation apps are installed
     */
    private fun showNoNavigationAppsDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("No Navigation Apps Found")
            .setMessage("Please install Google Maps or Waze to use navigation features.")
            .setPositiveButton("Install Google Maps") { _, _ ->
                openPlayStore(context, "com.google.android.apps.maps")
            }
            .setNeutralButton("Install Waze") { _, _ ->
                openPlayStore(context, "com.waze")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Shows error dialog when navigation app fails to open
     */
    private fun showNavigationErrorDialog(context: Context, appName: String) {
        AlertDialog.Builder(context)
            .setTitle("Navigation Error")
            .setMessage("Failed to open $appName. Please check if the app is properly installed.")
            .setPositiveButton("OK", null)
            .show()
    }

    /**
     * Opens Google Play Store for app installation
     */
    private fun openPlayStore(context: Context, packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            context.startActivity(intent)
        } catch (e: Exception) {
            // If Play Store app is not available, open web version
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
            context.startActivity(intent)
        }
    }

    /**
     * Enum for navigation apps
     */
    private enum class NavigationApp(val displayName: String) {
        GOOGLE_MAPS("Google Maps"),
        WAZE("Waze")
    }
}