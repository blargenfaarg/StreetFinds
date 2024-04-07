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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

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
    val type: String
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

   LaunchedEffect(key1 = Unit) {
        val db = Firebase.firestore
        try {
            val querySnapshot = db.collection("trucks").get().await()
            val trucks = querySnapshot.documents.mapNotNull { document ->
                val truckName = document.getString("Name")
                val location = document.getString("Location")
                val description = document.getString("Description")
                val type = document.getString("Type")

                if (truckName != null && location != null && description != null && type != null) {
                    Truck(truckName, location, description, type)
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
            modifier = Modifier
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "StreetFinds",
                modifier = Modifier.padding(top = 4.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
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
                                Text(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    text = "Hours: 11:00 AM - 3:00 PM"
                                )
                            }
                        }
                    }
                }
                Divider(modifier = Modifier.width(300.dp))
            }
        }
    }
}



