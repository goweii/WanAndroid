package per.goweii.wanandroid.module.home.model;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.List;

import per.goweii.rxhttp.core.RxLife;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.http.WanCache;
import per.goweii.wanandroid.module.main.model.ArticleBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class HomeRequest extends BaseRequest {

    public static void getBanner(RxLife rxLife, @NonNull RequestListener<List<BannerBean>> listener) {
        cacheAndNetList(rxLife,
                WanApi.api().getBanner(),
                WanCache.CacheKey.BANNER,
                BannerBean.class,
                listener);
    }

    public static void getArticleList(RxLife rxLife, @IntRange(from = 0) int page, @NonNull RequestListener<HomeBean> listener) {
        cacheAndNetBean(rxLife,
                WanApi.api().getArticleList(page),
                WanCache.CacheKey.ARTICLE_LIST(page),
                HomeBean.class,
                listener);
    }

    public static void getTopArticleList(RxLife rxLife, @NonNull RequestListener<List<ArticleBean>> listener) {
        cacheAndNetList(rxLife,
                WanApi.api().getTopArticleList(),
                WanCache.CacheKey.TOP_ARTICLE_LIST,
                ArticleBean.class,
                listener);
    }

    public static void getHotKeyList(RxLife rxLife, @NonNull RequestListener<List<HotKeyBean>> listener) {
        cacheAndNetList(rxLife,
                WanApi.api().getHotKeyList(),
                WanCache.CacheKey.HOT_KEY_LIST,
                HotKeyBean.class,
                listener);
    }

    public static void search(RxLife rxLife, @IntRange(from = 0) int page, String key, @NonNull RequestListener<SearchBean> listener) {
        cacheAndNetBean(rxLife,
                WanApi.api().search(page, key),
                WanCache.CacheKey.SEARCH(key, page),
                SearchBean.class,
                listener);
    }

}
