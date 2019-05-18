package per.goweii.wanandroid.module.wxarticle.model;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.disposables.Disposable;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;

/**
 * @author CuiZhen
 * @date 2019/5/16
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WxRequest extends BaseRequest {

    public static Disposable getWxArticleChapters(@NonNull RequestListener<List<WxChapterBean>> listener) {
        return request(WanApi.api().getWxArticleChapters(), listener);
    }

    public static Disposable getWxArticleList(int id, @IntRange(from = 1) int page, @NonNull RequestListener<WxArticleBean> listener) {
        return request(WanApi.api().getWxArticleList(id, page), listener);
    }

    public static Disposable getWxArticleList(int id, @IntRange(from = 1) int page, String key, @NonNull RequestListener<WxArticleBean> listener) {
        return request(WanApi.api().getWxArticleList(id, page, key), listener);
    }

}
