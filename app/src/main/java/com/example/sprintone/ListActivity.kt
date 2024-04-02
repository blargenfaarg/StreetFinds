package com.example.sprintone

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class ListActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SprintOneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Red
                ) {
                    DisplayList()
                }
            }
        }
    }
}

data class Truck(
    val name: String,
    val location: String,
    val description: String,
    val type: String
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DisplayList() {
    val scrollState = rememberScrollState()
    val truckListState = remember { mutableStateOf<List<Truck>>(emptyList()) }

    LaunchedEffect(key1 = Unit) {
        val db = Firebase.firestore
        try {
            val querySnapshot = db.collection("trucks").get().await()
            val trucks = querySnapshot.documents.mapNotNull { document ->
                val truckName = document.getString("Name")
                val location = document.getString("Location")
                val description = document.getString("Description")
                val type = document.getString("Type")

                if (truckName != null && location != null && description != null && type != null) {
                    Truck(truckName, location, description, type)
                } else {
                    null
                }
            }
            truckListState.value = trucks
        } catch (e: Exception) {
            Log.e(TAG, "Error getting documents: ", e)
        }
    }

    Surface(color = Color.Red, modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            truckListState.value.forEach { truck ->
                Card(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        text = truck.name,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp,
                    )
                    Text(
                        text = truck.type,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(10.dp)
                    )
                    Text(
                        text = truck.location,
                        modifier = Modifier
                            .padding(10.dp)

                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        text = truck.description,
                        color = Color.Black,
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        text = "Hours: x AM to x PM",
                        color = Color.Black,
                    )
                }
            }
        }
    }
}

