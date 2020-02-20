package per.goweii.wanandroid.utils.ad;

import android.content.Context;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import per.goweii.basic.utils.LogUtils;
import per.goweii.wanandroid.BuildConfig;
import per.goweii.wanandroid.module.main.adapter.ArticleAdapter;
import per.goweii.wanandroid.utils.SettingUtils;

/**
 * @author CuiZhen
 * @date 2019/12/29
 * GitHub: https://github.com/goweii
 */
public class AdForListFactory {
    private final String TAG = AdForListFactory.class.getSimpleName();

    private final ArticleAdapter mAdapter;
    private final NativeExpressAD mNativeExpressAD;
    private final Queue<NativeExpressADView> mADCacheList = new LinkedBlockingDeque<>();

    public static AdForListFactory create(Context context, ArticleAdapter adapter) {
        if (!AdUtils.isShowAd()) {
            return null;
        }
        return new AdForListFactory(context, adapter);
    }

    private AdForListFactory(Context context, ArticleAdapter adapter) {
        this.mAdapter = adapter;
        final String posId;
        if (SettingUtils.getInstance().isDarkTheme()) {
            posId = BuildConfig.AD_ID_ARTICLE_LIST_DARK;
        } else {
            posId = BuildConfig.AD_ID_ARTICLE_LIST_LIGHT;
        }
        final ADSize adSize = new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT);
        mNativeExpressAD = new NativeExpressAD(context, adSize, BuildConfig.APPID_AD, posId, new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onNoAD(AdError error) {
                LogUtils.i(TAG, String.format("onADError, error code: %d, error msg: %s", error.getErrorCode(), error.getErrorMsg()));
                loading = false;
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
        mNativeExpressAD.loadAD(6);
        adapter.setPageLoadedCallback(new ArticleAdapter.PageLoadedCallback() {
            @Override
            public void pageLoaded(int startPos, List<? super MultiItemEntity> pageData) {
                if (pageData.size() < 10) {
                    int pagePos = pageData.size() / 2;
                    NativeExpressADView adView = mADCacheList.poll();
                    AdEntity adEntity = new AdEntity(startPos + pagePos, adView);
                    pageData.add(pagePos, adEntity);
                } else {
                    int adCount = 0;
                    for (int i = 0; i < pageData.size(); i++) {
                        if (i == 3 + 5 * adCount) {
                            adCount++;
                            NativeExpressADView adView = mADCacheList.poll();
                            AdEntity adEntity = new AdEntity(startPos + i, adView);
                            pageData.add(i, adEntity);
                        }
                    }
                }
                loadAD();
            }
        });
    }

    private boolean loading = false;

    private void loadAD() {
        if (mADCacheList.size() < 6) {
            if (loading) {
                return;
            }
            loading = true;
            mNativeExpressAD.loadAD(6);
        }
    }

    private void onLoaded(List<NativeExpressADView> adList) {
        loading = false;
        for (NativeExpressADView adView : adList) {
            mADCacheList.offer(adView);
        }
        for (int i = 0; i < mAdapter.getData().size(); i++) {
            MultiItemEntity entity = mAdapter.getData().get(i);
            if (entity != null && entity.getItemType() == ArticleAdapter.ITEM_TYPE_AD) {
                AdEntity adEntity = (AdEntity) entity;
                if (adEntity.getView() == null) {
                    NativeExpressADView adView = mADCacheList.poll();
                    if (adView != null) {
                        adEntity.setView(adView);
                        mAdapter.notifyItemChanged(i + mAdapter.getHeaderLayoutCount());
                    }
                }
            }
        }
        loadAD();
    }

    public void destroy() {
        for (int i = 0; i < mAdapter.getData().size(); i++) {
            MultiItemEntity entity = mAdapter.getData().get(i);
            if (entity != null && entity.getItemType() == ArticleAdapter.ITEM_TYPE_AD) {
                AdEntity adEntity = (AdEntity) entity;
                if (adEntity.getView() != null) {
                    adEntity.getView().destroy();
                }
            }
        }
    }
}
