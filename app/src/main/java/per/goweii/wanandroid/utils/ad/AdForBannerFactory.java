package per.goweii.wanandroid.utils.ad;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;

import java.util.List;

import per.goweii.basic.utils.LogUtils;
import per.goweii.wanandroid.BuildConfig;
import per.goweii.wanandroid.utils.SettingUtils;

/**
 * @author CuiZhen
 * @date 2019/12/29
 * GitHub: https://github.com/goweii
 */
public class AdForBannerFactory {
    private final String TAG = AdForBannerFactory.class.getSimpleName();

    private final OnADLoadedListener mOnADLoadedListener;
    private NativeExpressADView mADView = null;

    public static AdForBannerFactory create(Context context, @NonNull OnADLoadedListener listener) {
        if (!AdUtils.isShowAd()) {
            return null;
        }
        return new AdForBannerFactory(context, listener);
    }

    private AdForBannerFactory(Context context, OnADLoadedListener listener) {
        mOnADLoadedListener = listener;
        final String posId;
        if (SettingUtils.getInstance().isDarkTheme()) {
            posId = BuildConfig.AD_ID_BANNER_DARK;
        } else {
            posId = BuildConfig.AD_ID_BANNER_LIGHT;
        }
        final ADSize adSize = new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT);
        NativeExpressAD nativeExpressAD = new NativeExpressAD(context, adSize, BuildConfig.APPID_AD, posId, new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onNoAD(AdError error) {
                LogUtils.i(TAG, String.format("onADError, error code: %d, error msg: %s", error.getErrorCode(), error.getErrorMsg()));
            }

            @Override
            public void onADLoaded(List<NativeExpressADView> adList) {
                LogUtils.i(TAG, "onADLoaded: " + adList.size());
                onLoaded(adList);
            }

            @Override
            public void onRenderFail(NativeExpressADView adView) {
                LogUtils.i(TAG, "onRenderFail");
            }

            @Override
            public void onRenderSuccess(NativeExpressADView adView) {
                LogUtils.i(TAG, "onRenderSuccess");
            }

            @Override
            public void onADExposure(NativeExpressADView adView) {
                LogUtils.i(TAG, "onADExposure");
            }

            @Override
            public void onADClicked(NativeExpressADView adView) {
                LogUtils.i(TAG, "onADClicked");
            }

            @Override
            public void onADClosed(NativeExpressADView adView) {
                LogUtils.i(TAG, "onADClosed");
            }

            @Override
            public void onADLeftApplication(NativeExpressADView adView) {
                LogUtils.i(TAG, "onADLeftApplication");
            }

            @Override
            public void onADOpenOverlay(NativeExpressADView adView) {
                LogUtils.i(TAG, "onADOpenOverlay");
            }

            @Override
            public void onADCloseOverlay(NativeExpressADView adView) {
                LogUtils.i(TAG, "onADCloseOverlay");
            }
        });
        nativeExpressAD.loadAD(1);
    }

    private void onLoaded(List<NativeExpressADView> adList) {
        if (mADView != null) {
            mADView.destroy();
        }
        mADView = adList.get(0);
        mOnADLoadedListener.onLoaded(mADView);
    }

    @Nullable
    public NativeExpressADView getADView() {
        return mADView;
    }

    public void destroy() {
        if (mADView != null) {
            mADView.destroy();
        }
    }

    public interface OnADLoadedListener {
        void onLoaded(@NonNull NativeExpressADView adView);
    }
}
