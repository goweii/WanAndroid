package per.goweii.basic.core.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDex;

import java.util.ArrayList;
import java.util.List;

import per.goweii.basic.utils.ProcessUtils;

/**
 * @author Cuizhen
 * @date 2018/6/25-上午10:39
 */
class App extends Application implements Application.ActivityLifecycleCallbacks {
    @SuppressLint("StaticFieldLeak")
    private static Application application = null;
    private static final List<Activity> activities = new ArrayList<>();

    public static Application getApp() {
        if (application == null) {
            throw new NullPointerException("App is not registered in the manifest");
        } else {
            return application;
        }
    }

    public static Context getAppContext() {
        return getApp().getApplicationContext();
    }

    public static List<Activity> getActivities() {
        return activities;
    }

    public static boolean isAppAlive() {
        if (application == null) return false;
        return activities.size() != 0;
    }

    /**
     * 判断Android程序是否在前台运行
     */
    public static boolean isForeground() {
        ActivityManager activityManager = (ActivityManager) getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        String packageName = getAppContext().getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * APP从后台切换回前台
     */
    public static void bringToForeground() {
        if (!isForeground()) {
            Activity currentActivity = currentActivity();
            if (currentActivity != null) {
                Intent intent = new Intent(getAppContext(), currentActivity.getClass());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                getAppContext().startActivity(intent);
            }
        }
    }

    /**
     * 获取当前Activity
     */
    @Nullable
    public static Activity currentActivity() {
        if (activities.isEmpty()) {
            return null;
        }
        return activities.get(activities.size() - 1);
    }

    /**
     * 按照指定类名找到activity
     */
    @Nullable
    public static Activity findActivity(@Nullable Class<?> cls) {
        if (cls == null) {
            return null;
        }
        if (activities.isEmpty()) {
            return null;
        }
        for (Activity activity : activities) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }

    /**
     * 结束当前Activity
     */
    public static void finishCurrentActivity() {
        finishActivity(currentActivity());
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(@Nullable Activity activity) {
        if (activity == null) {
            return;
        }
        if (activities.isEmpty()) {
            return;
        }
        activities.remove(activity);
        activity.finish();
        activity = null;
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(@Nullable Class<? extends Activity> cls) {
        if (cls == null) {
            return;
        }
        if (activities.isEmpty()) {
            return;
        }
        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity activity = activities.get(i);
            if (cls.equals(activity.getClass())) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        if (activities.isEmpty()) {
            return;
        }
        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity activity = activities.get(i);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activities.clear();
    }

    public static void exitApp() {
        finishAllActivity();
        killProcess();
    }

    public static void killProcess() {
        Process.killProcess(Process.myPid());
    }

    public static void restartApp() {
        final Intent intent = getApp().getPackageManager().getLaunchIntentForPackage(getApp().getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            getApp().startActivity(intent);
            killProcess();
        } else {
            finishActivityWithoutCount(1);
            if (!activities.isEmpty()) {
                activities.get(0).recreate();
            }
        }
    }

    public static void recreate() {
        if (!activities.isEmpty()) {
            for (Activity activity : activities) {
                activity.recreate();
            }
        }
    }

    public static void finishActivityWithoutCount(int count) {
        if (activities.isEmpty()) {
            return;
        }
        if (count <= 0) {
            finishAllActivity();
            return;
        }
        for (int i = activities.size() - 1; i >= count; i--) {
            finishActivity(activities.get(i));
        }
    }

    public static void finishActivityWithout(@Nullable Class<? extends Activity> cls) {
        if (cls == null) {
            finishAllActivity();
            return;
        }
        if (activities.isEmpty()) {
            return;
        }
        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity activity = activities.get(i);
            if (!cls.equals(activity.getClass())) {
                finishActivity(activity);
            }
        }
    }

    public static void finishActivityWithout(@Nullable Activity activity) {
        if (activity == null) {
            finishAllActivity();
            return;
        }
        finishActivityWithout(activity.getClass());
    }

    public static boolean isMainProcess() {
        String mainProcessName = getAppContext().getPackageName();
        String processName = getCurrentProcessName();
        return TextUtils.equals(processName, mainProcessName);
    }

    @NonNull
    public static String getCurrentProcessName() {
        return ProcessUtils.INSTANCE.getCurrentProcessName(getAppContext());
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        application = this;
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        activities.add(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        activities.remove(activity);
    }

}
