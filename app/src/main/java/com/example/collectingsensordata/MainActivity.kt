package com.example.collectingsensordata

import android.content.Context
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    //used to determine if we are currently collecting data or not
    private var collecting: Boolean = false

    //textViews for displaying data, public so Sensors class can update
    public lateinit var acclX : TextView
    public lateinit var acclY : TextView
    public lateinit var acclZ : TextView
    public lateinit var lightLevel: TextView
    public lateinit var prox: TextView

    public lateinit var mSensorManager : SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TODO: detect if device has needed sensors

        //objects from xml
        val collectButton = findViewById(R.id.collectButton) as Button

        //initiate textViews
        acclX = findViewById(R.id.acclX) as TextView
        acclY = findViewById(R.id.acclY) as TextView
        acclZ = findViewById(R.id.acclZ) as TextView
        lightLevel = findViewById(R.id.lightLevel) as TextView
        prox = findViewById(R.id.prox) as TextView


        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val sensors = Sensors(this)


        //when collect button is pressed, start collecting data
        collectButton.setOnClickListener() {
            if (collecting) {
                collecting = false;
                collectButton.setText("Collect Data");
                sensors.pauseRecording()
            }
            else {
                collecting = true;
                collectButton.setText("Stop Collecting");
                sensors.startRecording()
            }
        }

    }

    public fun updateAccl (x: Float) {
        acclX.text = x.toString()
    }
}