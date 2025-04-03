package com.example.bth5_02

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                Log.d("CallReceiver", "Cuộc gọi đến từ: $phoneNumber")
            } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                // Khi cuộc gọi kết thúc, gửi tin nhắn SMS
                phoneNumber?.let {
                    sendSMS(it, "Xin lỗi, tôi đang bận. Tôi sẽ gọi lại sau.")
                }
            }
        }
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Log.d("CallReceiver", "Tin nhắn đã được gửi đến: $phoneNumber")
        } catch (e: Exception) {
            Log.e("CallReceiver", "Lỗi khi gửi tin nhắn: ${e.message}")
        }
    }
}