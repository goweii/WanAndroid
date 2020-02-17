package per.goweii.basic.core.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import per.goweii.basic.utils.LogUtils;

/**
 * @author Cuizhen
 * @date 2018/6/25-上午10:39
 */
class App extends Application implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = App.class.getSimpleName();

    protected static final int FLAG_CLEAR_TOP = 0;
    protected static final int FLAG_CLEAR_OLD = 1;

    @SuppressLint("StaticFieldLeak")
    private static Application application = null;
    private static List<Activity> activities = Collections.synchronizedList(new LinkedList<Activity>());
    private static Map<Class<? extends Activity>, Integer> singleInstanceActivities = Collections.synchronizedMap(new HashMap<Class<? extends Activity>, Integer>());

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

    public static void addSingleInstanceActivity(Class<? extends Activity> cls, @Flag int flag) {
        singleInstanceActivities.put(cls, flag);
    }

    public static List<Activity> getActivities() {
        return activities;
    }

    public static boolean isAppAlive() {
        if (application == null) {
            return false;
        }
        if (activities == null || activities.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 判断Android程序是否在前台运行
     */
    public static boolean isAppOnForeground() {
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
    public static void returnToForeground() {
        if (!isAppOnForeground()) {
            Activity currentActivity = currentActivity();
            if (currentActivity != null) {
                Intent intent = new Intent(getAppContext(), currentActivity.getClass());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                getAppContext().startActivity(intent);
            }
        }
    }

    public static boolean isMainProcess() {
        String mainProcessName = getAppContext().getPackageName();
        String processName = currentProcessName();
        return processName == null || TextUtils.equals(processName, mainProcessName);
    }

    @Nullable
    public static String currentProcessName() {
        return getProcessName(Process.myPid());
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    @Nullable
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取当前Activity
     */
    @Nullable
    public static Activity currentActivity() {
        if (activities == null || activities.isEmpty()) {
            return null;
        }
        return activities.get(activities.size() - 1);
    }

    /**
     * 按照指定类名找到activity
     */
    @Nullable
    public static Activity findActivity(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        if (activities == null || activities.isEmpty()) {
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
    public static void finishActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        if (activities == null || activities.isEmpty()) {
            return;
        }
        activities.remove(activity);
        activity.finish();
        activity = null;
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<? extends Activity> cls) {
        if (cls == null) {
            return;
        }
        if (activities == null || activities.isEmpty()) {
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
        if (activities == null || activities.isEmpty()) {
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
            if (activities != null && !activities.isEmpty()) {
                activities.get(0).recreate();
            }
        }
    }

    public static void recreate() {
        if (activities != null && !activities.isEmpty()) {
            for (Activity activity : activities) {
                activity.recreate();
            }
        }
    }

    public static void finishActivityWithoutCount(int count) {
        if (activities == null || activities.isEmpty()) {
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

    public static void finishActivityWithout(Class<? extends Activity> cls) {
        if (cls == null) {
            finishAllActivity();
            return;
        }
        if (activities == null || activities.isEmpty()) {
            return;
        }
        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity activity = activities.get(i);
            if (!cls.equals(activity.getClass())) {
                finishActivity(activity);
            }
        }
    }

    public static void finishActivityWithout(Activity activity) {
        if (activity == null) {
            finishAllActivity();
            return;
        }
        finishActivityWithout(activity.getClass());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        registerActivityListener();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity == null) {
            return;
        }
        Class<? extends Activity> cls = activity.getClass();
        if (!singleInstanceActivities.containsKey(cls)) {
            activities.add(activity);
            return;
        }
        int flag = singleInstanceActivities.get(cls);
        switch (flag) {
            default:
                throw new UnsupportedOperationException("Flag not find");
            case FLAG_CLEAR_TOP:
                int oldPos = -1;
                for (int i = 0; i < activities.size(); i++) {
                    if (cls.equals(activities.get(i).getClass())) {
                        oldPos = i;
                    }
                }
                if (oldPos >= 0) {
                    for (int i = activities.size() - 1; i >= oldPos; i--) {
                        Activity top = activities.get(i);
                        if (!top.isFinishing()) {
                            top.finish();
                        }
                    }
                }
                break;
            case FLAG_CLEAR_OLD:
                for (int i = activities.size() - 1; i >= 0; i--) {
                    Activity old = activities.get(i);
                    if (cls.equals(old.getClass()) && !old.isFinishing()) {
                        old.finish();
                    }
                }
                break;
        }
        activities.add(activity);
        LogUtils.i(TAG, activities);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity == null) {
            return;
        }
        if (activities == null || activities.isEmpty()) {
            return;
        }
        if (activities.contains(activity)) {
            activities.remove(activity);
        }
        LogUtils.i(TAG, activities);
    }

    private void registerActivityListener() {
        registerActivityLifecycleCallbacks(this);
    }

    @IntDef({FLAG_CLEAR_TOP, FLAG_CLEAR_OLD})
    @Retention(RetentionPolicy.SOURCE)
    @interface Flag {
    }

}
