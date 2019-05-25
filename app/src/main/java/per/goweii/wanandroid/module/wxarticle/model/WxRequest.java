package per.goweii.wanandroid.module.wxarticle.model;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.List;

import per.goweii.rxhttp.core.RxLife;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.http.WanCache;

/**
 * @author CuiZhen
 * @date 2019/5/16
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WxRequest extends BaseRequest {

    public static void getWxArticleChapters(RxLife rxLife, @NonNull RequestListener<List<WxChapterBean>> listener) {
        cacheAndNetList(rxLife,
                WanApi.api().getWxArticleChapters(),
                WanCache.CacheKey.WXARTICLE_CHAPTERS,
                WxChapterBean.class,
                listener);
    }

    public static void getWxArticleList(RxLife rxLife, boolean refresh, int id, @IntRange(from = 1) int page, @NonNull RequestListener<WxArticleBean> listener) {
        cacheAndNetBean(rxLife,
                WanApi.api().getWxArticleList(id, page),
                refresh,
                WanCache.CacheKey.WXARTICLE_LIST(id, page),
                WxArticleBean.class,
                listener);
    }

    public static void getWxArticleList(RxLife rxLife, boolean refresh, int id, @IntRange(from = 1) int page, String key, @NonNull RequestListener<WxArticleBean> listener) {
        cacheAndNetBean(rxLife,
                WanApi.api().getWxArticleList(id, page, key),
                refresh,
                WanCache.CacheKey.WXARTICLE_LIST_SEARCH(id, page, key),
                WxArticleBean.class,
                listener);
    }

}
