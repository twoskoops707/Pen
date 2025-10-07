package com.gemini.flipperremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gemini.flipperremote.screens.AddScriptScreen
import com.gemini.flipperremote.screens.DeviceScanScreen
import com.gemini.flipperremote.screens.DeviceControlScreen
import com.gemini.flipperremote.screens.ScriptListScreen
import com.gemini.flipperremote.ui.theme.FlipperRemoteTheme
import androidx.navigation.NavType
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlipperRemoteApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlipperRemoteApp() {
    val context = LocalContext.current
    val navController = rememberNavController()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permission results
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = arrayOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT
        ).filter {
            ContextCompat.checkSelfPermission(context, it) != android.content.pm.PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest)
        }
    }

    FlipperRemoteTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Flipper Remote") }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate("addScript") }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Script")
                }
            }
        ) {
            Box(modifier = Modifier.padding(it)) {
                NavHost(navController = navController, startDestination = "scriptList") {
                    composable("scriptList") { ScriptListScreen(navController) }
                    composable("addScript") { AddScriptScreen(navController) }
                    composable("deviceScan") { DeviceScanScreen(navController) }
                    composable(
                        "deviceControl/{deviceAddress}",
                        arguments = listOf(navArgument("deviceAddress") { type = NavType.StringType })
                    ) { backStackEntry ->
                        DeviceControlScreen(
                            navController = navController,
                            deviceAddress = backStackEntry.arguments?.getString("deviceAddress") ?: ""
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlipperRemoteApp()
}
