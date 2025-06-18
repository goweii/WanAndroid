package per.goweii.basic.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * @author Cuizhen
 */
public class Utils {
    @SuppressLint("StaticFieldLeak")
    private static Context context = null;
    private static final ActivityLifecycleCallbackImpl mActivityLifecycleCallback = new ActivityLifecycleCallbackImpl();

    public static void init(Context context) {
        if (Utils.context == null) {
            Utils.context = context;
            registerActivityLifecycleCallbacks();
        }
    }

    @NonNull
    public static Context getAppContext() {
        if (context == null) {
            throw new RuntimeException("Not initialized");
        }
        return context;
    }

    @Nullable
    public static Activity getResumedActivity() {
        if (context == null) {
            throw new RuntimeException("Not initialized");
        }
        return mActivityLifecycleCallback.getResumedActivity();
    }

    private static void registerActivityLifecycleCallbacks() {
        if (context == null) {
            return;
        }
        if (context instanceof Application) {
            Application application = (Application) context;
            application.registerActivityLifecycleCallbacks(mActivityLifecycleCallback);
        }
    }

    private static class ActivityLifecycleCallbackImpl implements Application.ActivityLifecycleCallbacks {
        @Nullable
        private WeakReference<Activity> mResumedActivityRef = null;

        @Nullable
        public Activity getResumedActivity() {
            return mResumedActivityRef != null ? mResumedActivityRef.get() : null;
        }

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            if (mResumedActivityRef == null || mResumedActivityRef.get() != activity) {
                mResumedActivityRef = new WeakReference<>(activity);
            }
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            if (mResumedActivityRef != null && mResumedActivityRef.get() == activity) {
                mResumedActivityRef.clear();
                mResumedActivityRef = null;
            }
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
        }
    }
}
