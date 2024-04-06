package com.example.sprintone

import android.content.Intent
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun LoadNavBar() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    NavigationBar {
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Place, contentDescription = "test") },
                label = { Text("Map") },
                selected = false,
                onClick = { context.startActivity(Intent(context, MapsComposeActivity::class.java)) }
            )
            NavigationBarItem(
                icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "test") },
                label = { Text("List") },
                selected = false,
                onClick = { context.startActivity(Intent(context, ListActivity::class.java)) }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Filled.AccountBox, contentDescription = "test") },
                label = { Text("Profile") },
                selected = false,
                onClick = {
                    if (UserType.GUEST)
                    {
                        context.startActivity(Intent(context, MainActivity::class.java))
                    }
                    else if (UserType.BUYER)
                    {
                        context.startActivity(Intent(context, UserPage::class.java))
                    }
                    else if (UserType.VENDOR) {
                        context.startActivity(Intent(context, VendorPage::class.java))
                    }
                    else{
                        Log.e("HEYY LOOK AGAIN", "Button Clicked. ")
                    }
                }
            )
    }
}