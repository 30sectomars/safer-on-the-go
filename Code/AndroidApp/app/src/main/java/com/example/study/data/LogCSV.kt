package com.example.study.data

import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class LogCSV {

    private lateinit var settingsDataStore: SettingsDataStore
    private lateinit var file: File
    private lateinit var path: File
    private lateinit var dir: File

    private var initialized = false

    init {
        try {
            // get public documents path
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/Study_Log")

            // validate directory path
            dir = File(path.absolutePath)
            if(!dir.exists()) dir.mkdir()
        } catch (e: IOException) {
            Log.e("IOException", "exception '" + e.message + "' in LogCSV initialization method")
        }
    }

    fun createNewFile() {
        // check if initialized
        if (!initialized) return
        // create new file
        try {
            // generate filename
            val participantID = settingsDataStore.participantID
            val selectionMethod = settingsDataStore.selectionMethod
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMANY).format(Date())
            val fileName = "${participantID}_${selectionMethod}_$timeStamp.csv"
            file = File(path, fileName)

            // check if file exists
            val fileExists = file.exists()
            if (!fileExists) file.createNewFile()

            // write header to file
            file.appendText(text = "Timestamp, Selection, Source, Description, Index\n")
        } catch (e: IOException) {
            Log.e("IOException", "exception '" + e.message + "' in createNewFile() method")
        }
    }

    fun appendLog(sel : String,src : String, desc : String, index : String = "") {
        // check if file exists
        if (!file.exists()) createNewFile()

        try {
            val timestamp = System.currentTimeMillis().toString()
            file.appendText( "$timestamp, $sel, $src, $desc, $index\n")
        } catch (e: IOException) {
            Log.e("IOException", "exception '" + e.message + "' in appendLog() method")
        }
    }

    fun setSettingsDataStore(settingsDataStore: SettingsDataStore) {
        this.settingsDataStore = settingsDataStore
        initialized = true
    }
}