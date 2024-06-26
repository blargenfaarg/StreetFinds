package com.example.sprintone

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UserLogIn : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SprintOneTheme {
                // A surface container using the 'background' color from the theme
                UserLogInScreen()
            }
        }
    }
}
@Preview
@Composable
fun UserLogInScreen()
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
            Card(modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(Color.hsl(225f, 0.6f, 0.9f)))
            {
                Text(
                    text = "User Sign In",
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    fontSize = 40.sp,
                    modifier = Modifier.padding(20.dp))
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
            }

            successMessage?.let {
                Text(
                    text = it, // Success! Logging in...
                    color = Color.Green,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Button(
                onClick = {
                    if(email.isNotEmpty() && password.isNotEmpty()) {
                        db.collection("users").whereEqualTo("Email", email)
                            .get().addOnSuccessListener { documents ->
                                if (documents.isEmpty) {
                                    errorMessage = "User not found"
                                    coroutineScope.launch {
                                        delay(5000)
                                        errorMessage = null
                                    }
                                } else
                                {
                                    val userDocument = documents.first()
                                    val storedPassword = userDocument.getString("Password")

                                    if (storedPassword == password)
                                    {
                                        successMessage = "Success! Logging in..."
                                        saveUserLoggedInState(context, true)
                                        saveUserType(context, "buyer")
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
                },
                enabled = email.isNotBlank()
                        && password.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color.hsl(225f, 0.6f, 0.9f)))
            {
                Text("Sign in",
                    fontSize = 20.sp,
                    color = Color.Black)
            }

            errorMessage?.let {
                Dialog(onDismissRequest = {  }) {
                    Card(modifier = Modifier.height(80.dp).fillMaxWidth())
                    {
                        Column(verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally)
                        {
                            Text(
                                text = it,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
