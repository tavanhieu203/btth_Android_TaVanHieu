package com.example.bt_sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "ContactsDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE Contacts (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Contacts")
        onCreate(db)
    }

    fun insertContact(name: String, phone: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("name", name)
        values.put("phone", phone)
        val result = db.insert("Contacts", null, values)
        db.close()
        return result != -1L
    }

    fun updateContact(id: Int, name: String, phone: String) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("name", name)
        values.put("phone", phone)
        db.update("Contacts", values, "id=?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteContact(id: Int) {
        val db = writableDatabase
        db.delete("Contacts", "id=?", arrayOf(id.toString()))
        db.close()
    }

    fun getAllContacts(): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Contacts", null)
        val result = StringBuilder()
        while (cursor.moveToNext()) {
            result.append("ID: ").append(cursor.getInt(0)).append(", Name: ").append(cursor.getString(1))
                .append(", Phone: ").append(cursor.getString(2)).append("\n")
        }
        cursor.close()
        db.close()
        return result.toString()
    }
}