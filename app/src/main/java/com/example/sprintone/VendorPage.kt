package com.example.sprintone

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import org.w3c.dom.Text

class VendorPage : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SprintOneTheme {
                VendorGreeting()
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
    Surface()
    {
        Scaffold(bottomBar = { LoadNavBar() }) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding),
            ){
                Text(
                    text = "~VENDOR PAGE IN DEVELOPMENT~",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 35.sp,
                    textAlign = TextAlign.Center
                )
                Button(onClick = {
                    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()
                    context.startActivity(Intent(context, MainActivity::class.java))
                                 },
                    modifier = Modifier.align(Alignment.CenterHorizontally) )
                {
                    Text("Sign out")
                }
            }
        }
    }
}

