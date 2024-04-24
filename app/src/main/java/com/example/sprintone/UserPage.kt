package com.example.sprintone

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class UserPage : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SprintOneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LoadUserProfile()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoadUserProfile() {

    val context = LocalContext.current
    val email = getUserEmail(context)
    val truckNames = remember { mutableStateListOf<String>() }

    LaunchedEffect(key1 = email) {
        getFirebaseData(email.toString(), truckNames)
    }
    Surface(modifier = Modifier.fillMaxSize())
    {
        Scaffold(bottomBar = { LoadNavBar() })
        { innerPadding ->
            Column(modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = "Hello $email",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
                OutlinedCard(modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp))
                {
                    Column(modifier=Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally)
                    {
                        Text("Your Favorite Vendors:", modifier=Modifier.padding(10.dp), fontWeight = FontWeight.ExtraBold)
                        truckNames.forEach { name ->
                            OutlinedCard(modifier = Modifier.fillMaxWidth().padding(5.dp), onClick = {
                                val intent = Intent(context, VendorProfilePage::class.java)
                                intent.putExtra("name", name)
                                context.startActivity(intent)})
                            {
                                Text(name, modifier = Modifier.padding(20.dp), fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(96.dp))
                Button(onClick = {
                    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()
                    context.startActivity(Intent(context, MainActivity::class.java))
                },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(200.dp, 50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray))
                {
                    Text("Sign out", fontSize = 24.sp)
                }
            }
        }
    }
}


suspend fun getFirebaseData(email: String, truckNames: MutableList<String>)
{
    val db = Firebase.firestore
    db.collection("users")
        .whereEqualTo("Email", email)
        .get()
        .await()  // Using await from kotlinx.coroutines.tasks.await extension
        .documents.firstOrNull()?.let { document ->
            val favorites = document.get("Favorite") as? List<String> ?: emptyList()
            val trucks = favorites.mapNotNull { favorite ->
                db.collection("trucks")
                    .whereEqualTo("Name", favorite)
                    .get()
                    .await()  // Await the result
                    .documents.firstOrNull()
                    ?.getString("Name")
            }
            truckNames.clear()  // Clear existing items
            truckNames.addAll(trucks)  // Add new items
        }
}
