package com.example.gst.trainingcourse.iotcharger

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.gst.trainingcourse.iotcharger.constant.CONSTANT
import com.example.gst.trainingcourse.iotcharger.model.Account
import com.example.gst.trainingcourse.iotcharger.databinding.ActivityMapsBinding
import com.example.gst.trainingcourse.iotcharger.model.Device
import com.example.gst.trainingcourse.iotcharger.model.LocationStore
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding

    private lateinit var database: DatabaseReference
    private var storeList : ArrayList<LocationStore> = arrayListOf()

    private var currentLocation: Location? = null
    private lateinit var latLng: LatLng
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    val REQ_CODE = 101


    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var delay = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrieveData()
    }

    //Pressed the back button to return to the MainActivity
    override fun onBackPressed() {
        val intentMain = Intent(this, MainActivity::class.java)
        intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intentMain)
        finish()
    }

    //Remember to turn on the location in your phone so it can locate your current location
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        //Locate your current position button
        googleMap.isMyLocationEnabled = true

        latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)

        val markerOption = MarkerOptions().position(latLng).title("You are here!")

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        googleMap.addMarker(markerOption)

        //Test the marker
        for (store in storeList){
            val storeLatLng = LatLng(store.latitude, store.longtitude)

            val markerStoreOption = MarkerOptions().position(storeLatLng).title("IotCharge")
            googleMap.addMarker(markerStoreOption)
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQ_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQ_CODE
            )
            return
        }

        val task = fusedLocationProviderClient!!.lastLocation

        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location

                val supportMapManager =
                    (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)

                supportMapManager!!.getMapAsync(this@MapsActivity)
            }
        }
    }

    //get all the IotCharge Store
    private fun retrieveData() {
        database = FirebaseDatabase.getInstance().getReference("LocationStore")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (locationSnapshot in snapshot.children) {
                        val location = locationSnapshot.getValue(LocationStore::class.java)
                        storeList.add(location as LocationStore)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onResume() {
        super.onResume()

        /**
         * Start Countdown method to execute code check if map is empty
         */
        mHandler.postDelayed(Runnable {
            mHandler.postDelayed(runnable!!, delay.toLong())
            if (storeList.isEmpty()) {

                retrieveData()
                Toast.makeText(this, CONSTANT.LOADING, Toast.LENGTH_SHORT).show()

            } else {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

                getCurrentLocation()

                /**
                 * Stop Handler countdown
                 */
                runnable?.let { mHandler.removeCallbacks(it) }
            }

            Log.d("#handlerCheck", "${delay / 1000} second has passed")

        }.also { runnable = it }, delay.toLong())
    }

    override fun onStop() {
        /**
         * Stop Handler countdown
         */
        runnable?.let { mHandler.removeCallbacks(it) }

        super.onStop()
    }
}