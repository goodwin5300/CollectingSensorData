package com.example.collectingsensordata

import android.content.Context
import android.os.Environment
import android.util.Log
import android.util.Log.ASSERT
import androidx.core.net.toUri
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*

class DataStorage (context: Context) {

    private lateinit var storage : FirebaseStorage
    private lateinit var storageRef : StorageReference
    private lateinit var userRef : StorageReference
    private lateinit var fileRef : StorageReference
    private lateinit var context : Context


    init {

        this.context = context

        //setup the file for upload
        //need a unique name for each instance of app
        var uniqueID = FirebaseInstallations.getInstance().id


        //get todays date for unique name with for each date
        val sdf = SimpleDateFormat("dd-M-yyyy-mm-ss")
        val currentDate = sdf.format(Date())

        //create file with unique name for todays date
        val path = context.filesDir.absolutePath + "/data"
        //val path = context.filesDir.absolutePath + "/dataadfsdf"
        Log.d("file path", path)
        var f = File(path)
        f.createNewFile()
        f.writeText("")

        //setup firebase storage connection
        storage = Firebase.storage
        storageRef = storage.reference
        fileRef = storageRef.child(uniqueID.toString() + "/" + currentDate+".txt")

        var uploadTask = fileRef.putFile(f.toUri())


    }

}