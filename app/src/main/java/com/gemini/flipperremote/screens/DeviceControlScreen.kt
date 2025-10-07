package com.gemini.flipperremote.screens

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.system.PingRequest
import com.gemini.flipperremote.bluetooth.BluetoothManager
import com.gemini.flipperremote.bluetooth.FlipperUuids

@Composable
fun DeviceControlScreen(navController: NavController, deviceAddress: String) {
    val context = LocalContext.current
    val bluetoothManager = remember { BluetoothManager(context) }
    var connectionState by remember { mutableStateOf("Connecting...") }
    var servicesDiscovered by remember { mutableStateOf(false) }
    var rxCharacteristic by remember { mutableStateOf<BluetoothGattCharacteristic?>(null) }
    var txCharacteristic by remember { mutableStateOf<BluetoothGattCharacteristic?>(null) }
    var command by remember { mutableStateOf("") }
    var receivedData by remember { mutableStateOf("") }

    val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    connectionState = "Connected"
                    gatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    connectionState = "Disconnected"
                    servicesDiscovered = false
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt?.getService(FlipperUuids.SERVICE_UUID)
                rxCharacteristic = service?.getCharacteristic(FlipperUuids.RX_CHAR_UUID)
                txCharacteristic = service?.getCharacteristic(FlipperUuids.TX_CHAR_UUID)
                txCharacteristic?.let { bluetoothManager.setCharacteristicNotification(it, true) }
                servicesDiscovered = true
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray) {
            if (characteristic.uuid == FlipperUuids.TX_CHAR_UUID) {
                val main = Main.parseFrom(value)
                receivedData = "Received: ${main.commandStatus} - ${main.systemPingResponse.data.toString(Charsets.UTF_8)}"
            }
        }
    }

    LaunchedEffect(Unit) {
        bluetoothManager.connect(deviceAddress, gattCallback)
    }

    DisposableEffect(Unit) {
        onDispose {
            bluetoothManager.disconnect()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Device: $deviceAddress")
        Text(text = "Status: $connectionState")
        if (servicesDiscovered) {
            Text(text = "Services discovered")
            Row {
                OutlinedTextField(
                    value = command,
                    onValueChange = { command = it },
                    label = { Text("Enter command") }
                )
                Button(onClick = { 
                    rxCharacteristic?.let {
                        val pingRequest = PingRequest.newBuilder().setData(command.toByteArray()).build()
                        val main = Main.newBuilder().setCommandId(1).setSystemPingRequest(pingRequest).build()
                        bluetoothManager.writeCharacteristic(it, main.toByteArray())
                    }
                }) {
                    Text("Send Ping")
                }
            }
            Text(text = receivedData)
        }
    }
}



