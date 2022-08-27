package com.example.buttoncontrolledrccarapp

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Button Controlled RC Car"

        val connectBtn: Button = findViewById(R.id.connectBluetoothBtn)
        val disconnectBtn: Button = findViewById(R.id.disconnectBluetoothBtn)
        val forwardBtn: ImageButton = findViewById(R.id.forwardBtn)
        val backwardBtn: ImageButton = findViewById(R.id.backwardBtn)
        val leftBtn: ImageButton = findViewById(R.id.leftBtn)
        val rightBtn: ImageButton = findViewById(R.id.rightBtn)
        val stopBtn: ImageButton = findViewById(R.id.stopBtn)

        connectBtn.setOnClickListener {
            val intent = Intent(this, ConnectBluetoothActivity::class.java)
            startActivity(intent)
        }

        m_address = intent.getStringExtra(ConnectBluetoothActivity.EXTRA_ADDRESS).toString()
        val connected: String = intent.getStringExtra(ConnectBluetoothActivity.CONNECTED).toString()
        if (connected == "YES") {
            ConnectToDevice(this).execute()

            disconnectBtn.setOnClickListener { disconnect() }
            forwardBtn.setOnClickListener { sendCommand("1") }
            backwardBtn.setOnClickListener { sendCommand("2") }
            leftBtn.setOnClickListener { sendCommand("3") }
            rightBtn.setOnClickListener { sendCommand("4") }
            stopBtn.setOnClickListener { sendCommand("0") }
        } else {
            forwardBtn.setOnClickListener { Toast.makeText(applicationContext, "No bluetooth device connected", Toast.LENGTH_SHORT).show() }
            backwardBtn.setOnClickListener { Toast.makeText(applicationContext, "No bluetooth device connected", Toast.LENGTH_SHORT).show() }
            leftBtn.setOnClickListener { Toast.makeText(applicationContext, "No bluetooth device connected", Toast.LENGTH_SHORT).show() }
            rightBtn.setOnClickListener { Toast.makeText(applicationContext, "No bluetooth device connected", Toast.LENGTH_SHORT).show() }
            stopBtn.setOnClickListener { Toast.makeText(applicationContext, "No bluetooth device connected", Toast.LENGTH_SHORT).show() }
        }

    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try{
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "Please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
//                Log.i("data", "couldn't connect")
                Toast.makeText(this.context, "Could not connect", Toast.LENGTH_SHORT).show()
            } else {
                m_isConnected = true
                Toast.makeText(this.context, "Connected", Toast.LENGTH_SHORT).show()
            }
            m_progress.dismiss()
        }
    }
}