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
    val email = getUserEmail(context)
    val db = Firebase.firestore
    val scrollState = rememberScrollState()
    
    var businessName by remember { mutableStateOf("")}
    var businessDescription by remember { mutableStateOf("")}
    var businessType by remember { mutableStateOf("")}
    var businessLocation by remember { mutableStateOf("")}
    var hasEnteredBusiness by remember { mutableStateOf(false)}
    var showDialog by remember { mutableStateOf(false)}

    var newBusinessName by remember { mutableStateOf("")}
    var newBusinessDescription by remember { mutableStateOf("")}
    var newBusinessLocation by remember { mutableStateOf("")}
    var newBusinessType by remember { mutableStateOf("")}

    var nameWasChanged by remember { mutableStateOf(false)}
    var descriptionWasChanged by remember { mutableStateOf(false)}
    var locationWasChanged by remember { mutableStateOf(false)}
    var typeWasChanged by remember { mutableStateOf(false)}


    Surface(modifier = Modifier.fillMaxSize())
    {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp).verticalScroll(scrollState)
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
                if (showDialog)
                {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            Button(onClick = {
                                showDialog = false

                                if (nameWasChanged)
                                {
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
                                    db.collection("vendors").whereEqualTo("Email", email)
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
                                if (descriptionWasChanged)
                                {
                                    db.collection("trucks").whereEqualTo("Description", businessDescription)
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

                                if(locationWasChanged)
                                {
                                    db.collection("trucks").whereEqualTo("Location", businessLocation)
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

                                if(typeWasChanged)
                                {
                                    db.collection("trucks").whereEqualTo("Type", businessType)
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
                                             },
                                enabled = newBusinessName.isNotBlank()
                                        || newBusinessDescription.isNotBlank()
                                        || newBusinessLocation.isNotBlank()
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



