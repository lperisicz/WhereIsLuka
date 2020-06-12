package com.perisic.luka.whereisluka

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private val locationRequestCode = 10
    private lateinit var locationManager: LocationManager
    private lateinit var geocoder: Geocoder
    private lateinit var map: GoogleMap

    private val locationListener = object: LocationListener {
        override fun onProviderEnabled(provider: String?) { }
        override fun onProviderDisabled(provider: String?) { }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) { }
        override fun onLocationChanged(location: Location?) {
            updateLocationDisplay(location)
        }
    }
    private fun updateLocationDisplay(location: Location?) {
        val lat = location?.latitude ?: 0
        val lon = location?.longitude ?: 0
        textViewLocationInfo.text = "Lat: $lat\nLon: $lon"
        //TODO geocoder nac adresu i to stali
        println("LOCATION UPDATE")
        //geocoder.nesto nesto
    }
    private fun updateLocationDisplay(latitude: Long?, longitude: Long) {
        val lat = latitude ?: 0
        val lon = longitude ?: 0
        textViewLocationInfo.text = "Lat: $lat\nLon: $lon"
        //TODO geocoder nac adresu i to stali
        println("LOCATION UPDATE")
        //geocoder.nesto nesto
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonTakePhoto.setOnClickListener{ trackLocation() }
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        geocoder = Geocoder(this)
        val fragment = supportFragmentManager.findFragmentById(R.id.map)
        if(fragment is SupportMapFragment) {
            fragment.getMapAsync(this)
        }
    }

    private fun trackLocation() {
        //if(hasPermissionCompat(locationPermission)){
            startTrackingLocation()
        /*} else {
            requestPermisionCompat(arrayOf(locationPermission), locationRequestCode)
        }*/
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        when(requestCode){
            locationRequestCode -> {
                if(grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    trackLocation()
                else
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun startTrackingLocation() {
        Log.d("TAG", "Tracking location")
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val provider = locationManager.getBestProvider(criteria, true)
        val minTime = 1000L
        val minDistance = 1F
        try{
            locationManager.requestLocationUpdates(provider ?: "", minTime, minDistance, locationListener)
        } catch (e: SecurityException){
            Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(locationListener)
    }

    private fun AppCompatActivity.hasPermissionCompat (permission: String): Boolean{
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
    fun AppCompatActivity.shouldShowRationaleCompat(permission: String): Boolean{
        return ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
    }
    private fun AppCompatActivity.requestPermisionCompat(permission: Array<String>, requestCode: Int){
        ActivityCompat.requestPermissions(this, permission, requestCode)
    }

    override fun onMapReady(p0: GoogleMap?) {
        println("ON MAP READY")
        map = p0!!

        val osijek = LatLng(45.55111, 18.69389)
        map.addMarker(MarkerOptions().position(osijek).title("Marker in Osijek"))
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        map.uiSettings.isZoomControlsEnabled = true
        map.moveCamera(CameraUpdateFactory.newLatLng(osijek))
        val location = geocoder.getFromLocation(osijek.latitude, osijek.longitude, 1)
        location.firstOrNull()?.let {
            textViewLocationInfo.setText(
                ""
            )
        }
    }
}
