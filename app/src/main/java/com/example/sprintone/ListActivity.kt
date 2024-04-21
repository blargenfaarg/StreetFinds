package com.example.sprintone

import android.content.ContentValues.TAG
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
                            modifier = Modifier.padding(innerPadding),
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


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DisplayList() {
    val scrollState = rememberScrollState()
    val truckListState = remember { mutableStateOf<List<Truck>>(emptyList()) }
    val selectedTruckType = remember { mutableStateOf<String?>(null) }
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    val day = dateFormat.format(calendar.time)
    val context = LocalContext.current

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
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(scrollState))
        {
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
            } // HEADER "StreetFinds" + Logo
            Divider(modifier = Modifier.padding(2.dp))
            LazyRow(horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth())
            {
                item {  FilterButton("American", selectedTruckType)}
                item {  FilterButton("Breakfast", selectedTruckType)}
                item {  FilterButton("Mexican", selectedTruckType)}
                item {  FilterButton("Italian", selectedTruckType)}
                item {  FilterButton("Asian", selectedTruckType)}
                item {  FilterButton("Fusion", selectedTruckType)}
                item {  FilterButton("Seafood", selectedTruckType)}
            }
            truckListState.value.filter { truck ->
                selectedTruckType.value == null || truck.type == selectedTruckType.value
            }.forEach { truck ->
                val randomNumber = (50..360).random()
                Card(modifier = Modifier
                    .padding(top = 5.dp, start = 10.dp, end = 10.dp, bottom = 5.dp)
                    .align(Alignment.CenterHorizontally),
                    onClick = {
                        val intent = Intent(context, VendorProfilePage::class.java)
                        intent.putExtra("name", truck.name)
                        intent.putExtra("description", truck.description)
                        intent.putExtra("type", truck.type)
                        intent.putExtra("location", truck.location)
                        intent.putExtra("colorVal", randomNumber)
                        context.startActivity(intent)
                    })
                {
                    Row(modifier = Modifier.fillMaxWidth()){
                        OutlinedCard(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 8.dp),
                            colors = CardDefaults
                                .outlinedCardColors(Color
                                    .hsl(randomNumber
                                        .toFloat(), 0.5f, 0.92f)))
                        {
                            Icon(
                                painter = painterResource(R.drawable.icon_foodtruck),
                                contentDescription = "A food truck",
                                tint = Color.Black, modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                                    .padding(10.dp))
                        }
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(modifier = Modifier, text = truck.name,
                                textAlign = TextAlign.Left, color = Color.Black,
                                fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                            Text(text = truck.type, textAlign = TextAlign.Left)
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
    Card(modifier = Modifier.fillMaxWidth())
    {
        Row(modifier = Modifier.padding(start=4.dp))
        {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    modifier = modifier,
                    text = "$day Hours: $hours",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterButton(type: String, selectedTruckType: MutableState<String?>)
{
    val selected = selectedTruckType.value == type

    ElevatedFilterChip(
        onClick = {
            if (selectedTruckType.value == type) {
                selectedTruckType.value = null // Deselect if this type is already selected
            } else {
                selectedTruckType.value = type // Select this type
            }
                  },
        leadingIcon = { if(selected) {Icon(imageVector = Icons.Filled.Done, contentDescription = null,
            modifier = Modifier.size(FilterChipDefaults.IconSize))} },
        modifier = Modifier.padding(start = 4.dp, end = 4.dp),
        colors = FilterChipDefaults.elevatedFilterChipColors(),
        label ={ Text(type, color = Color.Black) } ,
        selected = selected)

}



