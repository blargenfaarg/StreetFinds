package com.example.sprintone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class VendorProfilePage : ComponentActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
        SprintOneTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                Scaffold(bottomBar = { LoadNavBar()})
                { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding),
                        verticalArrangement = Arrangement.spacedBy(16.dp))
                    {
                        val extras = intent.extras
                        val db = Firebase.firestore

                        // These vals are passed in when we call from the List
                        val name = extras?.getString("name")
                        val description = extras?.getString("description")
                        val type = extras?.getString("type")
                        val location = extras?.getString("location")
                        var randomNumber = extras?.getInt("colorVal")


                        // these variables are for when we call from the Map Screen
                        var tempDescription by remember { mutableStateOf("")}
                        var tempType by remember { mutableStateOf("")}
                        var tempLocation by remember { mutableStateOf("")}

                        if (description == null && type == null && location == null && randomNumber == 0)
                        { // This is for the case that this activity was called from the Map Screen
                            randomNumber = (50..360).random()
                            LaunchedEffect(key1 = Unit) {
                                val querySnapshot = db.collection("trucks")
                                    .whereEqualTo("Name", name)
                                    .get()
                                    .await()

                                if (!querySnapshot.isEmpty)
                                {
                                    val document = querySnapshot.documents.first()
                                    tempDescription = document.getString("Description").toString()
                                    tempType = document.getString("Type").toString()
                                    tempLocation = document.getString("Location").toString()

                                }
                            }

                            LoadVendorProfilePage(
                                name = name.toString(),
                                description = tempDescription,
                                type = tempType,
                                location = tempLocation,
                                randomNumber = randomNumber)
                        }
                        else {  // This is for the case that this activity was called from the List Screen
                            if (randomNumber != null) {
                                LoadVendorProfilePage(name.toString(),
                                    description.toString(), type.toString(), location.toString(), randomNumber)
                            }
                        }
                    }
                }
            }
        }
    }
}
}


data class TruckHours(
val mondayHours : String,
val tuesdayHours : String,
val wednesdayHours : String,
val thursdayHours : String,
val fridayHours : String,
val saturdayHours : String,
val sundayHours : String,
val phoneNumber : String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadVendorProfilePage(name:String, description:String, type:String, location:String, randomNumber:Int) {

val calendar = Calendar.getInstance()
val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
val day = dateFormat.format(calendar.time)
val truckListState = remember { mutableStateOf<List<TruckHours>>(emptyList()) }
val context = LocalContext.current
val userId = remember{ mutableStateOf<String?>(null)}
val email = getUserEmail(context).toString()
val isFavorite = remember { mutableStateOf(false)}
val db = Firebase.firestore
val userType = getUserType(context)
var imageUrls by remember { mutableStateOf<List<String>?>(null) }



    LaunchedEffect(Unit) {
        db.collection("vendors").whereEqualTo("Business Name", name)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val vendorDoc = documents.documents.first()
                    imageUrls = vendorDoc.get("imageUrl") as? List<String>
                    Log.e("imageUrls", "ImageUrls: $imageUrls")
                }
            }
    }



LaunchedEffect(key1 = Unit)
{
    val querySnapshot = db.collection("trucks").whereEqualTo("Name", name).get().await()
    val trucks = querySnapshot.documents.mapNotNull { document ->
            val truckMondayHours = document.getString("Monday Hours")?.takeIf { it.isNotBlank() } ?: "Closed"
            val truckTuesdayHours = document.getString("Tuesday Hours")?.takeIf { it.isNotBlank() } ?: "Closed"
            val truckWednesdayHours = document.getString("Wednesday Hours")?.takeIf { it.isNotBlank() } ?: "Closed"
            val truckThursdayHours = document.getString("Thursday Hours")?.takeIf { it.isNotBlank() } ?: "Closed"
            val truckFridayHours = document.getString("Friday Hours")?.takeIf { it.isNotBlank() } ?: "Closed"
            val truckSaturdayHours = document.getString("Saturday Hours")?.takeIf { it.isNotBlank() } ?: "Closed"
            val truckSundayHours = document.getString("Sunday Hours")?.takeIf { it.isNotBlank() } ?: "Closed"
            val phoneNumber = document.getString("Phone").toString()

            TruckHours(truckMondayHours,
                truckTuesdayHours,
                truckWednesdayHours,
                truckThursdayHours,
                truckFridayHours,
                truckSaturdayHours,
                truckSundayHours, phoneNumber)
        }
        truckListState.value = trucks
    if (userType == "buyer")
    {
        db.collection("users")
            .whereEqualTo("Email", email)
            .get()
            .addOnSuccessListener { querySnapshot2 ->
                if (!querySnapshot2.isEmpty) {
                    val document = querySnapshot2.documents.first()
                    userId.value = document.id
                    val favorites = document.get("Favorite") as? List<String> ?: emptyList()
                    isFavorite.value = name in favorites
                    saveUserFavorites(context, favorites)
                }
            }
    }
}

fun handleFavoriteClick() {
    userId.value?.let { uid ->
        val userRef = db.collection("users").document(uid)

        if (isFavorite.value) {
            userRef.update("Favorite", FieldValue.arrayRemove(name)).addOnCompleteListener {
                if (it.isSuccessful) {
                    isFavorite.value = false
                }
            }
        } else {
            userRef.update("Favorite", FieldValue.arrayUnion(name)).addOnCompleteListener {
                if (it.isSuccessful) {
                    isFavorite.value = true
                }
            }
        }
    }
}

Surface(color = Color.White, modifier = Modifier.fillMaxSize())
{
    Column()
    {
        Card(modifier = Modifier.fillMaxSize(), colors = CardDefaults.cardColors(Color.LightGray))
        {
            Card(modifier = Modifier.fillMaxWidth().height(125.dp))
                {
                Column(modifier = Modifier.fillMaxSize().padding(10.dp),
                    verticalArrangement = Arrangement.Bottom)
                {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom)
                    {
                        OutlinedCard(
                            modifier = Modifier
                                .width(100.dp)
                                .height(100.dp),
                            colors = CardDefaults.outlinedCardColors(Color.hsl(randomNumber.toFloat(), 0.5f, 0.92f)))
                        {
                            Icon(
                                painter = painterResource(R.drawable.icon_foodtruck),
                                contentDescription = "A food truck",
                                tint = Color.Black,
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                                    .padding(10.dp)
                            )
                        }
                        Column {
                            Text(text = name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black,
                                modifier = Modifier.padding(start = 10.dp))
                                Text(text = type, fontSize = 20.sp, modifier = Modifier.padding(start = 10.dp))
                        }
                        Column(modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End)
                        {
                            if (userType == "buyer")
                            {
                                val buttonColor = if (isFavorite.value) Color.hsl(50f, 0.81f, 0.60f) else Color.Gray
                                OutlinedCard {
                                    IconButton(onClick = {
                                        handleFavoriteClick()
                                    })
                                    {
                                        Icon(Icons.Outlined.Star, contentDescription = "Favorite Button", tint = buttonColor)
                                    }
                                }
                            }
                        }

                    }
                }
            }
            Divider(modifier=Modifier.padding(8.dp))
            Card(modifier = Modifier.fillMaxWidth())
            {
                Row(modifier=Modifier.fillMaxWidth())
                {
                    Icon(imageVector = Icons.Filled.Info, contentDescription = "Info", modifier= Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp))
                    Text(description, modifier = Modifier.padding(12.dp))
                }

            }
            Spacer(modifier = Modifier.padding(2.dp))
            Card(modifier = Modifier.fillMaxWidth(), onClick = {
                val intent = Intent(context, MapsComposeActivity::class.java)
                intent.putExtra("query", name)
                context.startActivity(intent)
            })
            {
                Row {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "A location Icon",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp)
                    )
                    Text(location, modifier = Modifier.padding(12.dp))
                }
            }
            Spacer(modifier = Modifier.padding(2.dp))
            Card(modifier = Modifier.fillMaxWidth(), onClick = {
                val intent = Intent(Intent.ACTION_DIAL).apply{
                    truckListState.value.forEach() {truck ->
                        data = Uri.parse("tel:${truck.phoneNumber}")}
                }
                context.startActivity(intent)

            })
            {
                Row {
                    Icon(
                        imageVector = Icons.Filled.Phone,
                        contentDescription = "A phone Icon",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp)
                    )
                    truckListState.value.forEach() { truck ->
                        Text(
                            text = "Phone Number: ${truck.phoneNumber}",
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(2.dp))

            Card(modifier = Modifier.fillMaxWidth())
            {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                    horizontalArrangement = Arrangement.Center)
                {

                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "A clock",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    truckListState.value.forEach() { truck ->
                        when(day)
                        {
                            "Monday" -> GenerateDayText("Monday", truck.mondayHours)
                            "Tuesday" -> GenerateDayText("Tuesday", truck.tuesdayHours)
                            "Wednesday" -> GenerateDayText("Wednesday", truck.wednesdayHours)
                            "Thursday" -> GenerateDayText("Thursday", truck.thursdayHours)
                            "Friday" -> GenerateDayText("Friday", truck.fridayHours)
                            "Saturday" -> GenerateDayText("Saturday", truck.saturdayHours)
                            "Sunday" -> GenerateDayText("Sunday", truck.sundayHours)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(2.dp))

            Card(modifier = Modifier.fillMaxWidth()) {

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    , horizontalArrangement = Arrangement.Center)
                {
                    Text(text = "Images", fontWeight = FontWeight.Medium, color = Color.Black)
                }

                if (imageUrls.isNullOrEmpty())
                {
                    Row(modifier= Modifier
                        .fillMaxWidth()
                        .padding(4.dp), horizontalArrangement = Arrangement.Center)
                    {
                        Text("No images yet")
                    }
                }
                else
                {
                    Box(modifier = Modifier.fillMaxSize())
                    {
                        Log.e("EEEEEE", "imageUrls: $imageUrls")
                        LoadImageFromUrls(imageUrls = imageUrls!!)
                    }
                }
           }
        }
    }
  }
}






