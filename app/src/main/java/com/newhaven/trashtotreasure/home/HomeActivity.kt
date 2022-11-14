 package com.newhaven.trashtotreasure.home


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.newhaven.trashtotreasure.R
import java.util.*


 class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

     lateinit var mGoogleMap: GoogleMap
     var mapFrag: SupportMapFragment? = null
     lateinit var mLocationRequest: LocationRequest
     var mLastLocation: Location? = null
     internal var mCurrLocationMarker: Marker? = null
     private var mFusedLocationClient: FusedLocationProviderClient? = null
     private lateinit var btnFetchLocation: Button
     private lateinit var geocoder: Geocoder
     private lateinit var addresses: List<Address>
     var latLng: LatLng? = null

     private var mLocationCallback: LocationCallback = object : LocationCallback() {
         override fun onLocationResult(locationResult: LocationResult) {
             val locationList = locationResult.locations
             if (locationList.isNotEmpty()) {
                 val location = locationList.last()
                 Log.i("MapsActivity", "Location: " + location.latitude + " " + location.longitude)
                 mLastLocation = location
                 if (mCurrLocationMarker != null) {
                     mCurrLocationMarker?.remove()
                 }

                 //Place current location marker
                  latLng = LatLng(location.latitude, location.longitude)
                 val markerOptions = MarkerOptions()
                 markerOptions.position(latLng!!)
                 markerOptions.title("Current Position")
                 markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                 mCurrLocationMarker = mGoogleMap.addMarker(markerOptions)

                 //move map camera
                 mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng!!, 11.0F))


             }
         }
     }

     private fun getAddressFromLocation(latLng: LatLng): Address {
         geocoder = Geocoder(this, Locale.getDefault())
         addresses = geocoder.getFromLocation(
             latLng.latitude,
             latLng.longitude,
             1
         )
         val address =
             addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

         val city = addresses[0].locality
         val state = addresses[0].adminArea
         val country = addresses[0].countryName
         val postalCode = addresses[0].postalCode
         val knownName = addresses[0].featureName
         Toast.makeText(this, city, Toast.LENGTH_SHORT).show()
         return  addresses[0]
     }

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_home)
         supportActionBar?.title = "Map Location Activity"
         mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
         mapFrag = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
         mapFrag?.getMapAsync(this)
         btnFetchLocation = findViewById(R.id.btn_fetch_address)

         btnFetchLocation.setOnClickListener {
             latLng?.let { it1 -> getAddressFromLocation(it1) }
         }
     }

     public override fun onPause() {
         super.onPause()
         mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
     }

     @SuppressLint("MissingPermission")
     override fun onMapReady(googleMap: GoogleMap) {
         mGoogleMap = googleMap
         mGoogleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
         mLocationRequest = LocationRequest()
         mLocationRequest.interval = 120000 // two minute interval
         mLocationRequest.fastestInterval = 120000
         mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             if (ContextCompat.checkSelfPermission(
                     this,
                     Manifest.permission.ACCESS_FINE_LOCATION
                 ) == PackageManager.PERMISSION_GRANTED
             ) {
                 //Location Permission already granted
                 mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
                 mGoogleMap.isMyLocationEnabled = true
             } else {
                 //Request Location Permission
                 checkLocationPermission()
             }
         } else {
             mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
             mGoogleMap.isMyLocationEnabled = true
         }
     }

     private fun checkLocationPermission() {
         if (ActivityCompat.checkSelfPermission(
                 this,
                 Manifest.permission.ACCESS_FINE_LOCATION
             ) != PackageManager.PERMISSION_GRANTED
         ) {
             // Should we show an explanation?
             if (ActivityCompat.shouldShowRequestPermissionRationale(
                     this,
                     Manifest.permission.ACCESS_FINE_LOCATION
                 )
             ) {
                 AlertDialog.Builder(this)
                     .setTitle("Location Permission Needed")
                     .setMessage("This app needs the Location permission, please accept to use location functionality")
                     .setPositiveButton(
                         "OK"
                     ) { _, _ ->
                         //Prompt the user once explanation has been shown
                         ActivityCompat.requestPermissions(
                             this,
                             arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                             MY_PERMISSIONS_REQUEST_LOCATION
                         )
                     }
                     .create()
                     .show()


             } else {
                 // No explanation needed, we can request the permission.
                 ActivityCompat.requestPermissions(
                     this,
                     arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                     MY_PERMISSIONS_REQUEST_LOCATION
                 )
             }
         }
     }

     @SuppressLint("MissingPermission")
     override fun onRequestPermissionsResult(
         requestCode: Int,
         permissions: Array<String>, grantResults: IntArray
     ) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         when (requestCode) {
             MY_PERMISSIONS_REQUEST_LOCATION -> {
                 // If request is cancelled, the result arrays are empty.
                 if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                     // permission was granted, yay! Do the
                     // location-related task you need to do.
                     if (ContextCompat.checkSelfPermission(
                             this,
                             Manifest.permission.ACCESS_FINE_LOCATION
                         ) == PackageManager.PERMISSION_GRANTED
                     ) {
                         mFusedLocationClient?.requestLocationUpdates(
                             mLocationRequest,
                             mLocationCallback,
                             Looper.myLooper()
                         )
                         mGoogleMap.isMyLocationEnabled = true
                     }
                 } else {
                     Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                 }
                 return
             }
         }
     }

     companion object {
         val MY_PERMISSIONS_REQUEST_LOCATION = 99
     }
 }
