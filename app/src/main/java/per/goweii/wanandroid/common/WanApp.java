package per.goweii.wanandroid.common;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;

import java.util.LinkedHashMap;
import java.util.Map;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import io.realm.Realm;
import per.goweii.basic.core.CoreInit;
import per.goweii.basic.core.base.BaseApp;
import per.goweii.basic.utils.DebugUtils;
import per.goweii.basic.utils.LogUtils;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.burred.Blurred;
import per.goweii.rxhttp.core.RxHttp;
import per.goweii.wanandroid.BuildConfig;
import per.goweii.wanandroid.http.RxHttpRequestSetting;
import per.goweii.wanandroid.http.WanCache;
import per.goweii.wanandroid.module.main.activity.CrashActivity;
import per.goweii.wanandroid.module.main.activity.MainActivity;
import per.goweii.wanandroid.module.main.activity.WebActivity;
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
        if (isMainProcess()) {
            initDarkMode();
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
        initX5();
        initBugly();
        initCrashActivity();
    }

    private void initX5() {
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                LogUtils.d("x5", "initX5Environment->onCoreInitFinished");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                LogUtils.d("x5", "initX5Environment->onViewInitFinished=" + b);
            }
        });
    }

    private void initBugly() {
        if (!DebugUtils.isDebug()) {
            CrashReport.setIsDevelopmentDevice(this, DebugUtils.isDebug());
            CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
            strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
                @Override
                public Map<String, String> onCrashHandleStart(int crashType, String errorType, String errorMessage, String errorStack) {
                    LinkedHashMap<String, String> map = new LinkedHashMap<>();
                    String x5CrashInfo = com.tencent.smtt.sdk.WebView.getCrashExtraMessage(getAppContext());
                    map.put("x5crashInfo", x5CrashInfo);
                    return map;
                }

                @Override
                public byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType, String errorMessage, String errorStack) {
                    try {
                        return "Extra data.".getBytes("UTF-8");
                    } catch (Exception e) {
                        return null;
                    }
                }
            });
            strategy.setUploadProcess(isMainProcess());
            CrashReport.initCrashReport(this, BuildConfig.APPID_BUGLY, DebugUtils.isDebug(), strategy);
        }
    }

    private void initCrashActivity() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
                .enabled(true)
                .showErrorDetails(true)
                .showRestartButton(true)
                .logErrorOnRestart(false)
                .trackActivities(false)
                .minTimeBetweenCrashesMs(2000)
                .restartActivity(MainActivity.class)
                .errorActivity(CrashActivity.class)
                .apply();
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

    public static boolean isDarkMode() {
        int mode = getAppContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static void initDarkMode() {
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
