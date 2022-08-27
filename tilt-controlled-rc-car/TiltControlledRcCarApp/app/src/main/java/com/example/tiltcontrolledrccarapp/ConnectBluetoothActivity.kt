package com.example.tiltcontrolledrccarapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import java.io.IOException

@SuppressLint("MissingPermission")
class ConnectBluetoothActivity : AppCompatActivity() {

    private var m_bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var m_pairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_bluetooth)

        title = "Connect to Bluetooth"

        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (m_bluetoothAdapter == null) {
            Toast.makeText(applicationContext, "This device doesn't support bluetooth", Toast.LENGTH_SHORT).show()
            return
        }
        if (m_bluetoothAdapter?.isEnabled == false) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        val refreshBtn = findViewById<Button>(R.id.refreshBtn)
        refreshBtn.setOnClickListener{ pairedDeviceList() }
    }

    private fun pairedDeviceList() {
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList()
        var deviceNamesAndAddresses : ArrayList<String> = ArrayList()

        if (!m_pairedDevices.isEmpty()) {
            for (device: BluetoothDevice in m_pairedDevices) {
                list.add(device)
                deviceNamesAndAddresses.add(device.name + ": " + device.address)
                Log.i("device", ""+device)
            }
        } else {
            Toast.makeText(applicationContext, "No paired bluetooth devices found", Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceNamesAndAddresses)
        val select_device_list = findViewById<ListView>(R.id.select_device_list)
        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            MainActivity.m_address = device.address
            val connectSuccess = connectToDevice(this)
            if (!connectSuccess) {
                Toast.makeText(this, "Could not connect", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (m_bluetoothAdapter!!.isEnabled) {
                    Toast.makeText(applicationContext, "Bluetooth has been enabled", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Bluetooth has been disabled", Toast.LENGTH_SHORT).show()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(applicationContext, "Bluetooth enabling has been canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun connectToDevice(c: Context): Boolean {
        var connectSuccess = true
        try {
            if (MainActivity.m_bluetoothSocket == null || !MainActivity.m_isConnected) {
                MainActivity.m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                val device: BluetoothDevice = MainActivity.m_bluetoothAdapter.getRemoteDevice(
                    MainActivity.m_address
                )
                MainActivity.m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(
                    MainActivity.m_myUUID
                )
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                MainActivity.m_bluetoothSocket!!.connect()
            }
            MainActivity.m_isConnected = true
        } catch (e: IOException) {
            connectSuccess = false
            e.printStackTrace()
        }
        return connectSuccess
    }
}