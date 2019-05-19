package per.goweii.wanandroid.module.navigation.model;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.disposables.Disposable;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class NaviRequest extends BaseRequest {

    public static Disposable getNaviList(@NonNull RequestListener<List<NaviBean>> listener) {
        return request(WanApi.api().getNaviList(), listener);
    }

}
