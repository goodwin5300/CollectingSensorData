package com.example.collectingsensordata

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

//this class reads data from the sensors

class Sensors( MainActivity: MainActivity) : AppCompatActivity(), SensorEventListener {

    private lateinit var mSensorManager : SensorManager
    private lateinit var accelerometer : Sensor //used to x,y,z acceleration
    private lateinit var proximitySensor : Sensor //used to detect if light sensor is blocked
    private lateinit var lightSensor : Sensor //used to detect light levels
    private lateinit var ma : MainActivity


    //setting up our sensors
    init {
        ma = MainActivity

        mSensorManager = ma.mSensorManager

        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        ma = MainActivity

        //TODO: figure out getting data from microphone
    }

    //called when recording button is pressed and recording is paused
    fun startRecording() {
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        Log.d("Sensors", "Started Recording")
    }

    //called when recording button is pressed and app is currently recording data
    fun pauseRecording() {
        mSensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //detect which sensor is updated and change on screen
        //todo: log and upload to database

        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                ma.acclX.text = event.values[0].toString() + " m/s²"
                ma.acclY.text = event.values[1].toString() + " m/s²"
                ma.acclZ.text = event.values[2].toString() + " m/s²"
            } else if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                ma.prox.text = event.values[0].toString() + " cm"
            } else if (event.sensor.type == Sensor.TYPE_LIGHT) {
                ma.lightLevel.text = event.values[0].toString() + " °C"
            }
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //todo
    }

}