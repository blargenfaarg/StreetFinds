@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.sprintone

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadSearchBar() {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    //val ctx = LocalContext.current
    val searchHistory = listOf(
        "Don Julio's Taco Truck",
        "Street Spice Express",
        "Not Topper's Pizza",
        "Generic meal",
        "Oh WowICE"
    )
    SearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = { newQuery ->
            println("search on $newQuery")
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
            val filteredPlaces =
                searchHistory.filter { it.contains(query, true) }
            filteredPlaces.forEach { name ->
                Text(
                    text = name,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { println("Info: $name") }
                )
            }
        }
    }
}








