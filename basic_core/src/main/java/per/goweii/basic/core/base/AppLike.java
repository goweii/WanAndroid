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
interface AppLike {
    void attachBaseContext(Context context);
    void onCreate(Application app);
    void onConfigurationChanged(Application app, Configuration newConfig);
    void onTerminate(Application app);
    void onLowMemory(Application app);
    void onTrimMemory(Application app, int level);
}
