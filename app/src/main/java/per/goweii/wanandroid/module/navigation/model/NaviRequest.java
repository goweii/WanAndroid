package per.goweii.wanandroid.module.navigation.model;

import android.support.annotation.NonNull;

import java.util.List;

import per.goweii.rxhttp.core.RxLife;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.http.WanCache;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class NaviRequest extends BaseRequest {

    public static void getNaviListCache(@NonNull RequestListener<List<NaviBean>> listener) {
        cacheList(WanCache.CacheKey.NAVI_LIST,
                NaviBean.class,
                listener);
    }

    public static void getNaviList(RxLife rxLife, @NonNull RequestListener<List<NaviBean>> listener) {
        cacheAndNetList(rxLife,
                WanApi.api().getNaviList(),
                true,
                WanCache.CacheKey.NAVI_LIST,
                NaviBean.class,
                listener);
    }

}
