package com.keysersoze.yumyard

import android.app.Application
import com.keysersoze.yumyard.fcm.FcmTokenManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class YumYard : Application() {
    @Inject
    lateinit var fcmTokenManager: FcmTokenManager
}