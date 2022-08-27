package com.example.tiltcontrolledrccarapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var sensorOn: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val mGravitySensor: Sensor? = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensorManager!!.registerListener(this,mGravitySensor,
            SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (sensorOn) {
            sensorOn = false
            if (event != null) {
                val xAccelTv = findViewById<TextView>(R.id.xAccelTv).apply {
                    text = "x acceleration: " + String.format("%.2f", event.values[0])
                }
                val yAccelTv = findViewById<TextView>(R.id.yAccelTv).apply {
                    text = "y acceleration: " + String.format("%.2f", event.values[1])
                }
                val zAccelTv = findViewById<TextView>(R.id.zAccelTv).apply {
                    text = "z acceleration: " + String.format("%.2f", event.values[2])
                }
            }
            val handler = Handler()
            handler.postDelayed({
                sensorOn = true;
            }, 1000)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}