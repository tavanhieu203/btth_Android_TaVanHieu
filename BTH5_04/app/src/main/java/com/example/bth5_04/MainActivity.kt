package com.example.bth5_04

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var editTextUrl: EditText
    private lateinit var imageView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var buttonLoad: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextUrl = findViewById(R.id.editTextUrl)
        imageView = findViewById(R.id.imageView)
        progressBar = findViewById(R.id.progressBar)
        buttonLoad = findViewById(R.id.buttonLoad)

        buttonLoad.setOnClickListener {
            val imageUrl = editTextUrl.text.toString().trim()
            if (imageUrl.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập URL!", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.WEB_URL.matcher(imageUrl).matches()) {
                Toast.makeText(this, "URL không hợp lệ!", Toast.LENGTH_SHORT).show()
            } else {
                loadImage(imageUrl)
            }
        }
    }

    private fun loadImage(url: String) {
        progressBar.visibility = View.VISIBLE
        imageView.setImageBitmap(null) // Xóa ảnh cũ trước khi tải ảnh mới

        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = downloadImage(url)
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                } else {
                    imageView.setImageResource(R.drawable.placeholder) // Ảnh mặc định khi lỗi
                    Toast.makeText(this@MainActivity, "Không thể tải ảnh!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun downloadImage(url: String): Bitmap? {
        var input: InputStream? = null
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            input?.close()
        }
    }
}
