package com.example.sprintone


import android.content.ContentValues
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.sprintone.databinding.ActivityMapsBinding
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale

// Don't use this activity, use the new "MapsComposeActivity".
// I don't want to delete this because it might mess up the AndroidManifest.xml...

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

    // **** Don't use this activity, use the new "MapsComposeActivity".
    // **** I don't want to delete this because it might mess up the AndroidManifest.xml...

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val geocode = Geocoder(this, Locale.getDefault())
        val db = Firebase.firestore

        try {
            db.collection("trucks")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val location = document.getString("Location")
                        val truckName = document.getString("Name")
                        val type = document.getString("Type")
                        if (location != null) {
                            val addList = geocode.getFromLocationName(location, 1)
                            val lat = addList!![0].latitude
                            val long = addList[0].longitude

                            val pin = LatLng(lat, long)
                            googleMap.addMarker(
                                MarkerOptions()
                                    .position(pin)
                                    .title(truckName)
                                    .snippet(type)
                            )
                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    pin,
                                    12.0f
                                )
                            ) // Adjust zoom level as needed
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

// Don't use this activity, use the new "MapsComposeActivity".
// I don't want to delete this because it might mess up the AndroidManifest.xml...

