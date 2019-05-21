package per.goweii.wanandroid.common;

import android.app.Activity;

import per.goweii.basic.core.CoreInit;
import per.goweii.basic.core.base.BaseApp;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.burred.Blurred;
import per.goweii.rxhttp.core.RxHttp;
import per.goweii.swipeback.SwipeBack;
import per.goweii.wanandroid.http.RxHttpRequestSetting;
import per.goweii.wanandroid.utils.UserUtils;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WanApp extends BaseApp {
    @Override
    public void onCreate() {
        super.onCreate();
        RxHttp.init(this);
        RxHttp.initRequest(new RxHttpRequestSetting());
        Blurred.init(getAppContext());
        CoreInit.getInstance().setOnGoLoginCallback(new SimpleCallback<Activity>() {
            @Override
            public void onResult(Activity data) {
                UserUtils.getInstance().doIfLogin(data);
            }
        });
        SwipeBack.init(this);
    }
}
