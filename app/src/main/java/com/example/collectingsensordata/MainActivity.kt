package com.example.collectingsensordata

import android.Manifest
import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest

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

    private lateinit var sensors : Sensors;

    private val sleepRequestManager by lazy{
        SleepRequestsManager(this)
    }

    private val permissionRequester: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                requestActivityRecognitionPermission()
            } else {
                Log.d("Main Activity", "Subscribing to updates");
                sleepRequestManager.subscribeToSleepUpdates()
            }
        }

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

        sensors = Sensors(this)

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

    override fun onDestroy() {
        super.onDestroy()
        sensors.pauseRecording();
    }

    public fun requestPerm() {
        Log.d("permission", "Requesting permission");
        sleepRequestManager.requestSleepUpdates(requestPermission = {
            permissionRequester.launch(ACTIVITY_RECOGNITION)
        })
    }

    //method to open the settings
    private fun requestActivityRecognitionPermission() {

        Log.d("permission", "passing intent for settings");

        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        startActivity(intent)
    }

    public fun subscribeToSleepEvents() {
        sleepRequestManager.subscribeToSleepUpdates();
    }

    public fun unSubscribeToSleepEvents() {
        sleepRequestManager.unsubscribeFromSleepUpdates();
    }

}