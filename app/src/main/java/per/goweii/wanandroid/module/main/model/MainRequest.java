package per.goweii.wanandroid.module.main.model;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.util.List;

import io.reactivex.disposables.Disposable;
import per.goweii.rxhttp.core.RxLife;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestCallback;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.http.WanCache;
import per.goweii.wanandroid.utils.UserUtils;

/**
 * @author CuiZhen
 * @date 2019/5/16
 * GitHub: https://github.com/goweii
 */
public class MainRequest extends BaseRequest {

    public static Disposable collectArticle(int id, @NonNull RequestListener<BaseBean> listener) {
        return request(WanApi.api().collectArticle(id), listener);
    }

    public static Disposable collectArticle(String title, String author, String link, @NonNull RequestListener<ArticleBean> listener) {
        return request(WanApi.api().collectArticle(title, author, link), listener);
    }

    public static Disposable collectLink(String title, String link, @NonNull RequestListener<CollectionLinkBean> listener) {
        return request(WanApi.api().collectLink(title, link), listener);
    }

    public static Disposable uncollectArticle(int id, @NonNull RequestListener<BaseBean> listener) {
        return request(WanApi.api().uncollectArticle(id), listener);
    }

    public static Disposable uncollectLink(int id, @NonNull RequestListener<BaseBean> listener) {
        return request(WanApi.api().uncollectLink(id), listener);
    }

    public static Disposable uncollectArticle(int id, int originId, @NonNull RequestListener<BaseBean> listener) {
        return request(WanApi.api().uncollectArticle(id, originId), listener);
    }

    public static void update(RxLife rxLife, @NonNull RequestListener<UpdateBean> listener) {
        rxLife.add(request(WanApi.api().update(), listener));
    }

    public static void betaUpdate(RxLife rxLife, @NonNull RequestListener<UpdateBean> listener) {
        rxLife.add(request(WanApi.api().betaUsers(), new RequestListener<List<BetaUserBean>>() {
            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onSuccess(int code, List<BetaUserBean> data) {
                boolean isBetaUser = false;
                if (UserUtils.getInstance().isLogin()) {
                    for (BetaUserBean betaUserBean : data) {
                        if (betaUserBean.getUserId() == UserUtils.getInstance().getWanId()) {
                            isBetaUser = true;
                            break;
                        }
                    }
                }
                if (isBetaUser) {
                    rxLife.add(request(WanApi.api().betaUpdate(), new RequestCallback<UpdateBean>() {
                        @Override
                        public void onSuccess(int code, UpdateBean data) {
                            listener.onSuccess(code, data);
                            listener.onFinish();
                        }

                        @Override
                        public void onFailed(int code, String msg) {
                            listener.onFailed(code, msg);
                            listener.onFinish();
                        }
                    }));
                } else {
                    listener.onFailed(WanApi.ApiCode.ERROR, "非内测用户");
                    listener.onFinish();
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                listener.onFailed(code, msg);
                listener.onFinish();
            }

            @Override
            public void onError(ExceptionHandle handle) {
                listener.onError(handle);
            }

            @Override
            public void onFinish() {
            }
        }));
    }

    public static void getConfig(RxLife rxLife, @NonNull RequestListener<ConfigBean> listener) {
        rxLife.add(request(WanApi.api().getConfig(), listener));
    }

    public static void getAdvert(RxLife rxLife, @NonNull RequestListener<AdvertBean> listener) {
        rxLife.add(request(WanApi.api().getAdvert(), listener));
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
        cacheOrNetBean(rxLife,
                WanApi.api().getJinrishiciToken(),
                WanCache.CacheKey.JINRISHICI_TOKEN,
                String.class,
                new RequestCallback<String>() {
                    @Override
                    public void onSuccess(int code, String data) {
                        rxLife.add(request(WanApi.api().getJinrishici(data), listener));
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        listener.onFailed(code, msg);
                    }
                });
    }

}
