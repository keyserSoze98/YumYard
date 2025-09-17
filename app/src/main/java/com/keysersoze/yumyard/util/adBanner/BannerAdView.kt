package com.keysersoze.yumyard.util.adBanner

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.keysersoze.yumyard.BuildConfig
import com.keysersoze.yumyard.R

@Composable
fun BannerAdView(
    adUnitId: String = if (BuildConfig.DEBUG)
        "ca-app-pub-3940256099942544/6300978111"
    else
        stringResource(R.string.banner_ad_unit_id)
) {
    AndroidView(factory = { context ->
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            setAdUnitId(adUnitId)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.d("@@@BannerAd", "Ad loaded successfully")
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("@@@BannerAd", "Ad failed: ${error.message}")
                }
            }
            loadAd(AdRequest.Builder().build())
        }
    })
}