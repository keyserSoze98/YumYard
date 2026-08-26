package com.keysersoze.yumyard.util.adBanner

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
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
            onLoaded(ad)
        }
        override fun onAdFailedToLoad(adError: LoadAdError) {
            onLoaded(null)
        }
    })
}

fun showInterstitialAd(
    ad: InterstitialAd?,
    activity: Activity,
    onDone: () -> Unit,
    onReload: (InterstitialAd?) -> Unit = {}
) {
    if (ad != null) {
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                onDone()
                loadInterstitialAd(activity) { onReload(it) }
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                onDone()
                loadInterstitialAd(activity) { onReload(it) }
            }

            override fun onAdShowedFullScreenContent() {
                onReload(null)
            }
        }
        ad.show(activity)
    } else {
        onDone()
    }
}