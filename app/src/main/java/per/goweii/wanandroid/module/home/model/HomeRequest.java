package per.goweii.wanandroid.module.home.model;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.util.List;

import per.goweii.rxhttp.core.RxLife;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.http.WanCache;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.module.main.model.ArticleListBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
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

    public static void getArticleList(RxLife rxLife, boolean refresh, @IntRange(from = 0) int page, @NonNull RequestListener<ArticleListBean> listener) {
        if (page == 0) {
            cacheAndNetBean(rxLife,
                    WanApi.api().getArticleList(page),
                    refresh,
                    WanCache.CacheKey.ARTICLE_LIST(page),
                    ArticleListBean.class,
                    listener);
        } else {
            rxLife.add(request(WanApi.api().getArticleList(page), listener));
        }
    }

    public static void getTopArticleList(RxLife rxLife, boolean refresh, @NonNull RequestListener<List<ArticleBean>> listener) {
        cacheAndNetList(rxLife,
                WanApi.api().getTopArticleList(),
                refresh,
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

    public static void searchCache(@IntRange(from = 0) int page, String key, @NonNull RequestListener<ArticleListBean> listener) {
        cacheBean(WanCache.CacheKey.SEARCH(key, page),
                ArticleListBean.class,
                listener);
    }

    public static void search(RxLife rxLife, boolean refresh, @IntRange(from = 0) int page, String key, @NonNull RequestListener<ArticleListBean> listener) {
        if (page == 0) {
            cacheAndNetBean(rxLife,
                    WanApi.api().search(page, key),
                    refresh,
                    WanCache.CacheKey.SEARCH(key, page),
                    ArticleListBean.class,
                    listener);
        } else {
            rxLife.add(request(WanApi.api().search(page, key), listener));
        }
    }

}
