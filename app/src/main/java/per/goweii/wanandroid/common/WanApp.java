package per.goweii.wanandroid.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import per.goweii.basic.core.base.BaseApp;
import per.goweii.basic.utils.InitTaskRunner;
import per.goweii.wanandroid.module.main.activity.MainActivity;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.utils.NightModeUtils;
import per.goweii.wanandroid.utils.TM;

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
        TM.APP_STARTUP.start("WanApp attachBaseContext");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TM.APP_STARTUP.record("WanApp onCreate");
        initDarkMode();
        new InitTaskRunner(this)
                .add(new CoreInitTask())
                .add(new RxHttpInitTask())
                .add(new WanCacheInitTask())
                .add(new RealmInitTask())
                .add(new BlurredInitTask())
                .add(new X5InitTask())
                .add(new BuglyInitTask())
                .add(new CrashInitTask())
//                .add(new CyanInitTask())
                .run();
        TM.APP_STARTUP.record("WanApp onCreate third-part init completed");
    }

    private static boolean mWebActivityStarted = false;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        if (activity instanceof MainActivity) {
            if (!mWebActivityStarted) {
                Intent intent = new Intent(activity, WebActivity.class);
                intent.putExtra("destroyOnCreated", true);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        super.onActivityStarted(activity);
        if (activity instanceof WebActivity) {
            boolean destroyOnCreated = activity.getIntent().getBooleanExtra("destroyOnCreated", false);
            if (destroyOnCreated) {
                activity.finish();
                activity.overridePendingTransition(0, 0);
            }
            mWebActivityStarted = true;
        }
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
