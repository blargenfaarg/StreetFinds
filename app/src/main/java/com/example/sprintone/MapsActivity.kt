package com.example.sprintone


import android.content.ContentValues
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.sprintone.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Locale


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //getPinLocation()

        val geocode = Geocoder(this, Locale.getDefault())

        val db = Firebase.firestore
        try {
            db.collection("trucks")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val location = document.getString("Location")
                        if (location != null) {
                            val addList = geocode.getFromLocationName(location, 1)
                            val lat = addList!![0].latitude
                            val long = addList[0].longitude

                            val pin = LatLng(lat, long)
                            googleMap.addMarker(
                                MarkerOptions()
                                    .position(pin)
                                    .title("Marker")
                            )

                        } else {
                            println("Location not found for document ${document.id}")
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error getting documents: ", e)
        }
    }
}
