package com.example.sprintone

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await
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
                    Scaffold(
                        bottomBar = { LoadNavBar() },
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .padding(innerPadding),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        )
                        {
                            val extras = intent.extras
                            val test = extras?.getString("ignore")

                            if (test.equals("ignore"))
                            {
                                LoadMap()
                            }
                            else{
                                Log.e("Error", "Intent.extras.isEmpty is false")
                                val query = extras?.getString("query")
                                LoadMapBySearch(query.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable fun LoadMapBySearch(query : String)
{
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
            val querySnapshot = db.collection("trucks")
                .whereEqualTo("Name", query)
                .get()
                .await()
            if (!querySnapshot.isEmpty)
            {
                val document = querySnapshot.documents.first()
                val location = document.getString("Location")
                val truckName = document.getString("Name")
                val type = document.getString("Type")
                val newData = mutableListOf<MarkerData>()

                if (location != null) {
                    val addList = geocode.getFromLocationName(location, 1)
                    val lat = addList!![0].latitude
                    val long = addList[0].longitude
                    newData.add(MarkerData(LatLng(lat, long), truckName ?: "", type ?: ""))
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(lat, long), 12f)

                } else {
                    Log.d("LoadMap", "Location not found for document ${document.id}")
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
                Marker(
                    state = MarkerState(position = marker.position),
                    title = marker.title, snippet = marker.snippet,
                    icon = bitmapDescriptorFromVector(context, R.drawable.truckpin),
                    onInfoWindowClick = {
                        val intent = Intent(context, VendorProfilePage::class.java)
                        intent.putExtra("name", marker.title)
                        context.startActivity(intent)
                    }
                )
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
                Marker(
                    state = MarkerState(position = marker.position),
                    title = marker.title, snippet = marker.snippet,
                    icon = bitmapDescriptorFromVector(context, R.drawable.truckpin),
                    onInfoWindowClick = {

                        val intent = Intent(context, VendorProfilePage::class.java)
                        intent.putExtra("name", marker.title)
                        context.startActivity(intent)

                    }
                )
            }
        }
    }
}




data class MarkerData(val position: LatLng, val title: String, val snippet: String)

private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
    vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}