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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview
fun LoadUserProfile() {

    val context = LocalContext.current
    val email = getUserEmail(context)

    Surface(modifier = Modifier.fillMaxSize())
    {
        Scaffold(bottomBar = { LoadNavBar() })
        { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = "Hello $email",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "There's not much here yet..."
                )
                Spacer(modifier = Modifier.height(96.dp))
                Button(onClick = {
                    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()
                    context.startActivity(Intent(context, MainActivity::class.java))
                },
                    modifier = Modifier.align(Alignment.CenterHorizontally).size(200.dp, 50.dp) )
                {
                    Text("Sign out", fontSize = 24.sp)
                }
            }
        }
    }
}
