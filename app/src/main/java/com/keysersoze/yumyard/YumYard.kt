package com.keysersoze.yumyard

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.keysersoze.yumyard.fcm.FcmTokenManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class YumYard : Application() {
    @Inject
    lateinit var fcmTokenManager: FcmTokenManager

    override fun onCreate() {
        super.onCreate()
        val requestConfiguration = RequestConfiguration.Builder()
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_PG)
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        MobileAds.initialize(this)
    }
}