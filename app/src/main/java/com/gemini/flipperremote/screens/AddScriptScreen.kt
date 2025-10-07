package com.gemini.flipperremote.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AddScriptScreen(navController: NavController) {
    var scriptName by remember { mutableStateOf("") }
    var scriptContent by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = scriptName,
            onValueChange = { scriptName = it },
            label = { Text("Script Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = scriptContent,
            onValueChange = { scriptContent = it },
            label = { Text("Paste script content here") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Button(
            onClick = { 
                // TODO: Save script
                navController.popBackStack() 
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Script")
        }
    }
}
