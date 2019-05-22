package per.goweii.swipeback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.Stack;

class SwipeBackManager implements Application.ActivityLifecycleCallbacks {

    private static SwipeBackManager INSTANCE = null;
    private final Stack<Activity> mActivityStack = new Stack<>();

    private SwipeBackManager(Application application) {
        application.registerActivityLifecycleCallbacks(this);
    }

    public static SwipeBackManager getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("需要先在Application中调用SwipeBack.init()方法完成初始化");
        }
        return INSTANCE;
    }

    public static void init(Application application) {
        if (INSTANCE == null) {
            INSTANCE = new SwipeBackManager(application);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        mActivityStack.add(activity);
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
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivityStack.remove(activity);
    }

    public Stack<Activity> getActivityStack() {
        return mActivityStack;
    }

    /**
     * 获取倒数第二个Activity
     */
    @Nullable
    public Activity getPreviousActivity() {
        return mActivityStack.size() >= 2 ? mActivityStack.get(mActivityStack.size() - 2) : null;
    }

    /**
     * 获取倒数第二个 Activity
     */
    @Nullable
    public Activity getPreviousActivity(Activity currentActivity) {
        Activity activity = null;
        try {
            if (mActivityStack.size() > 1) {
                activity = mActivityStack.get(mActivityStack.size() - 2);

                if (currentActivity.equals(activity)) {
                    int index = mActivityStack.indexOf(currentActivity);
                    if (index > 0) {
                        // 处理内存泄漏或最后一个 Activity 正在 finishing 的情况
                        activity = mActivityStack.get(index - 1);
                    } else if (mActivityStack.size() == 2) {
                        // 处理屏幕旋转后 mActivityStack 中顺序错乱
                        activity = mActivityStack.lastElement();
                    }
                }
            }
        } catch (Exception e) {
        }
        return activity;
    }
}
