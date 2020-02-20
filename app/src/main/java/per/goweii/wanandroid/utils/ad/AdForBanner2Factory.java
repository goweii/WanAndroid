package per.goweii.wanandroid.utils.ad;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.comm.util.AdError;

import per.goweii.wanandroid.BuildConfig;
import per.goweii.wanandroid.utils.ad.widget.AdContainer;

/**
 * @author CuiZhen
 * @date 2019/12/29
 * GitHub: https://github.com/goweii
 */
public class AdForBanner2Factory {
    private AdContainer adContainer;
    private UnifiedBannerView bannerView;

    public static AdForBanner2Factory create(Activity activity, @NonNull AdContainer adContainer) {
        if (!AdUtils.isShowAd()) {
            adContainer.setVisibility(View.GONE);
            return null;
        }
        adContainer.setVisibility(View.VISIBLE);
        return new AdForBanner2Factory(activity, adContainer);
    }

    private AdForBanner2Factory(Activity activity, @NonNull AdContainer adContainer) {
        adContainer.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                destroy();
            }
        });
        this.adContainer = adContainer;
        adContainer.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                destroy();
            }
        });
        final String posId = BuildConfig.AD_ID_BANNER;
        bannerView = new UnifiedBannerView(activity, BuildConfig.APPID_AD, posId, new UnifiedBannerADListener() {
            @Override
            public void onNoAD(AdError adError) {
            }

            @Override
            public void onADReceive() {
            }

            @Override
            public void onADExposure() {
            }

            @Override
            public void onADClosed() {
                destroy();
                adContainer.setVisibility(View.GONE);
            }

            @Override
            public void onADClicked() {
            }

            @Override
            public void onADLeftApplication() {
            }

            @Override
            public void onADOpenOverlay() {
            }

            @Override
            public void onADCloseOverlay() {
            }
        });
        adContainer.addView(bannerView);
        bannerView.loadAD();
    }

    public void destroy() {
        adContainer.removeAllViews();
        if (bannerView != null) {
            bannerView.destroy();
        }
    }
}
