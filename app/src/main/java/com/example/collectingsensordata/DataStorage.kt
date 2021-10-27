package com.example.collectingsensordata

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class DataStorage (context: Context) {

    private lateinit var storage : FirebaseStorage
    private lateinit var storageRef : StorageReference
    private lateinit var userRef : StorageReference
    private lateinit var fileRef : StorageReference
    private lateinit var context : Context
    private lateinit var dataFile : File
    private lateinit var handler: Handler


    init {

        this.context = context

        //setup the file for upload
        //need a unique name for each instance of app
        var uniqueID = FirebaseInstallations.getInstance().id


        //get todays date for unique name with for each date
        val sdf = SimpleDateFormat("dd-M-yyyy")
        val currentDate = sdf.format(Date())

        //create file with unique name for todays date
        val path = context.filesDir.absolutePath + "/data"
        Log.d("file path", path)
        dataFile = File(path)
        dataFile.createNewFile()
        dataFile.writeText("")

        //setup firebase storage connection
        storage = Firebase.storage
        storageRef = storage.reference
        fileRef = storageRef.child("dev/"+uniqueID.toString() + "/" + currentDate+".txt")

        logData("time,acclX,acclY,acclZ,variance,prox,light,confidence,light,motion\n")

        //upload file to storage
        var uploadTask = fileRef.putFile(dataFile.toUri())

        handler = Handler(Looper.getMainLooper())

    }

    public fun logData(data : String) {
        dataFile.appendText(data)
    }

    public fun uploadData() {
        Log.d("DataStorage", "Uploading Data")
        if(dataFile.length() != 0.toLong()) {
            var uploadTask = fileRef.putFile(dataFile.toUri())
        }
    }

    //uploads file once an hour in case of crash
    var uploader: Runnable = object : Runnable {
        override fun run() {
            try {
                periodicUpload()
            } finally {
                handler.postDelayed(this, 1000 * 60 * 60)
            }
        }
    }

    public fun startUploading() {
        uploader.run()
    }

    public fun pauseUploading() {
        handler.removeCallbacks(uploader)
    }

    private fun periodicUpload() {
        uploadData();
        Log.d("DataStorage", "Uploading data periodically");
    }

}