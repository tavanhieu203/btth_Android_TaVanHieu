package com.example.btth4

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button
    private lateinit var btnShow: Button
    private lateinit var txtDisplay: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        btnSave = findViewById(R.id.btnSave)
        btnDelete = findViewById(R.id.btnDelete)
        btnShow = findViewById(R.id.btnShow)
        txtDisplay = findViewById(R.id.txtDisplay)

        sharedPreferences = getSharedPreferences("Users", MODE_PRIVATE)

        btnSave.setOnClickListener {
            val username = edtUsername.text.toString()
            val password = edtPassword.text.toString()
            with(sharedPreferences.edit()) {
                putString("USERNAME", username)
                putString("PASSWORD", password)
                apply()
            }
        }

        btnDelete.setOnClickListener {
            with(sharedPreferences.edit()) {
                clear()
                apply()
            }
        }

        btnShow.setOnClickListener {
            val username = sharedPreferences.getString("USERNAME", "No username found")
            val password = sharedPreferences.getString("PASSWORD", "No password found")
            txtDisplay.text = "Username: $username, Password: $password"
        }
    }
}