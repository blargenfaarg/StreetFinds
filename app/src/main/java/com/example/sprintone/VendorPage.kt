package com.example.sprintone

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class VendorPage : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SprintOneTheme {
                Scaffold(
                    bottomBar = {
                        LoadNavBar()
                    },
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding))
                    {
                        VendorGreeting()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview
fun VendorGreeting() {
    val context = LocalContext.current
    val db = Firebase.firestore
    val email = getUserEmail(context)
    val scrollState = rememberScrollState()

    var businessName by remember { mutableStateOf("") }
    var businessDescription by remember { mutableStateOf("") }
    var businessType by remember { mutableStateOf("") }
    var businessLocation by remember { mutableStateOf("") }
    var hasEnteredBusiness by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showHoursDialog by remember { mutableStateOf(false) }

    var newBusinessName by remember { mutableStateOf("") }
    var newBusinessDescription by remember { mutableStateOf("") }
    var newBusinessLocation by remember { mutableStateOf("") }
    var newBusinessType by remember { mutableStateOf("") }

    var nameWasChanged by remember { mutableStateOf(false) }
    var descriptionWasChanged by remember { mutableStateOf(false) }
    var locationWasChanged by remember { mutableStateOf(false) }
    var typeWasChanged by remember { mutableStateOf(false) }
    var wereHoursUpdated by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("New Type") }

    var truckMondayHours = remember { mutableStateOf("Closed") }
    var truckTuesdayHours = remember { mutableStateOf("Closed") }
    var truckWednesdayHours = remember { mutableStateOf("Closed") }
    var truckThursdayHours = remember { mutableStateOf("Closed") }
    var truckFridayHours = remember { mutableStateOf("Closed") }
    var truckSaturdayHours = remember { mutableStateOf("Closed") }
    var truckSundayHours = remember { mutableStateOf("Closed") }

    val truckHours = remember {
        mutableStateOf(
            mapOf(
                "Monday" to mutableStateOf(Pair("", "")),
                "Tuesday" to mutableStateOf(Pair("", "")),
                "Wednesday" to mutableStateOf(Pair("", "")),
                "Thursday" to mutableStateOf(Pair("", "")),
                "Friday" to mutableStateOf(Pair("", "")),
                "Saturday" to mutableStateOf(Pair("", "")),
                "Sunday" to mutableStateOf(Pair("", ""))
            )
        )
    }


    Surface(modifier = Modifier.fillMaxSize())
    {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Welcome $email",
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
            )

            db.collection("vendors").whereEqualTo("Email", email)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        businessName = document.getString("Business Name").toString()
                    }
                    db.collection("trucks").whereEqualTo("Name", businessName)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.isEmpty) {
                                hasEnteredBusiness = false
                            } else {
                                for (document in querySnapshot) {
                                    hasEnteredBusiness = true
                                    businessDescription =
                                        document.getString("Description").toString()
                                    businessLocation = document.getString("Location").toString()
                                    businessType = document.getString("Type").toString()
                                    truckMondayHours.value =
                                        document.getString("Monday Hours").toString()
                                    truckTuesdayHours.value =
                                        document.getString("Tuesday Hours").toString()
                                    truckWednesdayHours.value =
                                        document.getString("Wednesday Hours").toString()
                                    truckThursdayHours.value =
                                        document.getString("Thursday Hours").toString()
                                    truckFridayHours.value =
                                        document.getString("Friday Hours").toString()
                                    truckSaturdayHours.value =
                                        document.getString("Saturday Hours").toString()
                                    truckSundayHours.value =
                                        document.getString("Sunday Hours").toString()
                                }
                            }
                        }
                        .addOnFailureListener {
                            Log.e("Error", "Another error happened.")
                        }
                }
                .addOnFailureListener {
                    Log.e("Error", "Couldn't find a matching document")
                }
            if (hasEnteredBusiness) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black
                                    )
                                )
                                {
                                    append("Your Business Name: ")
                                }
                                append(businessName)
                            }, modifier = Modifier.padding(8.dp)
                        )
                        Divider()
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black
                                    )
                                )
                                {
                                    append("Your Business Type: ")
                                }
                                append(businessType)
                            }, modifier = Modifier.padding(8.dp)
                        )
                        Divider()
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black
                                    )
                                )
                                {
                                    append("Your Business Description: ")
                                }
                                append(businessDescription)
                            }, modifier = Modifier.padding(8.dp)
                        )
                        Divider()
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black
                                    )
                                )
                                {
                                    append("Your Business Location: ")
                                }
                                append(businessLocation)
                            }, modifier = Modifier.padding(8.dp)
                        )
                        Divider()
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black
                                    )
                                )
                                {
                                    append("Your Business Hours: \n")
                                }
                                append("Monday Hours: ${truckMondayHours.value}\n")
                                append("Tuesday Hours: ${truckTuesdayHours.value}\n")
                                append("Wednesday Hours: ${truckWednesdayHours.value}\n")
                                append("Thursday Hours: ${truckThursdayHours.value}\n")
                                append("Friday Hours: ${truckFridayHours.value}\n")
                                append("Saturday Hours: ${truckSaturdayHours.value}\n")
                                append("Sunday Hours: ${truckSundayHours.value}")

                            }, modifier = Modifier.padding(8.dp)
                        )
                        Divider()
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        {
                            Button(onClick = { showDialog = true })
                            {
                                Text("Update Vendor Info")
                            }
                            PickImageFromGallery()
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDialog = false
                                    if (nameWasChanged) {
                                        db.collection("trucks").whereEqualTo("Name", businessName)
                                            .get()
                                            .addOnSuccessListener { documents ->
                                                if (!documents.isEmpty) {
                                                    val vendorDoc = documents.documents.first()
                                                    val vendorId = vendorDoc.id
                                                    db.collection("trucks").document(vendorId)
                                                        .update("Name", newBusinessName)
                                                    businessName = newBusinessName
                                                }
                                            }

                                        db.collection("vendors")
                                            .whereEqualTo("Business Name", businessName)
                                            .get()
                                            .addOnSuccessListener { documents ->
                                                if (!documents.isEmpty) {
                                                    val vendorDoc = documents.documents.first()
                                                    val vendorId = vendorDoc.id
                                                    db.collection("vendors").document(vendorId)
                                                        .update("Business Name", newBusinessName)
                                                }
                                            }
                                    }
                                    if (descriptionWasChanged) {
                                        db.collection("trucks").whereEqualTo("Name", businessName)
                                            .get()
                                            .addOnSuccessListener { documents ->
                                                if (!documents.isEmpty) {
                                                    val vendorDoc = documents.documents.first()
                                                    val vendorId = vendorDoc.id
                                                    db.collection("trucks").document(vendorId)
                                                        .update(
                                                            "Description",
                                                            newBusinessDescription
                                                        )

                                                    businessDescription = newBusinessDescription
                                                }
                                            }
                                    }
                                    if (locationWasChanged) {
                                        db.collection("trucks").whereEqualTo("Name", businessName)
                                            .get()
                                            .addOnSuccessListener { documents ->
                                                if (!documents.isEmpty) {
                                                    val vendorDoc = documents.documents.first()
                                                    val vendorId = vendorDoc.id
                                                    db.collection("trucks").document(vendorId)
                                                        .update("Location", newBusinessLocation)

                                                    businessLocation = newBusinessLocation
                                                }
                                            }
                                    }
                                    if (typeWasChanged) {
                                        db.collection("trucks").whereEqualTo("Name", businessName)
                                            .get()
                                            .addOnSuccessListener { documents ->
                                                if (!documents.isEmpty) {
                                                    val vendorDoc = documents.documents.first()
                                                    val vendorId = vendorDoc.id
                                                    db.collection("trucks").document(vendorId)
                                                        .update("Type", newBusinessType)

                                                    businessType = newBusinessType
                                                }
                                            }
                                    }
                                    if (wereHoursUpdated) {
                                        db.collection("trucks").whereEqualTo("Name", businessName)
                                            .get()
                                            .addOnSuccessListener { documents ->
                                                if (!documents.isEmpty) {
                                                    val vendorDoc = documents.documents.first()
                                                    val vendorId = vendorDoc.id
                                                    db.collection("trucks").document(vendorId)
                                                        .update(
                                                            "Monday Hours",
                                                            truckMondayHours.value
                                                        )
                                                    db.collection("trucks").document(vendorId)
                                                        .update(
                                                            "Tuesday Hours",
                                                            truckTuesdayHours.value
                                                        )
                                                    db.collection("trucks").document(vendorId)
                                                        .update(
                                                            "Wednesday Hours",
                                                            truckWednesdayHours.value
                                                        )
                                                    db.collection("trucks").document(vendorId)
                                                        .update(
                                                            "Thursday Hours",
                                                            truckThursdayHours.value
                                                        )
                                                    db.collection("trucks").document(vendorId)
                                                        .update(
                                                            "Friday Hours",
                                                            truckFridayHours.value
                                                        )
                                                    db.collection("trucks").document(vendorId)
                                                        .update(
                                                            "Saturday Hours",
                                                            truckSaturdayHours.value
                                                        )
                                                    db.collection("trucks").document(vendorId)
                                                        .update(
                                                            "Sunday Hours",
                                                            truckSundayHours.value
                                                        )
                                                }
                                            }
                                    }
                                },
                                enabled = (newBusinessName.isNotBlank()
                                        || newBusinessType.isNotBlank()
                                        || newBusinessDescription.isNotBlank()
                                        || newBusinessLocation.isNotBlank()) || wereHoursUpdated
                            )
                            {
                                Text("Update")
                            }
                        },
                        dismissButton = {
                            Button(onClick = {
                                showDialog = false
                            }) {
                                Text("Cancel")
                            }
                        },
                        title =
                        {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            )
                            {
                                Text("Update information", fontWeight = FontWeight.ExtraBold)
                            }
                        },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = newBusinessName,
                                    onValueChange = {
                                        newBusinessName = it
                                        if (newBusinessName != businessName) {
                                            nameWasChanged = true
                                        } else {
                                            nameWasChanged = false
                                        }
                                    },
                                    label = { Text("New Name") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                )

                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                )
                                {
                                    OutlinedTextField(
                                        value = selectedText,
                                        onValueChange = {},
                                        readOnly = true,
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }) {
                                        DropdownMenuItem(text = { Text("American") },
                                            onClick = {
                                                newBusinessType = "American"
                                                selectedText = newBusinessType
                                                expanded = false
                                                typeWasChanged = true

                                            })
                                        DropdownMenuItem(text = { Text("Mexican") },
                                            onClick = {
                                                newBusinessType = "Mexican"
                                                selectedText = newBusinessType
                                                expanded = false
                                                typeWasChanged = true

                                            })
                                        DropdownMenuItem(text = { Text("Fusion") },
                                            onClick = {
                                                newBusinessType = "Fusion"
                                                selectedText = newBusinessType
                                                expanded = false
                                                typeWasChanged = true

                                            })
                                        DropdownMenuItem(text = { Text("Asian") },
                                            onClick = {
                                                newBusinessType = "Asian"
                                                selectedText = newBusinessType
                                                expanded = false
                                                typeWasChanged = true

                                            })
                                        DropdownMenuItem(text = { Text("Seafood") },
                                            onClick = {
                                                newBusinessType = "Seafood"
                                                selectedText = newBusinessType
                                                expanded = false
                                                typeWasChanged = true

                                            })
                                        DropdownMenuItem(text = { Text("Breakfast") },
                                            onClick = {
                                                newBusinessType = "Breakfast"
                                                selectedText = newBusinessType
                                                expanded = false
                                                typeWasChanged = true

                                            })
                                        DropdownMenuItem(text = { Text("Italian") },
                                            onClick = {
                                                newBusinessType = "Italian"
                                                selectedText = newBusinessType
                                                expanded = false
                                                typeWasChanged = true

                                            })
                                    }
                                }

                                OutlinedTextField(
                                    value = newBusinessDescription,
                                    onValueChange = {
                                        newBusinessDescription = it
                                        if (newBusinessDescription == businessDescription) {

                                        } else {
                                            descriptionWasChanged = true
                                        }
                                    },
                                    label = { Text("New Description") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                )
                                OutlinedTextField(
                                    value = newBusinessLocation,
                                    onValueChange = {
                                        newBusinessLocation = it
                                        if (newBusinessLocation == businessLocation) {

                                        } else {
                                            locationWasChanged = true
                                        }
                                    },
                                    label = { Text("New Location") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                )
                                {
                                    Button(onClick = { showHoursDialog = true })
                                    { Text("Update Hours") }
                                }

                                if (showHoursDialog) {
                                    val updateHours = { day: String, hours: String ->
                                        when (day) {
                                            "Monday" -> truckMondayHours.value = hours
                                            "Tuesday" -> truckTuesdayHours.value = hours
                                            "Wednesday" -> truckWednesdayHours.value = hours
                                            "Thursday" -> truckThursdayHours.value = hours
                                            "Friday" -> truckFridayHours.value = hours
                                            "Saturday" -> truckSaturdayHours.value = hours
                                            "Sunday" -> truckSundayHours.value = hours
                                        }
                                    }
                                    AlertDialog(
                                        onDismissRequest = { showDialog = false },
                                        confirmButton = {
                                            Button(onClick = {
                                                showHoursDialog = false
                                                wereHoursUpdated = true
                                            })
                                            {
                                                Text("Save")
                                            }
                                        },
                                        dismissButton = {
                                            Button(onClick = { showHoursDialog = false })
                                            {
                                                Text("Cancel")
                                            }
                                        },
                                        title = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Center
                                            )
                                            {
                                                Text(
                                                    "Enter New Hours",
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        },
                                        text = {
                                            Column {
                                                WeeklyBusinessHours(
                                                    truckHours = truckHours,
                                                    updateHours
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }

            if (!hasEnteredBusiness) {
                Spacer(modifier = Modifier.padding(15.dp))
                Card(
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(Color.hsl(225f, 0.6f, 0.9f))
                ) {
                    Text(
                        text = "To list your business, please fill out the vendor form.",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )

                    Button(
                        onClick = {
                            context.startActivity(
                                Intent(
                                    context,
                                    VendorForm::class.java
                                )
                            )
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    {
                        Text(text = "Vendor Form")
                    }
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                onClick = {
                    val sharedPreferences =
                        context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()
                    context.startActivity(Intent(context, MainActivity::class.java))
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(200.dp, 50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            )
            {
                Text("Sign out", fontSize = 24.sp)
            }
        }
    }
}



