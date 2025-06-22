package per.goweii.component.ad

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import per.goweii.basic.utils.LogUtils
import per.goweii.basic.utils.listener.SimpleCallback

class NativeAdProvider(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val adUnitID: String,
) {
    companion object {
        private const val TAG = "NativeAdProvider"

        private const val TEST_ID = "ca-app-pub-3940256099942544/2247696110"

        fun test(context: Context, lifecycleOwner: LifecycleOwner): NativeAdProvider {
            return NativeAdProvider(
                context = context,
                lifecycleOwner = lifecycleOwner,
                adUnitID = TEST_ID,
            )
        }
    }

    private var ad: NativeAd? = null

    private var onLoaded: SimpleCallback<NativeAd>? = null

    init {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                ad?.destroy()
                ad = null
            }
        })
    }

    fun load(listener: SimpleCallback<NativeAd>) {
        if (ad != null) {
            listener.onResult(ad!!)
            return
        }
        onLoaded = listener
        val adLoader = AdLoader.Builder(context, adUnitID)
            .forNativeAd { ad: NativeAd ->
                LogUtils.d(TAG, ad)
                if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
                    ad.destroy()
                    return@forNativeAd
                }
                this.ad = ad
                if (onLoaded != null) {
                    listener.onResult(ad)
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    LogUtils.d(TAG, adError)
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .build()
            )
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
        LogUtils.d(TAG, "start load ad")
    }
}