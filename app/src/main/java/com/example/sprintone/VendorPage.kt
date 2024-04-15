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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

//TODO: Fix it up and make it look nicer
class VendorPage : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SprintOneTheme {
                Scaffold(
                    bottomBar = {
                        LoadNavBar()},
                    ) {
                    innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding))
                        {
                            VendorGreeting()
                        }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview
fun VendorGreeting()
{

    val context = LocalContext.current
    val db = Firebase.firestore
    val email = getUserEmail(context)
    Log.e("Error", "Email is: $email")
    val scrollState = rememberScrollState()

    var businessName by remember { mutableStateOf("")}
    var businessDescription by remember { mutableStateOf("")}
    var businessType by remember { mutableStateOf("")}
    var businessLocation by remember { mutableStateOf("")}
    var hasEnteredBusiness by remember { mutableStateOf(false)}
    var showDialog by remember { mutableStateOf(false)}
    var showHoursDialog by remember { mutableStateOf(false)}

    var newBusinessName by remember { mutableStateOf("")}
    var newBusinessDescription by remember { mutableStateOf("")}
    var newBusinessLocation by remember { mutableStateOf("")}
    var newBusinessType by remember { mutableStateOf("")}

    var nameWasChanged by remember { mutableStateOf(false)}
    var descriptionWasChanged by remember { mutableStateOf(false)}
    var locationWasChanged by remember { mutableStateOf(false)}
    var typeWasChanged by remember { mutableStateOf(false)}

    var mondayHoursWereChanged by remember { mutableStateOf(false)}
    var tuesdayHoursWereChanged by remember { mutableStateOf(false)}
    var wednesdayHoursWereChanged by remember { mutableStateOf(false)}
    var thursdayHoursWereChanged by remember { mutableStateOf(false)}
    var fridayHoursWereChanged by remember { mutableStateOf(false)}
    var saturdayHoursWereChanged by remember { mutableStateOf(false)}
    var sundayHoursWereChanged by remember { mutableStateOf(false)}
    var wereHoursUpdated by remember{ mutableStateOf(false)}


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


    Surface(modifier = Modifier.fillMaxSize())
    {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(scrollState)
        ){
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
                for (document in documents){
                    businessName = document.getString("Business Name").toString()
                }
                    db.collection("trucks").whereEqualTo("Name", businessName)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.isEmpty)
                            {
                                hasEnteredBusiness = false
                            } else
                            {
                                for (document in querySnapshot) {
                                    hasEnteredBusiness = true
                                    businessDescription = document.getString("Description").toString()
                                    businessLocation = document.getString("Location").toString()
                                    businessType = document.getString("Type").toString()
                                    truckMondayHours = document.getString("Monday Hours").toString()
                                    truckTuesdayHours = document.getString("Tuesday Hours").toString()
                                    truckWednesdayHours = document.getString("Wednesday Hours").toString()
                                    truckThursdayHours = document.getString("Thursday Hours").toString()
                                    truckFridayHours = document.getString("Friday Hours").toString()
                                    truckSaturdayHours = document.getString("Saturday Hours").toString()
                                    truckSundayHours = document.getString("Sunday Hours").toString()
                                }
                            }
                        }
                        .addOnFailureListener{ exception ->
                            Log.e("Error", "Another error happened.")
                        }
                }
                .addOnFailureListener{ exception ->
                    Log.e("Error","Couldn't find a matching document")
                }
            if (hasEnteredBusiness)
            {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, color = Color.Black))
                                {
                                    append("Your Business Name: \n")
                                }
                                append(businessName)
                            }, modifier = Modifier.padding(8.dp)
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, color = Color.Black))
                                {
                                    append("Your Business Type: \n")
                                }
                                append(businessType)
                            }, modifier = Modifier.padding(8.dp)
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, color = Color.Black))
                                {
                                    append("Your Business Description: \n")
                                }
                                append(businessDescription)
                            }, modifier = Modifier.padding(8.dp)
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, color = Color.Black))
                                {
                                    append("Your Business Location: \n")
                                }
                                append(businessLocation)
                            }, modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Button(onClick = { showDialog = true })
                {
                    Text("Update Vendor Info")
                }

                PickImageFromGallery()

                if (showDialog)
                {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            Button(onClick = {
                                showDialog = false
                                if (nameWasChanged) {
                                    db.collection("trucks").whereEqualTo("Name", businessName)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            if (!documents.isEmpty) {
                                                val vendorDoc = documents.documents.first()
                                                val vendorId = vendorDoc.id
                                                db.collection("trucks").document(vendorId).update("Name", newBusinessName)

                                                businessName = newBusinessName
                                            }
                                        }
                                    db.collection("trucks").whereEqualTo("Name", businessName)
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
                                                db.collection("trucks").document(vendorId).update("Description", newBusinessDescription)

                                                businessDescription = newBusinessDescription
                                            }
                                        }
                                }
                                if(locationWasChanged) {
                                    db.collection("trucks").whereEqualTo("Name", businessName)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            if (!documents.isEmpty) {
                                                val vendorDoc = documents.documents.first()
                                                val vendorId = vendorDoc.id
                                                db.collection("trucks").document(vendorId).update("Location", newBusinessLocation)

                                                businessLocation = newBusinessLocation
                                            }
                                        }
                                }
                                if(typeWasChanged) {
                                    db.collection("trucks").whereEqualTo("Name", businessName)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            if (!documents.isEmpty) {
                                                val vendorDoc = documents.documents.first()
                                                val vendorId = vendorDoc.id
                                                db.collection("trucks").document(vendorId).update("Type", newBusinessType)

                                                businessType = newBusinessType
                                            }
                                        }
                                }
                                if(mondayHoursWereChanged) {
                                    db.collection("trucks").whereEqualTo("Name", businessName)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            if (!documents.isEmpty) {
                                                val vendorDoc = documents.documents.first()
                                                val vendorId = vendorDoc.id
                                                db.collection("trucks").document(vendorId).update("Monday Hours", dialogMondayHours)

                                                truckMondayHours = dialogMondayHours
                                            }
                                        }
                                }
                                if (tuesdayHoursWereChanged) {
                                    db.collection("trucks").whereEqualTo("Name", businessName)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            if (!documents.isEmpty) {
                                                val vendorDoc = documents.documents.first()
                                                val vendorId = vendorDoc.id
                                                db.collection("trucks").document(vendorId).update("Tuesday Hours", dialogTuesdayHours)

                                                truckTuesdayHours = dialogTuesdayHours
                                            }
                                        }
                                }
                                if (wednesdayHoursWereChanged) {
                                    db.collection("trucks").whereEqualTo("Name", businessName)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            if (!documents.isEmpty) {
                                                val vendorDoc = documents.documents.first()
                                                val vendorId = vendorDoc.id
                                                db.collection("trucks").document(vendorId).update("Wednesday Hours", dialogWednesdayHours)

                                                truckWednesdayHours = dialogWednesdayHours
                                            }
                                        }
                                }
                                if (thursdayHoursWereChanged) {
                                    db.collection("trucks").whereEqualTo("Name", businessName)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            if (!documents.isEmpty) {
                                                val vendorDoc = documents.documents.first()
                                                val vendorId = vendorDoc.id
                                                db.collection("trucks").document(vendorId).update("Thursday Hours", dialogThursdayHours)

                                                truckThursdayHours = dialogThursdayHours
                                            }
                                        }
                                }
                                if (fridayHoursWereChanged) {
                                    db.collection("trucks").whereEqualTo("Name", businessName)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            if (!documents.isEmpty) {
                                                val vendorDoc = documents.documents.first()
                                                val vendorId = vendorDoc.id
                                                db.collection("trucks").document(vendorId).update("Friday Hours", dialogFridayHours)

                                                truckFridayHours = dialogFridayHours
                                            }
                                        }
                                }
                                if (saturdayHoursWereChanged) {
                                    db.collection("trucks").whereEqualTo("Name", businessName)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            if (!documents.isEmpty) {
                                                val vendorDoc = documents.documents.first()
                                                val vendorId = vendorDoc.id
                                                db.collection("trucks").document(vendorId).update("Saturday Hours", dialogSaturdayHours)

                                                truckSaturdayHours = dialogSaturdayHours
                                            }
                                        }
                                }
                                if (sundayHoursWereChanged) {
                                    db.collection("trucks").whereEqualTo("Name", businessName)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            if (!documents.isEmpty) {
                                                val vendorDoc = documents.documents.first()
                                                val vendorId = vendorDoc.id
                                                db.collection("trucks").document(vendorId).update("Sunday Hours", dialogSundayHours)

                                                truckSundayHours = dialogSundayHours
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
                            }){
                                Text("Cancel")
                            }
                        },
                        title = {Text("Update information")},
                        text = {
                            Column{
                                OutlinedTextField(
                                    value = newBusinessName,
                                    onValueChange = {
                                        newBusinessName = it
                                        if (newBusinessName == businessName)
                                        {
                                            nameWasChanged = false
                                        }
                                        else{
                                            nameWasChanged = true
                                        }
                                                    },
                                    label = { Text("New Name") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                )
                                OutlinedTextField(
                                    value = newBusinessType,
                                    onValueChange = { newBusinessType = it
                                        if (newBusinessType == businessType)
                                        {

                                        }
                                        else {
                                            typeWasChanged = true
                                        }
                                    },
                                    label = { Text("New Type") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                )
                                OutlinedTextField(
                                    value = newBusinessDescription,
                                    onValueChange = {
                                        newBusinessDescription = it
                                        if (newBusinessDescription == businessDescription)
                                        {

                                        }
                                        else{
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
                                    onValueChange = { newBusinessLocation = it
                                        if (newBusinessLocation == businessLocation)
                                        {

                                        }
                                        else {
                                            locationWasChanged = true
                                        }
                                                    },
                                    label = { Text("New Location") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                )
                                Button(onClick = {
                                    showHoursDialog = true
                                })
                                {
                                    Text("Update Hours")
                                }
                                if (showHoursDialog)
                                {
                                    AlertDialog(
                                        onDismissRequest = { showDialog = false },
                                        confirmButton = {
                                            Button(onClick = {
                                                showHoursDialog = false
                                                wereHoursUpdated = true
                                            }, enabled = dialogMondayHours.isNotBlank()
                                                    || dialogTuesdayHours.isNotBlank()
                                                    || dialogWednesdayHours.isNotBlank()
                                                    || dialogThursdayHours.isNotBlank()
                                                    || dialogFridayHours.isNotBlank()
                                                    || dialogSaturdayHours.isNotBlank()
                                                    || dialogSundayHours.isNotBlank())
                                            {
                                                Text("Save")
                                            } },
                                        dismissButton = { Button(onClick = { showHoursDialog = false })
                                        {
                                            Text("Cancel")
                                        } },
                                        title = {Text("Enter New Hours")},
                                        text = {
                                            Column{
                                                OutlinedTextField(
                                                    value = dialogMondayHours,
                                                    onValueChange = {
                                                        dialogMondayHours = it
                                                        if (dialogMondayHours == truckMondayHours)
                                                        {
                                                            mondayHoursWereChanged = false
                                                        }
                                                        else{
                                                            mondayHoursWereChanged = true
                                                        }
                                                    },
                                                    label = { Text("Monday") },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(4.dp)
                                                )
                                                OutlinedTextField(
                                                    value = dialogTuesdayHours,
                                                    onValueChange = {  dialogTuesdayHours = it
                                                        if (dialogTuesdayHours == truckTuesdayHours)
                                                        {
                                                            tuesdayHoursWereChanged = false
                                                        }
                                                        else{
                                                            tuesdayHoursWereChanged = true
                                                        }},
                                                    label = { Text("Tuesday") },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(4.dp)
                                                )
                                                OutlinedTextField(
                                                    value = dialogWednesdayHours,
                                                    onValueChange = {  dialogWednesdayHours = it
                                                        if (dialogWednesdayHours == truckWednesdayHours)
                                                        {
                                                            wednesdayHoursWereChanged = false
                                                        }
                                                        else{
                                                            wednesdayHoursWereChanged = true
                                                        } },
                                                    label = { Text("Wednesday") },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(4.dp)
                                                )
                                                OutlinedTextField(
                                                    value = dialogThursdayHours,
                                                    onValueChange = {  dialogThursdayHours = it
                                                        if (dialogThursdayHours == truckThursdayHours)
                                                        {
                                                            thursdayHoursWereChanged = false
                                                        }
                                                        else{
                                                            thursdayHoursWereChanged = true
                                                        } },
                                                    label = { Text("Thursday") },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(4.dp)
                                                )
                                                OutlinedTextField(
                                                    value = dialogFridayHours,
                                                    onValueChange = {  dialogFridayHours = it
                                                        if (dialogFridayHours == truckFridayHours)
                                                        {
                                                            fridayHoursWereChanged = false
                                                        }
                                                        else{
                                                            fridayHoursWereChanged = true
                                                        } },
                                                    label = { Text("Friday") },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(4.dp)
                                                )
                                                OutlinedTextField(
                                                    value = dialogSaturdayHours,
                                                    onValueChange = {  dialogSaturdayHours = it
                                                        if (dialogSaturdayHours == truckSaturdayHours)
                                                        {
                                                            saturdayHoursWereChanged = false
                                                        }
                                                        else{
                                                            saturdayHoursWereChanged = true
                                                        } },
                                                    label = { Text("Saturday") },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(4.dp)
                                                )
                                                OutlinedTextField(
                                                    value = dialogSundayHours,
                                                    onValueChange = {  dialogSundayHours = it
                                                        if (dialogSundayHours == truckSundayHours)
                                                        {
                                                            sundayHoursWereChanged = false
                                                        }
                                                        else{
                                                            sundayHoursWereChanged = true
                                                        } },
                                                    label = { Text("Sunday") },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(4.dp)
                                                )
                                            } }
                                    )
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.padding(30.dp))

            Text(text = "To list your business, please fill out the vendor form.", fontSize = 20.sp)

            Spacer(modifier = Modifier.padding(10.dp))

            Button(onClick = {context.startActivity(Intent(context, VendorForm::class.java))})
            {
                Text(
                    text = "Vendor Form",
                    fontSize = 30.sp
                )
            }

            Spacer(modifier = Modifier.padding(20.dp))

            Button(onClick = {
                val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()
                context.startActivity(Intent(context, MainActivity::class.java)) },
                modifier = Modifier.align(Alignment.End))
            {
                Text("Sign out")
            }
        }
    }
}



