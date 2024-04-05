package com.example.sprintone

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
                icon = { Icon(Icons.Filled.Favorite, contentDescription = "test") },
                label = { Text("Map") },
                selected = false,
                onClick = { context.startActivity(Intent(context, MapsComposeActivity::class.java)) }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Favorite, contentDescription = "test") },
                label = { Text("List") },
                selected = false,
                onClick = { context.startActivity(Intent(context, ListActivity::class.java)) }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Favorite, contentDescription = "test") },
                label = { Text("Profile") },
                selected = false,
                onClick = { context.startActivity(Intent(context, UserPage::class.java)) }
            )
    }
}