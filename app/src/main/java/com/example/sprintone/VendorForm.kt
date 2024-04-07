package com.example.sprintone

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VendorForm : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SprintOneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Red
                ) {
                    LoadVendorForm()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoadVendorForm()
{
    val scrollState = rememberScrollState()

    var truckName by remember { mutableStateOf("") }
    var truckDescription by remember { mutableStateOf("") }
    var truckType by remember { mutableStateOf("") }
    var truckLocation by remember { mutableStateOf("") }
    var truckHours by remember { mutableStateOf("")}
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val db = Firebase.firestore
    val coroutineScope = rememberCoroutineScope()

    Surface(color = Color.White, modifier = Modifier.fillMaxSize())
    {

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(scrollState)

        ) {
            Text(
                text = "Vendor Form",
                modifier = Modifier.padding(top = 4.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
            Text(
                text = "Please enter your business information below."
            )
            OutlinedTextField(
                value = truckName,
                onValueChange = { truckName = it },
                label = { Text("Vendor Name") },
                modifier = Modifier.fillMaxWidth().padding(4.dp).align(Alignment.CenterHorizontally)

            )
            OutlinedTextField(
                value = truckDescription,
                onValueChange = { truckDescription = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().padding(4.dp)

            )
            OutlinedTextField(
                value = truckType,
                onValueChange = { truckType = it },
                label = { Text("Type") },
                modifier = Modifier.fillMaxWidth().padding(4.dp)
            )
            OutlinedTextField(
                value = truckLocation,
                onValueChange = { truckLocation = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth().padding(4.dp)
            )
            Button(
                onClick = {
                    val truck = hashMapOf(
                        "Name" to truckName,
                        "Type" to truckType,
                        "Location" to truckLocation,
                        "Description" to truckDescription
                    )
                    db.collection("trucks").whereEqualTo("Name", truckName)
                        .get()
                        .addOnSuccessListener {documents ->
                            if(documents.isEmpty)
                            {
                                db.collection("trucks").add(truck)
                                successMessage = "Success! Your listing has been added."
                                coroutineScope.launch {
                                    delay(5000)
                                    successMessage = null
                                }
                            }
                            else{
                                errorMessage = "This vendor already exists. Please choose a different name."
                                coroutineScope.launch {
                                    delay(5000)
                                    errorMessage = null
                                }
                            }
                        }
                },
                enabled = truckName.isNotBlank()
                        && truckDescription.isNotBlank()
                        && truckType.isNotBlank()
                        && truckLocation.isNotBlank()
            ) {
                Text("Add Truck")
            }
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
            successMessage?.let {
                Text(
                    text = it,
                    color = Color.Green,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}