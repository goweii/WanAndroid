package per.goweii.wanandroid.module.main.model;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import io.reactivex.disposables.Disposable;
import per.goweii.rxhttp.core.RxLife;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestCallback;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.http.WanCache;

/**
 * @author CuiZhen
 * @date 2019/5/16
 * GitHub: https://github.com/goweii
 */
public class MainRequest extends BaseRequest {

    public static Disposable collect(int id, @NonNull RequestListener<BaseBean> listener) {
        return request(WanApi.api().collect(id), listener);
    }

    public static Disposable collect(String title, String author, String link, @NonNull RequestListener<ArticleBean> listener) {
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
        rxLife.add(request(WanApi.api().update(), listener));
    }

    public static void getConfig(RxLife rxLife, @NonNull RequestListener<ConfigBean> listener) {
        rxLife.add(request(WanApi.api().getConfig(), listener));
    }

    public static void getUserArticleListCache(@IntRange(from = 0) int page, @NonNull RequestListener<ArticleListBean> listener) {
        cacheBean(WanCache.CacheKey.USER_ARTICLE_LIST(page),
                ArticleListBean.class,
                listener);
    }

    public static void getUserArticleList(RxLife rxLife, boolean refresh, @IntRange(from = 0) int page, @NonNull RequestListener<ArticleListBean> listener) {
        if (page == 0) {
            cacheAndNetBean(rxLife,
                    WanApi.api().getUserArticleList(page),
                    refresh,
                    WanCache.CacheKey.USER_ARTICLE_LIST(page),
                    ArticleListBean.class,
                    listener);
        } else {
            rxLife.add(request(WanApi.api().getUserArticleList(page), listener));
        }
    }

    public static void getUserPage(RxLife rxLife, boolean refresh, int userId, @IntRange(from = 1) int page, @NonNull RequestListener<UserPageBean> listener) {
        if (page == 1) {
            cacheAndNetBean(rxLife,
                    WanApi.api().getUserPage(userId, page),
                    refresh,
                    WanCache.CacheKey.USER_PAGE(userId, page),
                    UserPageBean.class,
                    listener);
        } else {
            rxLife.add(request(WanApi.api().getUserPage(userId, page), listener));
        }
    }

    public static Disposable shareArticle(String title, String link, @NonNull RequestListener<BaseBean> listener) {
        return request(WanApi.api().shareArticle(title, link), listener);
    }

    public static void getJinrishici(RxLife rxLife, @NonNull RequestListener<JinrishiciBean> listener) {
        getJinrishiciToken(rxLife, new RequestCallback<String>() {
            @Override
            public void onSuccess(int code, String data) {
                request(WanApi.api().getJinrishici(data), listener);
            }

            @Override
            public void onFailed(int code, String msg) {
                listener.onFailed(code, msg);
            }
        });
    }

    private static void getJinrishiciToken(RxLife rxLife, @NonNull RequestListener<String> listener) {
        cacheBean(WanCache.CacheKey.JINRISHICI_TOKEN, String.class, new RequestCallback<String>() {
            @Override
            public void onSuccess(int code, String data) {
                if (TextUtils.isEmpty(data)) {
                    rxLife.add(request(WanApi.api().getJinrishiciToken(), listener));
                } else {
                    listener.onSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                rxLife.add(request(WanApi.api().getJinrishiciToken(), listener));
            }
        });
    }

}
