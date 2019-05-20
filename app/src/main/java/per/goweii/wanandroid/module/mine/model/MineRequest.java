package per.goweii.wanandroid.module.mine.model;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.disposables.Disposable;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.module.main.model.CollectionLinkBean;

/**
 * @author CuiZhen
 * @date 2019/5/16
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class MineRequest extends BaseRequest {

    public static Disposable getCollectArticleList(int page, @NonNull RequestListener<CollectionArticleBean> listener) {
        return request(WanApi.api().getCollectArticleList(page), listener);
    }

    public static Disposable getCollectLinkList(@NonNull RequestListener<List<CollectionLinkBean>> listener) {
        return request(WanApi.api().getCollectLinkList(), listener);
    }

    public static Disposable logout(@NonNull RequestListener<BaseBean> listener) {
        return request(WanApi.api().logout(), listener);
    }
}
