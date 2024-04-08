package com.example.sprintone

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VendorLogIn : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SprintOneTheme {
                // A surface container using the 'background' color from the theme
                VendorLogInScreen()
            }
        }
    }
}
@Preview
@Composable
fun VendorLogInScreen()
{
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val db = Firebase.firestore

    Surface(modifier = Modifier.fillMaxSize())
    {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Text(
                text = "Vendor Sign In",
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                modifier = Modifier.padding(20.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email", color = Color.Black) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password", color = Color.Black) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            )

            Button(
                onClick = {
                    if(email.isNotEmpty() && password.isNotEmpty()) {
                        db.collection("vendors").whereEqualTo("Email", email)
                            .get().addOnSuccessListener { documents ->
                                if (documents.isEmpty) {
                                    errorMessage = "Email not found"
                                    coroutineScope.launch {
                                        delay(5000)
                                        errorMessage = null
                                    }
                                } else
                                {
                                    val vendorDocument = documents.first()
                                    val storedPassword = vendorDocument.getString("Password")

                                    if (storedPassword == password)
                                    {
                                        successMessage = "Success! Logging in..."
                                        saveUserLoggedInState(context, true)
                                        saveUserType(context, "vendor")
                                        saveUserEmail(context, email)
                                        coroutineScope.launch {
                                            delay(5000)
                                            successMessage = null
                                        }
                                        context.startActivity(Intent(context, ListActivity::class.java))
                                    }
                                    else
                                    {
                                        // Passwords don't match, show an error message
                                        errorMessage = "Incorrect Password"
                                        coroutineScope.launch {
                                            delay(5000)
                                            errorMessage = null
                                        }
                                    }
                                }
                            }
                    }
                }
            )
            {
                Text("Sign in")
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
            successMessage?.let {
                Text(
                    text = it,
                    color = Color.Green,
                    modifier = Modifier.padding(16.dp)
                )
            }

        }
    }

}
