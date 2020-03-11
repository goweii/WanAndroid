package per.goweii.wanandroid.module.mine.model;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.util.List;

import io.reactivex.disposables.Disposable;
import per.goweii.rxhttp.core.RxLife;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.http.WanCache;
import per.goweii.wanandroid.module.main.model.ArticleListBean;
import per.goweii.wanandroid.module.main.model.CollectionLinkBean;
import per.goweii.wanandroid.module.main.model.UserPageBean;

/**
 * @author CuiZhen
 * @date 2019/5/16
 * GitHub: https://github.com/goweii
 */
public class MineRequest extends BaseRequest {

    public static void getCollectArticleListCache(@IntRange(from = 0) int page, @NonNull RequestListener<ArticleListBean> listener) {
        cacheBean(WanCache.CacheKey.COLLECT_ARTICLE_LIST(page), ArticleListBean.class, listener);
    }

    public static void getCollectArticleListNet(RxLife rxLife, @IntRange(from = 0) int page, @NonNull RequestListener<ArticleListBean> listener) {
        rxLife.add(request(WanApi.api().getCollectArticleList(page), listener));
    }

    public static void getCollectArticleList(RxLife rxLife, boolean removeAndRefresh, @IntRange(from = 0) int page, @NonNull RequestListener<ArticleListBean> listener) {
        if (page == 0) {
            cacheAndNetBean(rxLife,
                    WanApi.api().getCollectArticleList(page),
                    removeAndRefresh,
                    WanCache.CacheKey.COLLECT_ARTICLE_LIST(page),
                    ArticleListBean.class,
                    listener);
        } else {
            rxLife.add(request(WanApi.api().getCollectArticleList(page), listener));
        }
    }

    public static void getCollectLinkListCache(@NonNull RequestListener<List<CollectionLinkBean>> listener) {
        cacheList(WanCache.CacheKey.COLLECT_LINK_LIST(), CollectionLinkBean.class, listener);
    }

    public static void getCollectLinkListNet(RxLife rxLife, @NonNull RequestListener<List<CollectionLinkBean>> listener) {
        rxLife.add(request(WanApi.api().getCollectLinkList(), listener));
    }

    public static void getCollectLinkList(RxLife rxLife, boolean removeAndRefresh, @NonNull RequestListener<List<CollectionLinkBean>> listener) {
        cacheAndNetList(rxLife,
                WanApi.api().getCollectLinkList(),
                removeAndRefresh,
                WanCache.CacheKey.COLLECT_LINK_LIST(),
                CollectionLinkBean.class,
                listener);
    }

    public static Disposable updateCollectLink(int id, String name, String link, @NonNull RequestListener<CollectionLinkBean> listener) {
        return request(WanApi.api().updateCollectLink(id, name, link), listener);
    }

    public static Disposable logout(@NonNull RequestListener<BaseBean> listener) {
        return request(WanApi.api().logout(), listener);
    }

    public static void getAboutMe(RxLife rxLife, @NonNull RequestListener<AboutMeBean> listener) {
        cacheAndNetBean(rxLife,
                WanApi.api().getAboutMe(),
                WanCache.CacheKey.ABOUT_ME,
                AboutMeBean.class,
                listener);
    }

    public static Disposable getCoin(@NonNull RequestListener<Integer> listener) {
        return request(WanApi.api().getCoin(), listener);
    }

    public static Disposable getUserInfo(@NonNull RequestListener<UserInfoBean> listener) {
        return request(WanApi.api().getUserInfo(), listener);
    }

    public static Disposable getCoinRecordList(int page, @NonNull RequestListener<CoinRecordBean> listener) {
        return request(WanApi.api().getCoinRecordList(page), listener);
    }

    public static Disposable getCoinRankList(int page, @NonNull RequestListener<CoinRankBean> listener) {
        return request(WanApi.api().getCoinRankList(page), listener);
    }

    public static void getMineShareArticleListCache(@IntRange(from = 1) int page, @NonNull RequestListener<ArticleListBean> listener) {
        cacheBean(WanCache.CacheKey.MINE_SHARE_ARTICLE_LIST(page), ArticleListBean.class, listener);
    }

    public static void getMineShareArticleListNet(RxLife rxLife, @IntRange(from = 1) int page, @NonNull RequestListener<UserPageBean> listener) {
        rxLife.add(request(WanApi.api().getMineShareArticleList(page), listener));
    }

    public static void getMineShareArticleList(RxLife rxLife, boolean removeAndRefresh, @IntRange(from = 1) int page, @NonNull RequestListener<UserPageBean> listener) {
        if (page == 1) {
            cacheAndNetBean(rxLife,
                    WanApi.api().getMineShareArticleList(page),
                    removeAndRefresh,
                    WanCache.CacheKey.MINE_SHARE_ARTICLE_LIST(page),
                    UserPageBean.class,
                    listener);
        } else {
            rxLife.add(request(WanApi.api().getMineShareArticleList(page), listener));
        }
    }

    public static Disposable deleteMineShareArticle(int id, @NonNull RequestListener<BaseBean> listener) {
        return request(WanApi.api().deleteMineShareArticle(id), listener);
    }
}
