package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

// IMPORTANT NOTE: SOME OF THE CODE IS COPIED FROM THE LESSONS
// WITH MY OWN MODIFICATION TO WORK WITH THE TWO STEP GRANTING PERMISSION WITH LATER VERSIONS OF ANDROID
class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    lateinit var map: GoogleMap

    // variable for identifying if the device is running android Q or later
    private val runningQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    // for knowing how many times the app asked for permission (fine and background is needed)
    private var askingForPermissionCount = 0

    // for saving the state of error_snackbar to dismiss it later when the location is enabled
    var locationErrorSnackbarRef: Snackbar? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        if ((requireActivity() as RemindersActivity).locationErrorSnackbar != null) {
            locationErrorSnackbarRef =
                (requireActivity() as RemindersActivity).locationErrorSnackbar
        }

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        binding.saveButton.setOnClickListener {
            onLocationSelected()
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationErrorSnackbarRef != null) {
            (requireActivity() as RemindersActivity).locationErrorSnackbar =
                locationErrorSnackbarRef
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // if there were a selected poi already point to it
        _viewModel.selectedPOI.observe(viewLifecycleOwner, Observer { poi ->
            if (poi != null) {
                val poiMarker = map.addMarker(
                    MarkerOptions().position(poi.latLng)
                        .title(poi.name)
                        .snippet(getString(R.string.marker_poi_snippet))
                )
                poiMarker?.showInfoWindow()
            }
        })

        // setting the long click behavior for adding a mark
        setOnMapLongClick(map)
        setPoiClick(map)
        setMapStyle(map)

        checkForPermissions()

        _viewModel.showToast.postValue(getString(R.string.asking_to_select_poi))
    }

    override fun onStart() {
        super.onStart()

        if (::map.isInitialized) {
            askingForPermissionCount = 0

            checkForPermissions()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //  code to check that the user turned on their device location on
        //  if they didn't ask for it
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            if (resultCode == Activity.RESULT_OK) {
                // if the user approved to turn on the location dismiss the old error snackbar
                locationErrorSnackbarRef?.dismiss()
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
            // Set the app to MyLocationEnabled
            map.isMyLocationEnabled = true
            // check if the location is on after getting the permissions
            checkDeviceLocationSettings()
        }
    }

    /**
     * Starts the permission check and Geofence process only if the Geofence associated with the
     * current hint isn't yet active.
     */
    @SuppressLint("MissingPermission")
    private fun checkForPermissions() {
        if (foregroundAndBackgroundLocationPermissionApproved()) {
            // Set the app to MyLocationEnabled
            map.isMyLocationEnabled = true
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
        val foregroundPermissionApproved = checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val backgroundPermissionApproved =
            if (runningQOrLater) {
                checkSelfPermission(
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
                locationErrorSnackbarRef = Snackbar.make(
                    requireView(),
                    getString(R.string.Asking_to_enable_the_location),
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettings()
                }
                locationErrorSnackbarRef?.show()
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

        // if it was the first time calling this method asking first for the fine location
        if (askingForPermissionCount == 0) {
            askingForPermissionCount++
            requestPermissions(

                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
        // else if was the second time calling this method asking for the background location
        // that we have to get the foreground location then the background location in order
        else if (askingForPermissionCount == 1) {
            askingForPermissionCount++

            _viewModel.showToast.postValue(getString(R.string.Requesting_getting_location_all_time))
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            )
        }
    }

    private fun setOnMapLongClick(map: GoogleMap) {
        // the long click behavior is to add a marker in the map
        // and set it to be the reminder coordinates
        map.setOnMapLongClickListener { latLng ->
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )

            map.addMarker(
                MarkerOptions().position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            )

            // setting the view model values according to this marker
            _viewModel.apply {
                selectedPOI.postValue(PointOfInterest(latLng, "", ""))
                latitude.postValue(latLng.latitude)
                longitude.postValue(latLng.longitude)
                reminderSelectedLocationStr.postValue(getString(R.string.no_name))
            }
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        // set the clicking on the poi behavior is to show an info window
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions().position(poi.latLng)
                    .title(poi.name)
                    .snippet("marker poi snippet")
            )
            poiMarker?.showInfoWindow()
            // setting the view model values according to this POI
            _viewModel.apply {
                selectedPOI.postValue(poi)
                latitude.postValue(poi.latLng.latitude)
                longitude.postValue(poi.latLng.longitude)
                reminderSelectedLocationStr.postValue(poi.name)
            }
        }
    }


    private fun onLocationSelected() {
        // When the user confirms on the selected location,
        //  send back the selected location details to the view model
        //  and navigate back to the previous fragment to save the reminder and add the geofence
        if (_viewModel.selectedPOI.value == null) {
            _viewModel.showToast.postValue(getString(R.string.poi_or_location_needed))
            return
        } else {
            // navigate back to the save location fragment
            _viewModel.navigationCommand.postValue(NavigationCommand.Back)
        }
    }
    // setting the map style helper function
    private fun setMapStyle(map: GoogleMap) {
        // try getting the resources to set the map style
        try {
            val success =
                map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.map_style
                    )
                )

            if (!success) {
                Log.i(TAG, "failed to parse the json style")
            }
        } catch (e: Resources.NotFoundException) {
            Log.i(TAG, "failed to load the resource file")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Changing the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}

// random unique values for requests result codes
private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29

// Class TAG and indices of the permissions
private val TAG = SelectLocationFragment::class.java.simpleName
private const val LOCATION_PERMISSION_INDEX = 0
