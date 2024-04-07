package com.example.sprintone

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    var businessName by remember { mutableStateOf("")}
    var businessDescription by remember { mutableStateOf("")}
    var hasEnteredBusiness by remember { mutableStateOf(false)}

    val db = Firebase.firestore


    Surface(modifier = Modifier.fillMaxSize())
    {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ){
            Text(
                text = "Welcome $email",
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 35.sp,
                textAlign = TextAlign.Center,
            )

            db.collection("vendors").whereEqualTo("Email", email)
                .get()
                .addOnSuccessListener { documents ->
                for (document in documents){
                    businessName = document.getString("Business Name").toString()
                    Log.e("Found", "Business Name: ${businessName}")
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
                                    //TODO add rest of business info
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
                Text(
                    text = "Your Business Name: $businessName",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Your Business Description: $businessDescription",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.padding(130.dp))

            Text(text = "To list your business, please fill out the vendor form.",
                fontSize = 20.sp)

            Spacer(modifier = Modifier.padding(20.dp))

            Button(
                onClick = {context.startActivity(Intent(context, VendorForm::class.java))},
            )
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
                context.startActivity(Intent(context, MainActivity::class.java))
            },
                modifier = Modifier.align(Alignment.End) )
            {
                Text("Sign out")
            }
        }
    }
}



