package per.goweii.basic.core.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2019/3/13
 */
public class BaseAppLike implements AppLike {
    @Override
    public void attachBaseContext(Context context) {
    }

    @Override
    public void onCreate(Application app) {
    }

    @Override
    public void onConfigurationChanged(Application app, Configuration newConfig) {
    }

    @Override
    public void onTerminate(Application app) {
    }

    @Override
    public void onLowMemory(Application app) {
    }

    @Override
    public void onTrimMemory(Application app, int level) {
    }
}
