package com.example.sprintone

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale

class MapsComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SprintOneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoadMap()
                }
            }
        }
    }
}

@Composable
fun LoadMap() {
    var markerData by remember { mutableStateOf<List<MarkerData>>(emptyList()) }
    val context = LocalContext.current
    val camarillo = LatLng(34.2164, -119.0376)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(camarillo, 12f)
    }

    LaunchedEffect(Unit)
    {
        val db = Firebase.firestore
        val geocode = Geocoder(context, Locale.getDefault())

        try {
            db.collection("trucks")
                .get()
                .addOnSuccessListener { documents ->
                    val newData = mutableListOf<MarkerData>()
                    for (document in documents) {
                        val location = document.getString("Location")
                        val truckName = document.getString("Name")
                        val type = document.getString("Type")
                        if (location != null) {
                            val addList = geocode.getFromLocationName(location, 1)
                            val lat = addList!![0].latitude
                            val long = addList[0].longitude
                            newData.add(MarkerData(LatLng(lat, long), truckName ?: "", type ?: ""))
                        } else {
                            Log.d("LoadMap", "Location not found for document ${document.id}")
                        }
                    }
                    markerData = newData
                }
        } catch (e: Exception) {
            Log.e("LoadMap", "Error getting documents: ", e)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LoadSearchBar()
        GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState) {
            markerData.forEach { marker ->
                AdvancedMarker(
                    state = MarkerState(position = marker.position),
                    title = marker.title, snippet = marker.snippet,
                    onInfoWindowClick = {
                        context.startActivity(Intent(context, ListActivity::class.java))
                    }
                )
            }
        }
    }
}
data class MarkerData(val position: LatLng, val title: String, val snippet: String)
