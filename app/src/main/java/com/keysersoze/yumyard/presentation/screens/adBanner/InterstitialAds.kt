package com.keysersoze.yumyard.presentation.screens.adBanner

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

fun loadInterstitialAd(context: Context, onLoaded: (InterstitialAd?) -> Unit) {
    val adRequest = AdRequest.Builder().build()
    InterstitialAd.load(
        context,
        "ca-app-pub-3940256099942544/1033173712", // Test ID
        adRequest,
        object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                Log.d("@@@Interstitial", "Ad was loaded.")
                onLoaded(ad)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("@@@Interstitial", "Failed to load ad: ${adError.message}")
                onLoaded(null)
            }
        }
    )
}