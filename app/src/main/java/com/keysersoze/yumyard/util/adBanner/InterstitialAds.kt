package com.keysersoze.yumyard.util.adBanner

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.keysersoze.yumyard.BuildConfig
import com.keysersoze.yumyard.R

fun loadInterstitialAd(context: Context, onLoaded: (InterstitialAd?) -> Unit) {
    val adUnitId = if (BuildConfig.DEBUG)
        "ca-app-pub-3940256099942544/1033173712"
    else
        context.getString(R.string.interstitial_ad_unit_id)

    val adRequest = AdRequest.Builder().build()
    InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
        override fun onAdLoaded(ad: InterstitialAd) {
            Log.d("@@@Interstitial", "Ad loaded")
            onLoaded(ad)
        }
        override fun onAdFailedToLoad(adError: LoadAdError) {
            Log.e("@@@Interstitial", "Failed: ${adError.message}")
            onLoaded(null)
        }
    })
}