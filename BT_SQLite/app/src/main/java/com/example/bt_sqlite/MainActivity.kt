package com.example.bt_sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var nameInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var resultView: TextView
    private lateinit var btnAdd: Button
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button
    private lateinit var btnShow: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DBHelper(this)
        nameInput = findViewById(R.id.nameInput)
        phoneInput = findViewById(R.id.phoneInput)
        resultView = findViewById(R.id.resultView)
        btnAdd = findViewById(R.id.addButton)
        btnEdit = findViewById(R.id.updateButton)
        btnDelete = findViewById(R.id.deleteButton)
        btnShow = findViewById(R.id.displayButton)

        btnAdd.setOnClickListener {
            val success = dbHelper.insertContact(nameInput.text.toString(), phoneInput.text.toString())
            if (success) {
                Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show()
            }
        }

        btnEdit.setOnClickListener {
            dbHelper.updateContact(2, nameInput.text.toString(), phoneInput.text.toString())
            displayContacts()
        }

        btnDelete.setOnClickListener {
            dbHelper.deleteContact(2)
            displayContacts()
        }

        btnShow.setOnClickListener {
            displayContacts()
        }
    }

    private fun displayContacts() {
        resultView.text = dbHelper.getAllContacts()
    }
}
