package per.goweii.wanandroid.common;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatDelegate;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import io.realm.Realm;
import per.goweii.basic.core.CoreInit;
import per.goweii.basic.core.base.BaseApp;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.burred.Blurred;
import per.goweii.rxhttp.core.RxHttp;
import per.goweii.wanandroid.http.RxHttpRequestSetting;
import per.goweii.wanandroid.http.WanCache;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.utils.UserUtils;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WanApp extends BaseApp {

    private static PersistentCookieJar mCookieJar = null;

    @Override
    public void onCreate() {
        super.onCreate();
        setDarkModeStatus();
        RxHttp.init(this);
        RxHttp.initRequest(new RxHttpRequestSetting(getCookieJar()));
        WanCache.init();
        Blurred.init(getAppContext());
        CoreInit.getInstance().setOnGoLoginCallback(new SimpleCallback<Activity>() {
            @Override
            public void onResult(Activity data) {
                UserUtils.getInstance().doIfLogin(data);
            }
        });
        Realm.init(this);
    }

    public static boolean getDarkModeStatus() {
        int mode = getAppContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static void setDarkModeStatus() {
        if (SettingUtils.getInstance().isDarkTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static PersistentCookieJar getCookieJar() {
        if (mCookieJar == null) {
            mCookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getAppContext()));
        }
        return mCookieJar;
    }
}
