@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.sprintone

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadSearchBar() {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var sentToMap by remember { mutableStateOf(false) }
    var truckNames by remember { mutableStateOf(emptyList<String>())}

    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current


    LaunchedEffect(key1 = true) {
        try {
            val querySnapshot = db.collection("trucks").get().await()
            val names = querySnapshot.documents.mapNotNull { it.getString("Name") }
            truckNames = names

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    SearchBar(
        query = query,
        onQueryChange = { query = it},
        onSearch = { newQuery ->
            Log.e("Success", "Query: $query")
            sentToMap = true
            val intent = Intent(context, MapsComposeActivity::class.java)
            intent.putExtra("query", query)
            context.startActivity(intent)
        },
        active = active,
        onActiveChange = { active = it },
        placeholder = {
            Text(text = "Search")
        },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
        },
        trailingIcon = if (active) {
            {
                IconButton(
                    onClick = { if (query.isNotEmpty()) query = "" else active = false }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                }
            }
        } else null
    )
    {

        if(query.isNotEmpty()) {
            val filteredTrucks =
                truckNames.filter { it.contains(query, true) }
            filteredTrucks.forEach { name ->
                Text(
                    text = name,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { query = name }
                )
            }
        }
    }
}