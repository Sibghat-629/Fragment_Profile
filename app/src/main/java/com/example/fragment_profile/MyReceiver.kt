package com.example.fragment_profile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyReceiver(private val onStatus: (String) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val msg = when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> "Power Connected"
            Intent.ACTION_POWER_DISCONNECTED -> "Power Disconnected"
            "com.example.fragment_profile.TEST_BROADCAST" -> "Test wala  Broadcast Triggered!"
            else -> "  Broadcast: ${intent.action}"
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        onStatus(msg)
    }
}
