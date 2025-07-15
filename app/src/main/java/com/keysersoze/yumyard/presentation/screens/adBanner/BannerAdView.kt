import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun BannerAdView(
    adUnitId: String = "ca-app-pub-2487665338717314/6548721112"  // Test ID: "ca-app-pub-3940256099942544/6300978111"
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
                    Log.e("@@@BannerAd", "Ad failed to load: ${error.message}")
                }
            }
            loadAd(AdRequest.Builder().build())
        }
    })
}