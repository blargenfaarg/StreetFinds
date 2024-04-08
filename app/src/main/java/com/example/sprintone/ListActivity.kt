package com.example.sprintone

import android.content.ContentValues.TAG
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ListActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SprintOneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Scaffold(
                        bottomBar = { LoadNavBar() },
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .padding(innerPadding),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        )
                        {
                            DisplayList()
                        }
                    }
                }
            }
        }
    }
}

data class Truck(
    val name: String,
    val location: String,
    val description: String,
    val type: String,
    val mondayHours : String,
    val tuesdayHours : String,
    val wednesdayHours : String,
    val thursdayHours : String,
    val fridayHours : String,
    val saturdayHours : String,
    val sundayHours : String,

)

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewDisplayList()
{
    DisplayList()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DisplayList() {
    val scrollState = rememberScrollState()
    val truckListState = remember { mutableStateOf<List<Truck>>(emptyList()) }
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    val day = dateFormat.format(calendar.time)

   LaunchedEffect(key1 = Unit) {
        val db = Firebase.firestore
        try {
            val querySnapshot = db.collection("trucks").get().await()
            val trucks = querySnapshot.documents.mapNotNull { document ->
                val truckName = document.getString("Name")
                val location = document.getString("Location")
                val description = document.getString("Description")
                val type = document.getString("Type")
                val truckMondayHours = document.getString("Monday Hours")?.takeIf { it.isNotBlank() } ?: "Closed"
                val truckTuesdayHours = document.getString("Tuesday Hours")?.takeIf { it.isNotBlank() } ?: "Closed"
                val truckWednesdayHours = document.getString("Wednesday Hours")?.takeIf { it.isNotBlank() } ?: "Closed"
                val truckThursdayHours = document.getString("Thursday Hours")?.takeIf { it.isNotBlank() } ?: "Closed"
                val truckFridayHours = document.getString("Friday Hours")?.takeIf { it.isNotBlank() } ?: "Closed"
                val truckSaturdayHours = document.getString("Saturday Hours")?.takeIf { it.isNotBlank() } ?: "Closed"
                val truckSundayHours = document.getString("Sunday Hours")?.takeIf { it.isNotBlank() } ?: "Closed"


                if (truckName != null && location != null && description != null && type != null) {
                    Truck(truckName, location, description, type, truckMondayHours,
                        truckTuesdayHours,
                        truckWednesdayHours,
                        truckThursdayHours,
                        truckFridayHours,
                        truckSaturdayHours,
                        truckSundayHours)
                } else {
                    null
                }
            }
            truckListState.value = trucks
        } catch (e: Exception) {
            Log.e(TAG, "Error getting documents: ", e)
        }
    } // 8:00AM - 5:00PM

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(scrollState)
        ) {
            Row {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.truckpin),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.height(50.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "StreetFinds",
                    modifier = Modifier.padding(top = 4.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
            }
            Divider(modifier = Modifier.padding(2.dp))

            truckListState.value.forEach { truck ->
                val randomNumber = (50..360).random()

                Card(modifier = Modifier
                    .padding(top = 5.dp, start = 10.dp, end = 10.dp, bottom = 5.dp)
                    .align(Alignment.CenterHorizontally))
                {
                    Row(modifier = Modifier.fillMaxWidth()){
                        OutlinedCard(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 8.dp),
                            colors = CardDefaults.outlinedCardColors(Color.hsl(randomNumber.toFloat(), 0.5f, 0.92f)),
                        ) {
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
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                modifier = Modifier,
                                text = truck.name,
                                textAlign = TextAlign.Left,
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                            )
                            Text(
                                text = truck.type,
                                textAlign = TextAlign.Left,
                                modifier = Modifier,
                            )
                            Row {
                                Icon(
                                    imageVector = Icons.Filled.Place,
                                    contentDescription = "A pin",
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = truck.location,
                                    modifier = Modifier
                                )
                            }
                            Row {
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = "A clock",
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                                Spacer(modifier = Modifier.width(4.dp))

                                when(day){
                                    "Monday" -> GenerateDayText("Monday", truck.mondayHours, modifier = Modifier.align(Alignment.CenterVertically))
                                    "Tuesday" -> GenerateDayText("Tuesday", truck.tuesdayHours, modifier = Modifier.align(Alignment.CenterVertically))
                                    "Wednesday" -> GenerateDayText("Wednesday", truck.wednesdayHours, modifier = Modifier.align(Alignment.CenterVertically))
                                    "Thursday" -> GenerateDayText("Thursday", truck.thursdayHours, modifier = Modifier.align(Alignment.CenterVertically))
                                    "Friday" -> GenerateDayText("Friday", truck.fridayHours, modifier = Modifier.align(Alignment.CenterVertically))
                                    "Saturday" -> GenerateDayText("Saturday", truck.saturdayHours, modifier = Modifier.align(Alignment.CenterVertically))
                                    "Sunday" -> GenerateDayText("Sunday", truck.sundayHours, modifier = Modifier.align(Alignment.CenterVertically))
                                }
                            }
                        }
                    }
                }
                Divider(modifier = Modifier.width(300.dp))
            }
        }
    }
}

@Composable
fun GenerateDayText(day: String, hours: String, modifier: Modifier)
{
    return Text(
        modifier = modifier,
        text = "$day Hours: $hours",
        fontSize = 12.sp
    )
}


