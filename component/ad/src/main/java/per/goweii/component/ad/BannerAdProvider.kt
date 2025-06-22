package per.goweii.component.ad

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import per.goweii.basic.utils.LogUtils


class BannerAdProvider(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val adUnitId: String,
) {
    companion object {
        private const val TAG = "BannerAdProvider"

        private const val TEST_ID = "ca-app-pub-3940256099942544/9214589741"

        fun test(context: Context, lifecycleOwner: LifecycleOwner): NativeAdProvider {
            return NativeAdProvider(
                context = context,
                lifecycleOwner = lifecycleOwner,
                adUnitID = TEST_ID,
            )
        }
    }

    private var adView: AdView? = null

    init {
        if (lifecycleOwner.lifecycle.currentState != Lifecycle.State.DESTROYED) {
            val adView = AdView(context)
            adView.adUnitId = adUnitId
            adView.setAdSize(AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(context, 320))

            adView.adListener = object : AdListener() {
                override fun onAdClicked() {
                    LogUtils.d(TAG, "onAdClicked")
                }

                override fun onAdClosed() {
                    LogUtils.d(TAG, "onAdClosed")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    LogUtils.d(TAG, adError)
                }

                override fun onAdImpression() {
                    LogUtils.d(TAG, "onAdImpression")
                }

                override fun onAdLoaded() {
                    LogUtils.d(TAG, "onAdLoaded")
                }

                override fun onAdOpened() {
                    LogUtils.d(TAG, "onAdOpened")
                }
            }

            this.adView = adView

            val adRequest = AdManagerAdRequest.Builder().build()
            adView.loadAd(adRequest)
        }

        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                detachFromContainer()
                adView?.destroy()
                adView = null
            }
        })
    }

    fun attachToContainer(adContainerView: FrameLayout) {
        if (adView == null) return
        detachFromContainer()
        adContainerView.removeAllViews()
        adContainerView.addView(adView!!)
    }

    private fun detachFromContainer() {
        if (adView == null) return
        val parent = adView!!.parent
        if (parent is ViewGroup) {
            parent.removeView(adView)
        }
    }
}