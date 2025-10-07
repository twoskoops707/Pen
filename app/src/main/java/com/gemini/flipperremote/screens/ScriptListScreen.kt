package com.gemini.flipperremote.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ScriptListScreen(navController: NavController) {
    val scripts = listOf("Script 1", "Script 2", "Script 3") // Placeholder
    Column {
        Button(onClick = { navController.navigate("deviceScan") }) {
            Text("Scan for Devices")
        }
        LazyColumn {
            items(scripts) { script ->
                Text(text = script)
            }
        }
    }
}
