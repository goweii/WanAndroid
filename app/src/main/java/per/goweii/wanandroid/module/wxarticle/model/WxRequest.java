package per.goweii.wanandroid.module.wxarticle.model;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.List;

import per.goweii.rxhttp.core.RxLife;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.http.WanCache;
import per.goweii.wanandroid.module.main.model.ArticleListBean;
import per.goweii.wanandroid.module.main.model.ChapterBean;

/**
 * @author CuiZhen
 * @date 2019/5/16
 * GitHub: https://github.com/goweii
 */
public class WxRequest extends BaseRequest {

    public static void getWxArticleChapters(RxLife rxLife, @NonNull RequestListener<List<ChapterBean>> listener) {
        cacheAndNetList(rxLife,
                WanApi.api().getWxArticleChapters(),
                WanCache.CacheKey.WXARTICLE_CHAPTERS,
                ChapterBean.class,
                listener);
    }

    public static void getWxArticleListCache(int id, @IntRange(from = 1) int page, @NonNull RequestListener<ArticleListBean> listener) {
        cacheBean(WanCache.CacheKey.WXARTICLE_LIST(id, page),
                ArticleListBean.class,
                listener);
    }

    public static void getWxArticleList(RxLife rxLife, boolean refresh, int id, @IntRange(from = 1) int page, @NonNull RequestListener<ArticleListBean> listener) {
        if (page == 1) {
            cacheAndNetBean(rxLife,
                    WanApi.api().getWxArticleList(id, page),
                    refresh,
                    WanCache.CacheKey.WXARTICLE_LIST(id, page),
                    ArticleListBean.class,
                    listener);
        } else {
            rxLife.add(request(WanApi.api().getWxArticleList(id, page), listener));
        }
    }

    public static void getWxArticleList(RxLife rxLife, boolean refresh, int id, @IntRange(from = 1) int page, String key, @NonNull RequestListener<ArticleListBean> listener) {
        if (page == 1) {
            cacheAndNetBean(rxLife,
                    WanApi.api().getWxArticleList(id, page, key),
                    refresh,
                    WanCache.CacheKey.WXARTICLE_LIST_SEARCH(id, page, key),
                    ArticleListBean.class,
                    listener);
        } else {
            rxLife.add(request(WanApi.api().getWxArticleList(id, page, key), listener));
        }
    }

}
