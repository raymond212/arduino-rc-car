package com.example.buttoncontrolledrccarapp

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Button Controlled RC Car"
        val connectBtn: Button = findViewById(R.id.connectBluetoothBtn)
        connectBtn.setOnClickListener {
            val intent = Intent(this, ConnectBluetoothActivity::class.java)
            startActivity(intent)
        }
        buttonsOff()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (m_isConnected) {
            buttonsOn()
        } else {
            buttonsOff()
        }
    }

    private fun buttonsOn() {
        val disconnectBtn: Button = findViewById(R.id.disconnectBluetoothBtn)
        val forwardBtn: ImageButton = findViewById(R.id.forwardBtn)
        val backwardBtn: ImageButton = findViewById(R.id.backwardBtn)
        val leftBtn: ImageButton = findViewById(R.id.leftBtn)
        val rightBtn: ImageButton = findViewById(R.id.rightBtn)
        val stopBtn: ImageButton = findViewById(R.id.stopBtn)
        disconnectBtn.setOnClickListener { disconnect() }
        forwardBtn.setOnClickListener { sendCommand("1") }
        backwardBtn.setOnClickListener { sendCommand("2") }
        leftBtn.setOnClickListener { sendCommand("3") }
        rightBtn.setOnClickListener { sendCommand("4") }
        stopBtn.setOnClickListener { sendCommand("0") }
    }

    private fun buttonsOff() {
        val disconnectBtn: Button = findViewById(R.id.disconnectBluetoothBtn)
        val forwardBtn: ImageButton = findViewById(R.id.forwardBtn)
        val backwardBtn: ImageButton = findViewById(R.id.backwardBtn)
        val leftBtn: ImageButton = findViewById(R.id.leftBtn)
        val rightBtn: ImageButton = findViewById(R.id.rightBtn)
        val stopBtn: ImageButton = findViewById(R.id.stopBtn)
        disconnectBtn.setOnClickListener { toast("No bluetooth device connected") }
        forwardBtn.setOnClickListener { toast("No bluetooth device connected") }
        backwardBtn.setOnClickListener { toast("No bluetooth device connected") }
        leftBtn.setOnClickListener { toast("No bluetooth device connected") }
        rightBtn.setOnClickListener { toast("No bluetooth device connected") }
        stopBtn.setOnClickListener { toast("No bluetooth device connected") }
    }

    private fun toast(input: String) {
        Toast.makeText(applicationContext, input, Toast.LENGTH_SHORT).show()
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
        buttonsOff()
    }
}