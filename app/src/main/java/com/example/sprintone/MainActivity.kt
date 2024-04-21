package com.example.sprintone

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity()
{
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SprintOneTheme {
                LoadGreeting()
            }
        }
    }
}

object UserType {
    var BUYER = false
    var VENDOR = false
    var GUEST = false
}

@Preview
@Composable
fun LoadGreeting()
{
    val context = LocalContext.current

    if (isUserLoggedIn(context))
    {
        val userType = getUserType(context)
        if (userType != null) {
            when (userType)
            {
                "buyer" -> {
                    UserType.BUYER = true
                    UserType.VENDOR = false
                    UserType.GUEST = false
                }
                "vendor" -> {
                    UserType.VENDOR = true
                    UserType.BUYER = false
                    UserType.GUEST = false
                }
            }
        }
        context.startActivity(Intent(context, ListActivity::class.java))
    }
    else {
        // User is not logged in, show login screen

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.truckpin),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(150.dp, 150.dp)
                )
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
                        onClick = {
                            UserType.BUYER = true
                            UserType.VENDOR = false
                            UserType.GUEST = false
                            context.startActivity(Intent(context, UserSignUp::class.java))
                        },
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
                        onClick = {
                            UserType.GUEST = false
                            UserType.BUYER = false
                            UserType.VENDOR = true
                            context.startActivity(Intent(context, VendorSignUp::class.java))
                        },
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
                    onClick = {
                        UserType.GUEST = true
                        UserType.BUYER = false
                        UserType.VENDOR = false
                        context.startActivity(Intent(context, ListActivity::class.java))
                    },
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
}

@Composable
fun PickImageFromGallery()
{
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){ uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        imageUri?.let {
            if (Build.VERSION.SDK_INT < 28)
            {
                bitmap.value = MediaStore.Images
                    .Media.getBitmap(context.contentResolver, it)
            }  else
            {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                bitmap.value = ImageDecoder.decodeBitmap(source)
            }

            bitmap.value?.let { btm ->
                Image(
                    bitmap = btm.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(400.dp)
                        .padding(20.dp)
                )

            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { launcher.launch("image/*") })
        {
            Text(text="Pick Image")
        }
    }
}


fun saveUserLoggedInState(context: Context, isLoggedIn: Boolean) {
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("isLoggedIn", isLoggedIn)
    editor.apply()
}

fun isUserLoggedIn(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("isLoggedIn", false)
}

fun saveUserType(context: Context, userType: String) {
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("userType", userType)
    editor.apply()
}
fun getUserType(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    return sharedPreferences.getString("userType", null)
}

fun saveUserEmail(context: Context, email: String)
{
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("email", email)
    editor.apply()
}

fun getUserEmail(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    return sharedPreferences.getString("email", null)
}