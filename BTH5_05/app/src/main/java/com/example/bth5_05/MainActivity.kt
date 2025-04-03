package com.example.bth5_05

import android.os.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvTimeElapsed: TextView
    private var secondsElapsed = 0
    private var isRunning = true

    // Tạo Handler để xử lý cập nhật UI
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            tvTimeElapsed.text = "Thời gian: ${msg.arg1} giây"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTimeElapsed = findViewById(R.id.tvTimeElapsed)

        // Tạo Thread chạy nền
        val thread = Thread {
            while (isRunning) {
                try {
                    Thread.sleep(1000) // Đợi 1 giây
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                secondsElapsed++

                // Gửi message đến Handler để cập nhật UI
                val message = handler.obtainMessage()
                message.arg1 = secondsElapsed
                handler.sendMessage(message)
            }
        }
        thread.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false // Dừng vòng lặp khi Activity bị hủy
    }
}