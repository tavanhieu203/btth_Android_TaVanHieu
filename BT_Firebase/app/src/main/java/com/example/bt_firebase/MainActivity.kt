package com.example.bt_firebase

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnShowData = findViewById<Button>(R.id.btnShowData)
        val tvData = findViewById<TextView>(R.id.tvData)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                showToast("Nhập đầy đủ thông tin!")
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    saveUser(email)
                    showToast("Đăng ký thành công!")
                }
                .addOnFailureListener { showToast("Đăng ký thất bại!") }
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { showToast("Đăng nhập thành công!") }
                .addOnFailureListener { showToast("Đăng nhập thất bại!") }
        }

        btnShowData.setOnClickListener {
            database.child("users").get()
                .addOnSuccessListener { snapshot ->
                    tvData.text = snapshot.children.joinToString("\n") {
                        it.key + ": " + it.child("email").value.toString()
                    }
                }
                .addOnFailureListener { showToast("Lỗi khi tải dữ liệu!") }
        }
    }

    private fun saveUser(email: String) {
        auth.currentUser?.uid?.let { uid ->
            database.child("users").child(uid).setValue(mapOf("email" to email))
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
