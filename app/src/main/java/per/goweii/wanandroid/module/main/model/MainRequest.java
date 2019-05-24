package per.goweii.wanandroid.module.main.model;

import android.support.annotation.NonNull;

import io.reactivex.disposables.Disposable;
import per.goweii.rxhttp.core.RxLife;
import per.goweii.rxhttp.request.base.BaseBean;
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
public class MainRequest extends BaseRequest {

    public static Disposable collect(int id, @NonNull RequestListener<BaseBean> listener) {
        return request(WanApi.api().collect(id), listener);
    }

    public static Disposable collect(String title, String author, String link, @NonNull RequestListener<CollectArticleBean> listener) {
        return request(WanApi.api().collect(title, author, link), listener);
    }

    public static Disposable collect(String title, String link, @NonNull RequestListener<CollectionLinkBean> listener) {
        return request(WanApi.api().collect(title, link), listener);
    }

    public static Disposable uncollect(int id, @NonNull RequestListener<BaseBean> listener) {
        return request(WanApi.api().uncollect(id), listener);
    }

    public static Disposable uncollectLink(int id, @NonNull RequestListener<BaseBean> listener) {
        return request(WanApi.api().uncollectLink(id), listener);
    }

    public static Disposable uncollect(int id, int originId, @NonNull RequestListener<BaseBean> listener) {
        return request(WanApi.api().uncollect(id, originId), listener);
    }

    public static void update(RxLife rxLife, @NonNull RequestListener<UpdateBean> listener) {
        cacheAndNetBean(rxLife,
                WanApi.api().update(),
                WanCache.CacheKey.UPDATE,
                UpdateBean.class,
                listener);
    }

}
