package com.example.sprintone

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.input.KeyboardType
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
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val email = getUserEmail(context)

    var truckName by remember { mutableStateOf("") }
    var truckPhoneNumber by remember { mutableStateOf("") }
    var truckDescription by remember { mutableStateOf("") }
    var truckType by remember { mutableStateOf("") }
    var truckLocation by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isPhoneValid by remember { mutableStateOf(true) }

    var truckMondayHours by remember { mutableStateOf("") }
    var truckTuesdayHours by remember { mutableStateOf("") }
    var truckWednesdayHours by remember { mutableStateOf("") }
    var truckThursdayHours by remember { mutableStateOf("") }
    var truckFridayHours by remember { mutableStateOf("") }
    var truckSaturdayHours by remember { mutableStateOf("") }
    var truckSundayHours by remember { mutableStateOf("") }

    var dialogMondayHours by remember { mutableStateOf("") }
    var dialogTuesdayHours by remember { mutableStateOf("") }
    var dialogWednesdayHours by remember { mutableStateOf("") }
    var dialogThursdayHours by remember { mutableStateOf("") }
    var dialogFridayHours by remember { mutableStateOf("") }
    var dialogSaturdayHours by remember { mutableStateOf("") }
    var dialogSundayHours by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false)}

    val db = Firebase.firestore
    val coroutineScope = rememberCoroutineScope()
    val phoneNumberRegex = Regex("\\(\\d{3}\\)\\s\\d{3}-\\d{4}|\\d{10}")


    Surface(color = Color.White, modifier = Modifier.fillMaxSize())
    {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(20.dp)

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .align(Alignment.CenterHorizontally)
            )
            OutlinedTextField(
                value = truckPhoneNumber,
                onValueChange = {
                    truckPhoneNumber = it
                    isPhoneValid = phoneNumberRegex.matches(it)
                                },
                label = {Text("Business Phone Number") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .align(Alignment.CenterHorizontally),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            if (!isPhoneValid)
            {
                Text(
                    text = "Please enter a valid phone number (e.g. (123) 456-7890",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )
            }
            OutlinedTextField(
                value = truckDescription,
                onValueChange = { truckDescription = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
            OutlinedTextField(
                value = truckType,
                onValueChange = { truckType = it },
                label = { Text("Type") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
            OutlinedTextField(
                value = truckLocation,
                onValueChange = { truckLocation = it },
                label = { Text("Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
            Text(
                text = "Business Hours",
                modifier = Modifier.padding(top = 16.dp),
                fontWeight = FontWeight.Bold
            )
            Button(onClick = {showDialog = true})
            {
                Text("Input Business Hours")
            }

            if (showDialog)
            {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        Button(onClick = {
                        truckMondayHours = dialogMondayHours
                        truckTuesdayHours = dialogTuesdayHours
                        truckWednesdayHours = dialogWednesdayHours
                        truckThursdayHours = dialogThursdayHours
                        truckFridayHours = dialogFridayHours
                        truckSaturdayHours = dialogSaturdayHours
                        truckSundayHours = dialogSundayHours
                        showDialog = false
                    }, enabled = dialogMondayHours.isNotBlank()
                                || dialogTuesdayHours.isNotBlank()
                                || dialogWednesdayHours.isNotBlank()
                                || dialogThursdayHours.isNotBlank()
                                || dialogFridayHours.isNotBlank()
                                || dialogSaturdayHours.isNotBlank()
                                || dialogSundayHours.isNotBlank()
                        )
                        {
                        Text("Save")
                    } },
                    dismissButton = {
                                    Button(onClick = {
                                        showDialog = false
                                    }){
                                        Text("Cancel")
                                    }
                    },
                    title = {Text("Enter Business Hours")},
                    text = {
                        Column{

                        OutlinedTextField(
                            value = dialogMondayHours,
                            onValueChange = { dialogMondayHours = it },
                            label = { Text("Monday") },
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        )
                        OutlinedTextField(
                            value = dialogTuesdayHours,
                            onValueChange = { dialogTuesdayHours = it },
                            label = { Text("Tuesday") },
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        )
                        OutlinedTextField(
                            value = dialogWednesdayHours,
                            onValueChange = { dialogWednesdayHours = it },
                            label = { Text("Wednesday") },
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        )
                        OutlinedTextField(
                            value = dialogThursdayHours,
                            onValueChange = { dialogThursdayHours = it },
                            label = { Text("Thursday") },
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        )
                        OutlinedTextField(
                            value = dialogFridayHours,
                            onValueChange = { dialogFridayHours = it },
                            label = { Text("Friday") },
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        )
                        OutlinedTextField(
                            value = dialogSaturdayHours,
                            onValueChange = { dialogSaturdayHours = it },
                            label = { Text("Saturday") },
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        )
                        OutlinedTextField(
                            value = dialogSundayHours,
                            onValueChange = { dialogSundayHours = it },
                            label = { Text("Sunday") },
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        )
                    } }
                    )
            }

            Button(
                onClick = {
                    val truck = hashMapOf(
                        "Name" to truckName,
                        "Phone" to truckPhoneNumber,
                        "Type" to truckType,
                        "Location" to truckLocation,
                        "Description" to truckDescription,
                        "Monday Hours" to truckMondayHours,
                        "Tuesday Hours" to truckTuesdayHours,
                        "Wednesday Hours" to truckWednesdayHours,
                        "Thursday Hours" to truckThursdayHours,
                        "Friday Hours" to truckFridayHours,
                        "Saturday Hours" to truckSaturdayHours,
                        "Sunday Hours" to truckSundayHours,
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
                                    context.startActivity(Intent(context, ListActivity::class.java))
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

                    db.collection("vendors").whereEqualTo("Email", email)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                val vendorDoc = documents.documents.first()
                                val vendorId = vendorDoc.id
                                db.collection("vendors").document(vendorId).update("Business Name", truckName)
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