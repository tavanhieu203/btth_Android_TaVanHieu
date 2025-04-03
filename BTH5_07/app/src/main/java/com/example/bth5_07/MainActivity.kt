package com.example.bth5_07

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.VideoView
import android.widget.MediaController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var edtVideoUrl: EditText
    private val PICK_VIDEO_REQUEST = 1
    private var currentPosition = 0  // Lưu vị trí video

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoView = findViewById(R.id.videoView)
        edtVideoUrl = findViewById(R.id.edtVideoUrl)
        val btnSelectVideo: Button = findViewById(R.id.btnSelectVideo)
        val btnPlayUrl: Button = findViewById(R.id.btnPlayUrl)

        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        // Xử lý lỗi khi phát video
        videoView.setOnErrorListener { _, _, _ ->
            Toast.makeText(this, "Lỗi khi phát video!", Toast.LENGTH_SHORT).show()
            true
        }

        // Chọn video từ thiết bị (yêu cầu quyền nếu cần)
        btnSelectVideo.setOnClickListener {
            if (checkStoragePermission()) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, PICK_VIDEO_REQUEST)
            } else {
                requestStoragePermission()
            }
        }

        // Phát video từ URL
        btnPlayUrl.setOnClickListener {
            val videoUrl = edtVideoUrl.text.toString().trim()
            if (videoUrl.isNotEmpty()) {
                playVideo(Uri.parse(videoUrl))
            } else {
                Toast.makeText(this, "Vui lòng nhập URL hợp lệ!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedVideoUri = data.data
            if (selectedVideoUri != null) {
                playVideo(selectedVideoUri)
            }
        }
    }

    private fun playVideo(videoUri: Uri) {
        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener {
            videoView.seekTo(currentPosition)
            videoView.start()
        }
    }

    // Xử lý vòng đời của VideoView
    override fun onPause() {
        super.onPause()
        currentPosition = videoView.currentPosition
        videoView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.suspend()
    }

    // Kiểm tra quyền đọc bộ nhớ
    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true // Android 13+ không cần quyền READ_EXTERNAL_STORAGE
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Yêu cầu quyền đọc bộ nhớ
    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }
    }
}
