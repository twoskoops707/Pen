package com.gemini.flipperremote.screens

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gemini.flipperremote.bluetooth.BluetoothManager

@Composable
fun DeviceScanScreen(navController: NavController) {
    val context = LocalContext.current
    val bluetoothManager = remember { BluetoothManager(context) }
    var scanResults by remember { mutableStateOf(listOf<ScanResult>()) }
    var isScanning by remember { mutableStateOf(false) }
    var isBluetoothEnabled by remember { mutableStateOf(bluetoothManager.isBluetoothEnabled()) }

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isBluetoothEnabled = bluetoothManager.isBluetoothEnabled()
    }

    if (!bluetoothManager.isBluetoothSupported()) {
        Text("Bluetooth is not supported on this device")
        return
    }

    if (!isBluetoothEnabled) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Bluetooth is disabled. Please enable it to scan for devices.")
            Button(onClick = { 
                val intent = bluetoothManager.createEnableBluetoothIntent()
                enableBluetoothLauncher.launch(intent)
            }) {
                Text("Enable Bluetooth")
            }
        }
        return
    }

    val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                if (!scanResults.any { it.device.address == result.device.address }) {
                    scanResults = scanResults + it
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            bluetoothManager.stopScan(scanCallback)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = { 
                scanResults = emptyList()
                bluetoothManager.startScan(scanCallback)
                isScanning = true
            }, enabled = !isScanning) {
                Text("Start Scan")
            }
            Button(onClick = { 
                bluetoothManager.stopScan(scanCallback)
                isScanning = false
            }, enabled = isScanning) {
                Text("Stop Scan")
            }
        }
        LazyColumn {
            items(scanResults) { result ->
                DeviceItem(result = result, onClick = {
                    bluetoothManager.stopScan(scanCallback)
                    isScanning = false
                    navController.navigate("deviceControl/${result.device.address}")
                })
            }
        }
    }
}

@Composable
fun DeviceItem(result: ScanResult, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(text = result.device.name ?: "Unknown Device")
        Text(text = result.device.address)
    }
}


