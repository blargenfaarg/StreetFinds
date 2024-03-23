package com.example.sprintone

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sprintone.ui.theme.SprintOneTheme


class MainActivity : ComponentActivity() {
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
                    Greeting("StreetFinds", modifier = Modifier.fillMaxHeight())
                    OnboardingScreen()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OnboardingScreen(modifier: Modifier = Modifier) {
    val mContext = LocalContext.current
    Surface(color = Color.Red,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)){
        Row (
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center) {
            Text(
                "StreetFinds",
                modifier = Modifier.fillMaxHeight(),
                fontSize = 42.sp,
                color = Color.Black,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.ExtraBold)
        }
        Row(
            modifier = modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        )  {

            Button(
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 2.dp),

                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                onClick =  {
                    mContext.startActivity(Intent(mContext, MapsActivity::class.java))
                },
            ) {

                Text("Map", color = Color.White, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = TextUnit(0.9F, TextUnitType.Sp))
            }
            Button(
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                onClick =  {
                    mContext.startActivity(Intent(mContext, VendorPage::class.java))
                },
            ) {

                Text("Vendors", color = Color.White, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = TextUnit(0.9F, TextUnitType.Sp))
            }

            Button(
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                onClick =  {
                    mContext.startActivity(Intent(mContext, UserPage::class.java))
                },

            ) {
                Text("Users", color = Color.White, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = TextUnit(0.9F, TextUnitType.Sp))
            }

        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    Surface ( color = Color.Red, modifier = Modifier.fillMaxSize()){
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
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
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, widthDp = 320, heightDp = 320, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    SprintOneTheme {
        OnboardingScreen()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SprintOneTheme {
        Greeting("StreetFinds", modifier = Modifier.fillMaxSize())
    }
}