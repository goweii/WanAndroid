package per.goweii.wanandroid.common;

import per.goweii.basic.core.base.BaseApp;
import per.goweii.basic.utils.InitTaskRunner;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class WanApp extends BaseApp {
    @Override
    public void onCreate() {
        super.onCreate();
        new InitTaskRunner(this)
                .add(new SmartRefreshInitTask())
                .add(new CookieManagerInitTask())
                .add(new NightModeInitTask())
                .add(new ThemeInitTask())
                .add(new GrayFilterInitTask())
                .add(new SwipeBackInitTask())
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
}
