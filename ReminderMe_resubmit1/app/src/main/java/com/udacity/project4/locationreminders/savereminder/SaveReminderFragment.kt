package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.GeofencingConstants
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding

    private lateinit var geofencingClient: GeofencingClient

    // variable for identifying if the device is running android Q or later
    private val runningQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    // value to hold the newReminder
    private var newReminder: ReminderDataItem? = null

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveReminderBinding.inflate(inflater)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel

        geofencingClient = LocationServices.getGeofencingClient(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            // Navigate to another fragment to get the user location
            _viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    SaveReminderFragmentDirections
                        .actionSaveReminderFragmentToSelectLocationFragment()
                )
            )
        }

        binding.saveReminder.setOnClickListener {

            // getting the data ready for saving and starting the geofencing
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value

            if (title.isNullOrEmpty() || description.isNullOrEmpty() || location.isNullOrEmpty()) {
                _viewModel.showSnackBar.postValue(getString(R.string.error_no_data))
                return@setOnClickListener
            }
            // making a new reminder with the data
            newReminder = ReminderDataItem(title, description, location, latitude, longitude)
            // saving the newReminder to the database using the method in viewModel
            newReminder?.let {
                _viewModel.validateAndSaveReminder(
                    newReminder as ReminderDataItem
                )
            }


            // if there is no latitude or longitude save the reminder in the data base
            // but do not add a geofencing (return)
            if (latitude == null || longitude == null) {
                _viewModel.showSnackBar.postValue(getString(R.string.reminder_saved))
                return@setOnClickListener
            }

            checkForPermissionsAndAddGeofencing()
        }
    }

    // helper function to add the new geofencing
    private fun addNewGeofencing(reminder: ReminderDataItem) {

        // building the geofencing with the given latitude and longitude
        val geofence = Geofence.Builder()
            .setRequestId(reminder.id)
            .setCircularRegion(
                reminder.latitude as Double,
                reminder.longitude as Double,
                GeofencingConstants.GEOFENCE_RADIUS_IN_METERS
            )
            // Expires after and hour
            .setExpirationDuration(GeofencingConstants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
            // triggering on entering
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        // building the request
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        // add geofencing only if the location permission granted
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
                addOnCompleteListener {
                    _viewModel.showSnackBar.postValue(getString(R.string.reminder_saved_and_geofencing_added))
                }
            }
        }
    }

    /**
     * Starts the permission check and Geofence process only if the Geofence associated with the
     * current hint isn't yet active.
     */
    @SuppressLint("MissingPermission")
    private fun checkForPermissionsAndAddGeofencing() {
        if (foregroundAndBackgroundLocationPermissionApproved()) {
            // check if the location is on after as we already have the permission to use it
            checkDeviceLocationSettings()
        } else {
            // asking for foreground and background permission if not granted
            requestForegroundAndBackgroundLocationPermissions()
        }
    }

    /**
     *  Determines whether the app has the appropriate permissions across Android 10+ and all other
     *  Android versions.
     */
    @TargetApi(29)
    private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {

        // checking for foreground and background permission using checkSelfPermission
        val foregroundPermissionApproved = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val backgroundPermissionApproved =
            if (runningQOrLater) {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        return foregroundPermissionApproved && backgroundPermissionApproved
    }

    /**
     *  Uses the Location Client to check the current state of location settings, and gives the user
     *  the opportunity to turn on location services within our app.
     */
    private fun checkDeviceLocationSettings(resolve: Boolean = true) {
        // the add code to check that the device's location is on
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            // if the location is off but we can resolve that try to resolve
            if ((exception is ResolvableApiException) && resolve) {
                try {
                    startIntentSenderForResult(
                        exception.resolution.intentSender,
                        REQUEST_TURN_DEVICE_LOCATION_ON,
                        null, 0, 0, 0, null
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                // if the user dismissed turning the location on show a snackbar with the information
                _viewModel.showSnackBar.postValue(getString(R.string.location_required_error))
            }
        }

        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                // clearing the view model to allow adding a new reminder
                _viewModel.onClear()
                // adding the geofencing
                newReminder?.let { it1 -> addNewGeofencing(it1) }
            }
        }
    }

    /**
     *  Requests ACCESS_FINE_LOCATION and (on Android 10+ (Q) ACCESS_BACKGROUND_LOCATION.
     */
    @TargetApi(29)
    private fun requestForegroundAndBackgroundLocationPermissions() {

        // the add code to request foreground and background permissions
        if (foregroundAndBackgroundLocationPermissionApproved())
            return

        // checking for foreground and background permission using checkSelfPermission
        val foregroundPermissionApproved = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val backgroundPermissionApproved =
            if (runningQOrLater) {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        // if it was the first time calling this method asking first for the fine location
        if (!foregroundPermissionApproved) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
        // else if was the second time calling this method asking for the background location
        // that we have to get the foreground location then the background location in order
        else if (foregroundPermissionApproved && !backgroundPermissionApproved) {
            _viewModel.showToast.postValue(getString(R.string.Requesting_getting_location_all_time))
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //  code to check that the user turned on their device location on
        //  if they didn't ask for it
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            if (resultCode == Activity.RESULT_OK) {
                // clearing the view model to allow adding a new reminder
                _viewModel.onClear()
                // and add the geofencing to be reminded on location
                newReminder?.let { addNewGeofencing(it) }
            }
            checkDeviceLocationSettings(false)
        }
    }

    /**
     * In all cases, we need to have the location permission.  On Android 10+ (Q) we need to have
     * the background permission as well.
     */
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // the code to handle the result of the user's permission

        if (
            grantResults.isEmpty() ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED
        ) {
            // showing a snackbar if the user didn't approve to give the permission
            Snackbar.make(
                requireView(),
                getString(R.string.error_permission_needed),
                Snackbar.LENGTH_INDEFINITE
            )
                // adding action to go to the settings to allow location permission
                .setAction("Settings") {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
        } else if (!foregroundAndBackgroundLocationPermissionApproved()) {
            // call request for permission again for granting the background permission
            requestForegroundAndBackgroundLocationPermissions()
        } else {
            // check if the location is on after getting the permissions
            checkDeviceLocationSettings()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }

    companion object {
        internal const val ACTION_GEOFENCE_EVENT =
            "RemindersActivity.SaveReminderFragment.action.ACTION_GEOFENCE_EVENT"
    }
}

// random unique values for requests result codes
private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29

// Class TAG and indices of the permissions
private val TAG = SaveReminderFragment::class.java.simpleName
private const val LOCATION_PERMISSION_INDEX = 0
