package com.example.sprintone

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sprintone.databinding.ActivityVendorPage2Binding
import com.example.sprintone.ui.theme.SprintOneTheme

class VendorPage : AppCompatActivity() {

    //private lateinit var binding: ActivityVendorPage2Binding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SprintOneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Red
                ) {
                    VendorGreeting("StreetFinders")
                }
            }
        }
    }

    /*
    binding = ActivityUserPageBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val navView: BottomNavigationView = binding.navView

    val navController = findNavController(R.id.nav_host_fragment_activity_user_page)
    // Passing each menu ID as a set of Ids because each
    // menu should be considered as top level destinations.
    val appBarConfiguration = AppBarConfiguration(
        setOf(
            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
        )
    )
    setupActionBarWithNavController(navController, appBarConfiguration)
    navView.setupWithNavController(navController)

     */
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VendorGreeting(name: String, modifier: Modifier = Modifier.fillMaxSize()) {
    Surface(color = Color.Red, modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxHeight(),

                text = "Hello $name!",
                color = Color.Black,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 35.sp,
            )
        }


    }
}