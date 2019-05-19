package per.goweii.wanandroid.module.main.model;

import android.support.annotation.NonNull;

import io.reactivex.disposables.Disposable;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.module.mine.model.CollectionBean;

/**
 * @author CuiZhen
 * @date 2019/5/16
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class MainRequest extends BaseRequest {

    public static Disposable getCollectList(int page, @NonNull RequestListener<CollectionBean> listener) {
        return request(WanApi.api().getCollectList(page), listener);
    }

    public static Disposable collect(int id, @NonNull RequestListener<BaseBean> listener) {
        return request(WanApi.api().collect(id), listener);
    }

    public static Disposable collect(String title, String author, String link, @NonNull RequestListener<CollectBean> listener) {
        return request(WanApi.api().collect(title, author, link), listener);
    }

    public static Disposable uncollect(int id, @NonNull RequestListener<BaseBean> listener) {
        return request(WanApi.api().uncollect(id), listener);
    }

    public static Disposable uncollect(int id, int originId, @NonNull RequestListener<BaseBean> listener) {
        return request(WanApi.api().uncollect(id, originId), listener);
    }

    public static Disposable update(@NonNull RequestListener<UpdateBean> listener) {
        return request(WanApi.api().update(), listener);
    }

}
