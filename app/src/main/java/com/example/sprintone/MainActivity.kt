package com.example.sprintone

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class MainActivity : ComponentActivity() {
    val db = Firebase.firestore

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SprintOneTheme {
                LoadGreeting()
            // LoadMainScreen()
            }
        }
    }
}

@Preview
@Composable
fun LoadGreeting()
{
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "StreetFinds",
                textAlign = TextAlign.Center,
                fontSize = 62.sp,
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(
                modifier = Modifier.padding(10.dp)
            )
            Row {
                Button(
                    onClick = { context.startActivity(Intent(context, LoginActivity::class.java)) },
                    modifier = Modifier.width(130.dp)
                )
                {
                    Text(
                        text = "Users",
                        fontSize = 20.sp,
                        color = Color.Black
                    )

                }
                Spacer(
                    modifier = Modifier.padding(16.dp)
                )
                Button(
                    onClick = { context.startActivity(Intent(context, VendorLoginActivity::class.java)) },
                    modifier = Modifier.width(130.dp)
                )
                {
                    Text(
                        text = "Vendors",
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(20.dp)
            )
            OutlinedButton(
                onClick = { context.startActivity(Intent(context, ListActivity::class.java)) },
                colors = ButtonDefaults.buttonColors(Color.LightGray)
            )
            {
                Text(
                    text = "Continue as a guest",
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }

        }
    }

}


/*
@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun LoadMainScreen(modifier: Modifier = Modifier) {
    val mContext = LocalContext.current

    Surface(color = Color.Red,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoadSearchBar()
            Text(
                "StreetFinds",
                modifier = Modifier.fillMaxHeight(),
                textAlign = TextAlign.Center,
                fontSize = 62.sp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }

        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        )  {
            Button(
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                onClick =  {
                    mContext.startActivity(Intent(mContext, MapsComposeActivity::class.java))
                },
            )
            {
                Text("Map", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = TextUnit(0.9F, TextUnitType.Sp))
            }
            Button(
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                onClick =  {
                    mContext.startActivity(Intent(mContext, ListActivity::class.java))
                },
            ) {

                Text("List", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = TextUnit(0.9F, TextUnitType.Sp))
            }
            Button(
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                onClick =  {
                    mContext.startActivity(Intent(mContext, VendorPage::class.java))
                },
            ) {

                Text("Vendors", color = Color.White,  fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = TextUnit(0.9F, TextUnitType.Sp))
            }
            Button(
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                onClick =  {
                    mContext.startActivity(Intent(mContext, UserPage::class.java))
                },

            ) {
                Text("Users", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = TextUnit(0.9F, TextUnitType.Sp))
            }
        }
}

 */



