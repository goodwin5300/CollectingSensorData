package com.example.collectingsensordata

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.collectingsensordata.SleepReceiver
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest

//this class manages subscribing and unsubscribing to updates from sleep API
class SleepRequestsManager(private val context: Context) {

    private val sleepReceiverPendingIntent by lazy {
        SleepReceiver.createPendingIntent(context)
    }

    //check whether data can be accessed and subscribe to updates if still can
    fun requestSleepUpdates(requestPermission: () -> Unit = {}) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("SleepRequestManager", "Subscribing to sleep updates");
            subscribeToSleepUpdates()
        } else {
            requestPermission()
        }
    }

    fun subscribeToSleepUpdates() {
        //ask sleep API to send data
        Log.d("SleepRequestManager", "Subscribing to sleep updates function");
        ActivityRecognition.getClient(context).requestSleepSegmentUpdates (sleepReceiverPendingIntent,
                SleepSegmentRequest.getDefaultSleepSegmentRequest())
    }

    fun unsubscribeFromSleepUpdates() {
        ActivityRecognition.getClient(context).removeSleepSegmentUpdates(sleepReceiverPendingIntent)
    }

}