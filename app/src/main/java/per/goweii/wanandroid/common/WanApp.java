package per.goweii.wanandroid.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import per.goweii.basic.core.base.BaseApp;
import per.goweii.basic.utils.InitTaskRunner;
import per.goweii.wanandroid.utils.GrayFilterHelper;
import per.goweii.wanandroid.utils.NightModeUtils;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class WanApp extends BaseApp {

    private static PersistentCookieJar mCookieJar = null;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GrayFilterHelper.INSTANCE.attach(this);
        initDarkMode();
        new InitTaskRunner(this)
                .add(new CoreInitTask())
                .add(new RxHttpInitTask())
                .add(new WanDbInitTask())
                .add(new WanCacheInitTask())
                .add(new BlurredInitTask())
                .add(new X5InitTask())
                .add(new BuglyInitTask())
                .add(new CrashInitTask())
                .run();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        super.onActivityStarted(activity);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static boolean isDarkMode() {
        return NightModeUtils.isNightMode(getAppContext());
    }

    public static void initDarkMode() {
        NightModeUtils.initNightMode();
    }

    public static PersistentCookieJar getCookieJar() {
        if (mCookieJar == null) {
            mCookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getAppContext()));
        }
        return mCookieJar;
    }
}
