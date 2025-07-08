package com.keysersoze.yumyard.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject

class FcmTokenManager @Inject constructor() {
    init {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("@@@FCM", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                Log.d("@@@FCM", "FCM Token: $token")
            }
    }
}