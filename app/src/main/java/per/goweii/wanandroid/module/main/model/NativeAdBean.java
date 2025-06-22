package per.goweii.wanandroid.module.main.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.android.gms.ads.nativead.NativeAd;

import per.goweii.wanandroid.module.main.adapter.ArticleAdapter;

public class NativeAdBean implements MultiItemEntity {
    private NativeAd nativeAd;

    public NativeAdBean(NativeAd nativeAd) {
        this.nativeAd = nativeAd;
    }

    public void setNativeAd(NativeAd nativeAd) {
        this.nativeAd = nativeAd;
    }

    public NativeAd getNativeAd() {
        return nativeAd;
    }

    @Override
    public int getItemType() {
        return ArticleAdapter.ITEM_TYPE_AD;
    }
}
