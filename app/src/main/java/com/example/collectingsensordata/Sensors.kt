package com.example.collectingsensordata

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.SleepClassifyEvent
import java.text.SimpleDateFormat
import java.util.*
import java.util.Arrays.copyOf
import kotlin.math.pow

//this class reads data from the sensors

class Sensors( MainActivity: MainActivity) : AppCompatActivity(), SensorEventListener {

    private lateinit var mSensorManager : SensorManager
    private lateinit var accelerometer : Sensor //used to x,y,z acceleration
    private lateinit var proximitySensor : Sensor //used to detect if light sensor is blocked
    private lateinit var lightSensor : Sensor //used to detect light levels
    private lateinit var ma : MainActivity
    private lateinit var storage : DataStorage //used to upload data to cloud

    //storing sensor readings locally for easy logging
    private lateinit var acclReadings : FloatArray
    private var proxReading : Float = 0.0F
    private var lightReading: Float = 0.0F
    private var acclVariance: Float = 0.0F
    //sleep API values
    private var saConfidence : Int = 0
    private var saLight : Int = 0
    private var saMotion : Int = 0


    //setting up our sensors
    init {
        ma = MainActivity

        mSensorManager = ma.mSensorManager

        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        ma = MainActivity

        storage = DataStorage(ma.applicationContext);

        //initialize accl readings array
        acclReadings = FloatArray(3)
        val oldAcclReadings = acclReadings
        acclReadings[0] = 0.0F
        acclReadings[1] = 0.0F
        acclReadings[2] = 0.0F

        acclVariance = bodyMovCalc(acclReadings, oldAcclReadings)

        //initialize sleep request manager
        //sleepRequestManager = SleepRequestsManager(ma);


        //TODO: figure out getting data from microphone
    }

    //called when recording button is pressed and recording is paused
    fun startRecording() {
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)

        ma.requestPerm();
        ma.subscribeToSleepEvents();
        storage.startUploading();

        Log.d("Sensors", "Started Recording")
    }

    //called when recording button is pressed and app is currently recording data
    public fun pauseRecording() {
        Log.d("Sensors", "Pausing recording");
        mSensorManager.unregisterListener(this)
        storage.uploadData()
        //unsubscribe from sleep API
        ma.unSubscribeToSleepEvents()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //detect which sensor is updated and change on screen
        //log to file to be uploaded to database

        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                ma.acclX.text = event.values[0].toString()
                ma.acclY.text = event.values[1].toString()
                ma.acclZ.text = event.values[2].toString()
                val oldAcclReadings = acclReadings.copyOf(acclReadings.size)
                acclReadings[0] = event.values[0]
                acclReadings[1] = event.values[1]
                acclReadings[2] = event.values[2]
                //calculate the acceleration variance
                acclVariance = bodyMovCalc(acclReadings, oldAcclReadings)
                if (acclReadings[0] != 0f || acclReadings[1] != 0f || acclReadings[2] != 0f)
                    logAllData()
            } else if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                ma.prox.text = event.values[0].toString()
                proxReading = event.values[0]
                logAllData()
            } else if (event.sensor.type == Sensor.TYPE_LIGHT) {
                ma.lightLevel.text = event.values[0].toString()
                lightReading = event.values[0]
                logAllData()
            }
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //todo
    }

    fun onSleepClassifyEvent(event: SleepClassifyEvent) {
        Log.d("Sensors", "Got sleep API data")
        if(event != null) {
            saConfidence = event.confidence
            saLight = event.light
            saMotion = event.motion
            logAllData();
        }
    }

    private fun logAllData() {
        val sdf = SimpleDateFormat("hh:mm:ss")
        val currentDate = sdf.format(Date())
        storage.logData(currentDate + "," + acclReadings[0].toString() + "," + acclReadings[1].toString() + ","
                + acclReadings[2].toString() + "," +acclVariance+","+ proxReading + "," + lightReading+","+saConfidence+","+
                saLight+","+saMotion+"\n")
    }

    //function to detect body movements
    fun bodyMovCalc(acclReadings: FloatArray, oldAcclReadings: FloatArray): Float {
        //calculate overall acceleration
        //sqrt(ax^2 + ay^2 + az^2)
        val accl = Math.sqrt((acclReadings[0].toDouble().pow(2))+(acclReadings[0].toDouble().pow(2))+(acclReadings[0].toDouble().pow(2)))
        val oldAccl = Math.sqrt((oldAcclReadings[0].toDouble().pow(2))+(oldAcclReadings[0].toDouble().pow(2))+(oldAcclReadings[0].toDouble().pow(2)))

        //calculate accleration variance
        //variance = a(i) - a(i-1)
        val variance = accl - oldAccl

        return variance.toFloat()

    }

}