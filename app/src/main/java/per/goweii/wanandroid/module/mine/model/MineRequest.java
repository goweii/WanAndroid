package per.goweii.wanandroid.module.mine.model;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.disposables.Disposable;
import per.goweii.rxhttp.core.RxLife;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.http.WanCache;
import per.goweii.wanandroid.module.main.model.CollectionLinkBean;

/**
 * @author CuiZhen
 * @date 2019/5/16
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class MineRequest extends BaseRequest {

    public static void getCollectArticleList(RxLife rxLife, boolean removeAndRefresh, int page, @NonNull RequestListener<CollectionArticleBean> listener) {
        cacheAndNetBean(rxLife,
                WanApi.api().getCollectArticleList(page),
                removeAndRefresh,
                WanCache.CacheKey.COLLECT_ARTICLE_LIST(page),
                CollectionArticleBean.class,
                listener);
    }

    public static void getCollectLinkList(RxLife rxLife, boolean removeAndRefresh, @NonNull RequestListener<List<CollectionLinkBean>> listener) {
        cacheAndNetList(rxLife,
                WanApi.api().getCollectLinkList(),
                removeAndRefresh,
                WanCache.CacheKey.COLLECT_LINK_LIST,
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
}
