package com.example.sprintone

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class VendorPage : AppCompatActivity() {
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
                    LoadVendorPage()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoadVendorPage()
{
    var truckName by remember { mutableStateOf("") }
    var truckDescription by remember { mutableStateOf("") }
    var truckType by remember { mutableStateOf("") }
    var truckLocation by remember { mutableStateOf("") }

    val db = Firebase.firestore

    Surface(color = Color.White, modifier = Modifier.fillMaxSize())
    {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = truckName,
                onValueChange = { truckName = it },
                label = { Text("Name") }
            )
            OutlinedTextField(
                value = truckDescription,
                onValueChange = { truckDescription = it },
                label = { Text("Description") }
            )
            OutlinedTextField(
                value = truckType,
                onValueChange = { truckType = it },
                label = { Text("Type") }
            )
            OutlinedTextField(
                value = truckLocation,
                onValueChange = { truckLocation = it },
                label = { Text("Location") }
            )

            Button(
                onClick = {
                    val truck = hashMapOf(
                        "Name" to truckName,
                        "Type" to truckType,
                        "Location" to truckLocation,
                        "Description" to truckDescription
                    )
                    db.collection("trucks").add(truck)
                },
                enabled = truckName.isNotBlank()
                        && truckDescription.isNotBlank()
                        && truckType.isNotBlank()
                        && truckLocation.isNotBlank()
            ) {
                Text("Add Truck")
            }
        }
    }
}