package per.goweii.wanandroid.module.home.model;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.disposables.Disposable;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.module.main.model.ArticleBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class HomeRequest extends BaseRequest {

    public static Disposable getBanner(@NonNull RequestListener<List<BannerBean>> listener) {
        return request(WanApi.api().getBanner(), listener);
    }

    public static Disposable getArticleList(@IntRange(from = 0) int page, @NonNull RequestListener<HomeBean> listener) {
        return request(WanApi.api().getArticleList(page), listener);
    }

    public static Disposable getTopArticleList(@NonNull RequestListener<List<ArticleBean>> listener) {
        return request(WanApi.api().getTopArticleList(), listener);
    }

    public static Disposable getHotKeyList(@NonNull RequestListener<List<HotKeyBean>> listener) {
        return request(WanApi.api().getHotKeyList(), listener);
    }

    public static Disposable search(@IntRange(from = 0) int page, String key, @NonNull RequestListener<SearchBean> listener) {
        return request(WanApi.api().search(page, key), listener);
    }

}
