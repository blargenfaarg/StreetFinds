package com.example.sprintone

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
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
                ) {
                    LoadVendorForm()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoadVendorForm() {
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
    var selectedText by remember { mutableStateOf("Type") }

    val truckMondayHours = remember { mutableStateOf("Closed") }
    val truckTuesdayHours = remember { mutableStateOf("Closed") }
    val truckWednesdayHours = remember { mutableStateOf("Closed") }
    val truckThursdayHours = remember { mutableStateOf("Closed") }
    val truckFridayHours = remember { mutableStateOf("Closed") }
    val truckSaturdayHours = remember { mutableStateOf("Closed") }
    val truckSundayHours = remember { mutableStateOf("Closed") }

    val showHoursDialog = remember { mutableStateOf(false) }
    val hasVendorEnteredHours = remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val db = Firebase.firestore
    val coroutineScope = rememberCoroutineScope()
    val phoneNumberRegex = Regex("\\(\\d{3}\\)\\s\\d{3}-\\d{4}|\\d{10}")

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

    Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            Card(colors = CardDefaults.cardColors(Color.hsl(225f, 0.6f, 0.9f)),
                modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
                {
                    Text(
                        text = "Vendor Form",
                        modifier = Modifier.padding(top = 4.dp),
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 30.sp
                    )
                }

                Text(
                    text = "Please enter your business information below.",
                    modifier = Modifier.padding(16.dp)
                )
                OutlinedTextField(
                    value = truckName,
                    onValueChange = { truckName = it },
                    label = { Text("Vendor Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .align(Alignment.CenterHorizontally)
                )
                OutlinedTextField(
                    value = truckPhoneNumber,
                    onValueChange = {
                        truckPhoneNumber = it
                        isPhoneValid = phoneNumberRegex.matches(it)
                    },
                    label = { Text("Business Phone Number") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .align(Alignment.CenterHorizontally),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                if (!isPhoneValid) {
                    Text(
                        text = "Please enter a valid phone number (e.g. (123) 456-7890)",
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
                        .height(150.dp)
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                {
                    OutlinedTextField(
                        value = selectedText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    ExposedDropdownMenu(expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("American") }, onClick = {
                            truckType = "American"
                            selectedText = truckType
                            expanded = false
                        })
                        DropdownMenuItem(text = { Text("Mexican") }, onClick = {
                            truckType = "Mexican"
                            selectedText = truckType
                            expanded = false
                        })
                        DropdownMenuItem(text = { Text("Fusion") }, onClick = {
                            truckType = "Fusion"
                            selectedText = truckType
                            expanded = false
                        })
                        DropdownMenuItem(text = { Text("Asian") }, onClick = {
                            truckType = "Asian"
                            selectedText = truckType
                            expanded = false
                        })
                        DropdownMenuItem(text = { Text("Seafood") }, onClick = {
                            truckType = "Seafood"
                            selectedText = truckType
                            expanded = false
                        })
                        DropdownMenuItem(text = { Text("Breakfast") }, onClick = {
                            truckType = "Breakfast"
                            selectedText = truckType
                            expanded = false
                        })
                        DropdownMenuItem(text = { Text("Italian") }, onClick = {
                            truckType = "Italian"
                            selectedText = truckType
                            expanded = false
                        })
                    }
                }
                OutlinedTextField(
                    value = truckLocation,
                    onValueChange = { truckLocation = it },
                    label = { Text("Address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center)
                {
                    OutlinedButton(onClick = { showHoursDialog.value = true }
                        , modifier = Modifier.fillMaxWidth()) {
                        Text("Input Business Hours")
                    }
                }

                if (showHoursDialog.value) {
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
                    AlertDialog(onDismissRequest = {
                        truckMondayHours.value = "Closed"
                        truckTuesdayHours.value = "Closed"
                        truckWednesdayHours.value = "Closed"
                        truckThursdayHours.value = "Closed"
                        truckFridayHours.value = "Closed"
                        truckSaturdayHours.value = "Closed"
                        truckSundayHours.value = "Closed"
                        showHoursDialog.value = false
                    }, confirmButton = {
                        Button(onClick = {
                            showHoursDialog.value = false
                            hasVendorEnteredHours.value = true
                        }) { Text("Add Hours") }
                    }, dismissButton = {
                        Button(onClick = {
                            truckMondayHours.value = "Closed"
                            truckTuesdayHours.value = "Closed"
                            truckWednesdayHours.value = "Closed"
                            truckThursdayHours.value = "Closed"
                            truckFridayHours.value = "Closed"
                            truckSaturdayHours.value = "Closed"
                            truckSundayHours.value = "Closed"
                            showHoursDialog.value = false

                        }) { Text("Cancel") }
                    }, text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row() {
                                Text(
                                    "Opening Hours",
                                    modifier = Modifier.padding(end = 8.dp),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Closing Hours",
                                    modifier = Modifier.padding(start = 8.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            WeeklyBusinessHours(truckHours = truckHours, updateHours)
                        }
                    })
                }
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    , horizontalArrangement = Arrangement.Center)
                {
                    Button(onClick = {
                        val truck = hashMapOf(
                            "Name" to truckName,
                            "Phone" to truckPhoneNumber,
                            "Type" to truckType,
                            "Location" to truckLocation,
                            "Description" to truckDescription,
                            "Monday Hours" to truckMondayHours.value,
                            "Tuesday Hours" to truckTuesdayHours.value,
                            "Wednesday Hours" to truckWednesdayHours.value,
                            "Thursday Hours" to truckThursdayHours.value,
                            "Friday Hours" to truckFridayHours.value,
                            "Saturday Hours" to truckSaturdayHours.value,
                            "Sunday Hours" to truckSundayHours.value,
                        )
                        db.collection("trucks").whereEqualTo("Name", truckName).get()
                            .addOnSuccessListener { documents ->
                                if (documents.isEmpty) {
                                    db.collection("trucks").add(truck)
                                    successMessage = "Success! Your listing has been added."
                                    coroutineScope.launch {
                                        delay(5000)
                                        successMessage = null
                                        context.startActivity(
                                            Intent(
                                                context, ListActivity::class.java
                                            )
                                        )
                                    }
                                } else {
                                    errorMessage =
                                        "This vendor already exists. Please choose a different name."
                                    coroutineScope.launch {
                                        delay(5000)
                                        errorMessage = null
                                    }
                                }
                            }

                        db.collection("vendors").whereEqualTo("Email", email).get()
                            .addOnSuccessListener { documents ->
                                if (!documents.isEmpty) {
                                    val vendorDoc = documents.documents.first()
                                    val vendorId = vendorDoc.id
                                    db.collection("vendors").document(vendorId)
                                        .update("Business Name", truckName)
                                }
                            }

                    }, enabled = truckName.isNotBlank()
                            && truckDescription.isNotBlank()
                            && truckType.isNotBlank()
                            && truckLocation.isNotBlank()
                            && hasVendorEnteredHours.value, modifier = Modifier.fillMaxWidth()
                    ) { Text("Add Truck") }
                }

                errorMessage?.let {
                    Text(
                        text = it, color = Color.Red, modifier = Modifier.padding(16.dp)
                    )
                }
                successMessage?.let {
                    Text(
                        text = it, color = Color.Green, modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun WeeklyBusinessHours(
    truckHours: MutableState<Map<String, MutableState<Pair<String, String>>>>,
    onUpdateHours: (String, String) -> Unit
) {
    val formattedHoursMap = remember { mutableStateMapOf<String, String>() }
    Column {
        listOf(
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        ).forEach { day ->
            val dayHours = truckHours.value[day]!!
            val openingHours = remember { mutableStateOf(dayHours.value.first) }
            val closingHours = remember { mutableStateOf(dayHours.value.second) }

            DayTimePicker(day, openingHours, closingHours) { formattedHours ->
                formattedHoursMap[day] = formattedHours
                onUpdateHours(day, formattedHours)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayTimePicker(
    day: String,
    openingHours: MutableState<String>,
    closingHours: MutableState<String>,
    onHoursFormatted: (String) -> Unit
) {
    var formattedHours by remember { mutableStateOf("") }
    Row(modifier = Modifier.fillMaxWidth()) {
        TimeInputField(day, "Opening", openingHours)
        TimeInputField(day, "Closing", closingHours)
    }


    LaunchedEffect(openingHours.value, closingHours.value) {
        if (openingHours.value.isNotEmpty() && closingHours.value.isNotEmpty()) {
            formattedHours = formatBusinessHours(openingHours.value, closingHours.value)
            onHoursFormatted(formattedHours)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeInputField(day: String, label: String, timeState: MutableState<String>) {
    val showDialog = remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()
    val isEnabled = remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .width(135.dp)
        .height(67.5.dp)
        .padding(2.dp)
        .clickable {
            isEnabled.value = true
            showDialog.value = true
        }) {
        OutlinedTextField(
            value = timeState.value,
            onValueChange = {},
            enabled = isEnabled.value,
            label = {
                Text(text = "$day $label", color = Color.Gray, fontSize = 14.sp, lineHeight = 12.sp)
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    if (showDialog.value) {
        TimePickerDialog(
            title = "Select $label Time", timePickerState = timePickerState, showDialog = showDialog
        ) { selectedTime ->
            timeState.value = selectedTime
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String,
    timePickerState: TimePickerState,
    showDialog: MutableState<Boolean>,
    onTimeSelected: (String) -> Unit
) {
    if (showDialog.value) {
        AlertDialog(onDismissRequest = {}, title = { Text(title) }, confirmButton = {
            Button(onClick = {
                showDialog.value = false
            }) {
                Text("Confirm")
            }
        }, dismissButton = {
            Button(onClick = { showDialog.value = false }) {
                Text("Cancel")
            }
        }, text = {
            TimePicker(state = timePickerState, layoutType = TimePickerLayoutType.Vertical)
            onTimeSelected(convertHours(timePickerState.hour, timePickerState.minute))
        })
    }
}

@Composable
fun convertHours(hour: Int, minute: Int): String {
    val hour12 = if (hour == 0 || hour == 12) 12 else hour % 12
    val amPm = if (hour < 12) "AM" else "PM"

    val formattedHour = hour12.toString().padStart(2, '0')
    val formattedMinute = minute.toString().padStart(2, '0')

    val finalString = "$formattedHour:$formattedMinute $amPm"

    return finalString
}

fun formatBusinessHours(openingTime: String, closingTime: String): String {
    return "$openingTime to $closingTime"
}