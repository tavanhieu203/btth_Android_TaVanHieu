package com.example.bth5_06

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var filePath: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecordingAdapter
    private val recordingList = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRecord = findViewById<Button>(R.id.btnRecord)
        val btnStop = findViewById<Button>(R.id.btnStop)
        val btnPlay = findViewById<Button>(R.id.btnPlay)
        val btnList = findViewById<Button>(R.id.btnList)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecordingAdapter(recordingList) { uri -> playRecording(uri) }
        recyclerView.adapter = adapter

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Quyền bị từ chối!", Toast.LENGTH_SHORT).show()
            }
        }

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        btnRecord.setOnClickListener { startRecording() }
        btnStop.setOnClickListener { stopRecording() }
        btnPlay.setOnClickListener { playRecording(null) }
        btnList.setOnClickListener { fetchRecordings() }
    }

    private fun startRecording() {
        filePath = "${externalCacheDir?.absolutePath}/recording.3gp"
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(filePath)
            try {
                prepare()
                start()
                Toast.makeText(this@MainActivity, "Bắt đầu ghi âm...", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null

        val contentValues = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, "recording_${System.currentTimeMillis()}.3gp")
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp")
            put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings")
            put(MediaStore.Audio.Media.IS_PENDING, 1)
        }

        val uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            contentValues.clear()
            contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0)
            contentResolver.update(it, contentValues, null, null)
        }

        Toast.makeText(this, "Ghi âm đã được lưu!", Toast.LENGTH_SHORT).show()
    }

    private fun playRecording(uri: Uri?) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            try {
                if (uri != null) {
                    setDataSource(applicationContext, uri)
                } else {
                    setDataSource(filePath)
                }
                prepare()
                start()
                Toast.makeText(this@MainActivity, "Đang phát...", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchRecordings() {
        recordingList.clear()
        val projection = arrayOf(MediaStore.Audio.Media._ID)
        val selection = "${MediaStore.Audio.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("Music/Recordings%")
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        val cursor: Cursor? = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                recordingList.add(uri)
            }
        }

        adapter.notifyDataSetChanged()
        recyclerView.visibility = if (recordingList.isNotEmpty()) RecyclerView.VISIBLE else RecyclerView.GONE
    }
}
