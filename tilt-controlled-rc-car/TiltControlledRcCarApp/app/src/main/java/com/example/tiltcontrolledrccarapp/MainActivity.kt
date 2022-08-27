package com.example.tiltcontrolledrccarapp

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.lang.System.currentTimeMillis
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity(), SensorEventListener {


    private var sensorOn: Boolean = true
    private var connected: String = "NO"

    private val maxPower: Float = 10.2f
    private val threshold = 1
    private lateinit var mSensorManager: SensorManager
    private var mGravitySensor: Sensor? = null

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

        title = "Tilt Controlled RC Car"


        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        mSensorManager!!.registerListener(this,mGravitySensor,
            SensorManager.SENSOR_DELAY_GAME)

        val connectBtn: Button = findViewById(R.id.connectBluetoothBtn)
        val disconnectBtn: Button = findViewById(R.id.disconnectBluetoothBtn)

        connectBtn.setOnClickListener {
            val intent = Intent(this, ConnectBluetoothActivity::class.java)
            startActivity(intent)
        }

        m_address = intent.getStringExtra(ConnectBluetoothActivity.EXTRA_ADDRESS).toString()
        connected = intent.getStringExtra(ConnectBluetoothActivity.CONNECTED).toString()
        if (connected == "YES") {
            ConnectToDevice(this).execute()
            disconnectBtn.setOnClickListener { disconnect() }
        } else {
            disconnectBtn.setOnClickListener { toast("No bluetooth device connected") }
        }

    }

    private var lastCall = 0L

    override fun onSensorChanged(event: SensorEvent?) {
        val curTime = currentTimeMillis()
        if (m_isConnected && curTime - lastCall > 1000) {
            lastCall = curTime
            if (event != null) {
                val xAccel = event.values[0]
                val yAccel = event.values[1]
                val power = sqrt(xAccel * xAccel + yAccel * yAccel)
                var powerPercent = toPercent(power)
                var forward = 0
                var left = 0
                var standby = true

                if (abs(xAccel) > threshold) {
                    forward = toPercent(-1 * xAccel)
                    standby = false
                }
                if (abs(yAccel) > threshold) {
                    left = toPercent(-1 * yAccel)
                    standby = false
                }

                if (forward > 0) {
                    setViewText(R.id.forwardTv, "Foward: $forward%")
                    setViewText(R.id.backwardTv, "Backward: 0%")
                } else if (forward < 0) {
                    setViewText(R.id.forwardTv, "Forward: 0%")
                    setViewText(R.id.backwardTv, "Backward: " + (-1 * forward) + "%")
                } else {
                    setViewText(R.id.forwardTv, "Forward: 0%")
                    setViewText(R.id.backwardTv, "Backward: 0%")
                }

                if (left > 0) {
                    setViewText(R.id.leftTv, "Left: $left%")
                    setViewText(R.id.rightTv, "Right: 0%")
                } else if (left < 0) {
                    setViewText(R.id.leftTv, "Left: 0%")
                    setViewText(R.id.rightTv, "Right: " + (-1 * left) + "%")
                } else {
                    setViewText(R.id.leftTv, "Left: 0%")
                    setViewText(R.id.rightTv, "Right: 0%")
                }

                if (standby) {
                    setViewText(R.id.powerTv, "Power: Standby")
                    powerPercent = 0
                } else {
                    setViewText(R.id.powerTv, "Power: $powerPercent%")
                }
                sendCommand(powerPercent)
                sendCommand(forward)
                sendCommand(left)
            }
        }
//        if (m_isConnected && sensorOn) {
//            sensorOn = false
//
//            val handler = Handler()
//            handler.postDelayed({
//                sensorOn = true;
//            }, 1000)
//        }
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }


    private fun setViewText(id: Int, input: String) {
        val tv = findViewById<TextView>(id).apply {
            text = input
        }
    }

    private fun toPercent(x: Float): Int {
        return ((x / maxPower) * 100).roundToInt()
    }

    private fun toast(input: String) {
        Toast.makeText(applicationContext, input, Toast.LENGTH_SHORT).show()
    }

    private fun sendCommand(input: Int) {
        if (m_bluetoothSocket != null) {
            try{
                m_bluetoothSocket!!.outputStream.write(input)
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
            connected = "NO"
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
                Toast.makeText(this.context, "Could not connect", Toast.LENGTH_SHORT).show()
            } else {
                m_isConnected = true
                Toast.makeText(this.context, "Connected", Toast.LENGTH_SHORT).show()
            }
            m_progress.dismiss()
        }
    }
}