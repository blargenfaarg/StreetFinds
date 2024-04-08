package com.example.sprintone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import java.util.UUID

class VendorSignUp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SprintOneTheme {
                VendorSignUpForm()
            }
        }
    }
}

@Composable
@Preview
fun VendorSignUpForm()
{
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isEmailValid by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()
    val emailRegex = Regex("""^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""")
    val context = LocalContext.current
    val db = Firebase.firestore

    Surface(modifier = Modifier.fillMaxSize())
    {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Create a vendor account",
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                modifier = Modifier.padding(20.dp),
                lineHeight = 25.sp
            )
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email  = it
                    isEmailValid = emailRegex.matches(it)
                },
                label = { Text(text = "Enter an email address", color = Color.Black) },
                isError = !isEmailValid,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )
            }
            OutlinedTextField(
                value = password,
                onValueChange = { password  = it },
                label = { Text(text = "Enter a password", color = Color.Black) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
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
            Button(
                onClick = {
                    val vendor = hashMapOf(
                        "Email" to email,
                        "Password" to password,
                    )
                    db.collection("vendors").whereEqualTo("Email", email)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty)
                            {
                                val vendorId = UUID.randomUUID().toString()
                                db.collection("vendors").document(vendorId).set(vendor)
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
                            else{
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
                        && password.isNotBlank(),
                modifier = Modifier
                    .height(80.dp)
                    .width(250.dp)
                    .padding(16.dp)
                    .background(Color.White)
            ) {
                Text(
                    text = "Create Account",
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }

            OutlinedButton(
                onClick = {
                    context.startActivity(Intent(context, VendorLogIn::class.java))
                },
                colors = ButtonDefaults.buttonColors(Color.LightGray)
            )
            {
                Text(
                    text = "Sign in to existing account",
                    fontSize = 15.sp,
                    color = Color.Black
                )
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