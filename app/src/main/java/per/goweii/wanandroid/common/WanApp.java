package per.goweii.wanandroid.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;

import per.goweii.basic.core.base.BaseApp;
import per.goweii.basic.utils.InitTaskRunner;
import per.goweii.swipeback.SwipeBack;
import per.goweii.swipeback.SwipeBackDirection;
import per.goweii.swipeback.transformer.ParallaxSwipeBackTransformer;
import per.goweii.wanandroid.utils.ConfigUtils;
import per.goweii.wanandroid.utils.GrayFilterHelper;
import per.goweii.wanandroid.utils.NightModeUtils;
import per.goweii.wanandroid.widget.refresh.ShiciRefreshHeader;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class WanApp extends BaseApp {

    static {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new ShiciRefreshHeader(context);
            }
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new ClassicsFooter(context);
            }
        });
    }

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
        SwipeBack.getInstance().init(this);
        SwipeBack.getInstance().setSwipeBackDirection(SwipeBackDirection.RIGHT);
        SwipeBack.getInstance().setSwipeBackTransformer(new ParallaxSwipeBackTransformer());
        new InitTaskRunner(this)
                .add(new CoreInitTask())
                .add(new RxHttpInitTask())
                .add(new WanDbInitTask())
                .add(new WanCacheInitTask())
                .add(new BlurredInitTask())
                .add(new X5InitTask())
                .add(new BuglyInitTask())
                .add(new CrashInitTask())
                .add(new ReadingModeTask())
                .run();

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        int themeId = ConfigUtils.getInstance().getTheme();
        if (themeId > 0) {
            activity.setTheme(themeId);
        }
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
