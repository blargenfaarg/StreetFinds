package com.example.sprintone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class UserSignUp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SprintOneTheme {
                UserSignUpForm()
            }
        }
    }
}

@Composable
@Preview
fun UserSignUpForm() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var verifyPass by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isEmailValid by remember { mutableStateOf(true) }
    var doPasswordsMatch by remember { mutableStateOf(true) }


    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val emailRegex = Regex("""^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""")
    val db = Firebase.firestore

    Surface(modifier = Modifier.fillMaxSize())
    {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(Color.hsl(225f, 0.6f, 0.9f))
            )
            {
                Text(
                    text = "New Users",
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    fontSize = 38.sp,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp),
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        isEmailValid = emailRegex.matches(it)
                    },
                    label = { Text(text = "Enter an email address", color = Color.Black) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                )
                if (!isEmailValid) {
                    Text(
                        text = "Please enter a valid email address (e.g., xyz123@domain.com)",
                        color = Color.Red,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    )
                }
                Divider()
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = "Enter a password", color = Color.Black) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                )
                OutlinedTextField(
                    value = verifyPass,
                    onValueChange = {
                        verifyPass = it
                        doPasswordsMatch = verifyPass == password
                    },
                    label = { Text(text = "Re-enter password", color = Color.Black) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                )
                if (!doPasswordsMatch) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    )
                    {
                        Text(
                            text = "Passwords do not match",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Red,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        )
                    }
                }
            }
            Divider(color = Color.DarkGray)

            successMessage?.let {
                Text(
                    text = it,
                    color = Color.Green,
                    modifier = Modifier.padding(16.dp)
                )
            }


            Button(
                onClick = {
                    val user = hashMapOf(
                        "Email" to email,
                        "Password" to password,
                    )
                    db.collection("users").whereEqualTo("Email", email)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                val userId = UUID.randomUUID().toString()

                                db.collection("users").document(userId).set(user)
                                successMessage = "Success! Logging in..."
                                saveUserLoggedInState(context, true)
                                saveUserType(context, "buyer")
                                saveUserEmail(context, email)

                                coroutineScope.launch {
                                    delay(5000)
                                    successMessage = null
                                }
                                context.startActivity(Intent(context, ListActivity::class.java))
                            } else {
                                errorMessage = "An account with this email already exists."
                                coroutineScope.launch {
                                    delay(5000)
                                    errorMessage = null
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Error", "Error checking account existence: $e")
                            errorMessage = "Unexpected error occurred. Please try again."
                        }
                },
                enabled = email.isNotBlank()
                        && password.isNotBlank() && verifyPass.isNotBlank() && doPasswordsMatch,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color.hsl(225f, 0.6f, 0.9f))
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }
            Text("or")

            Button(
                onClick = {
                    context.startActivity(Intent(context, UserLogIn::class.java))
                },
                colors = ButtonDefaults.buttonColors(Color.LightGray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            )
            {
                Text(
                    text = "Sign into Existing Account",
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }

            errorMessage?.let {
                Dialog(onDismissRequest = { }) {
                    Card(modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth())
                    {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
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