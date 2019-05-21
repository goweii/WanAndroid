package per.goweii.swipeback;

import android.app.Activity;
import android.app.ActivityOptions;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SwipeChecker {

    @Deprecated
    public static boolean canViewScrollUp(View mView, float x, float y, boolean defaultValueForNull) {
        if (mView == null || !contains(mView, x, y)) {
            return defaultValueForNull;
        }
        return ViewCompat.canScrollVertically(mView, -1);
    }

    public static boolean canViewScrollUp(List<View> mViews, float x, float y, boolean defaultValueForNull) {
        if (mViews == null) {
            return defaultValueForNull;
        }
        View view = contains(mViews, x, y);
        if (view == null) {
            return defaultValueForNull;
        }
        return ViewCompat.canScrollVertically(view, -1);
    }

    @Deprecated
    public static boolean canViewScrollDown(View mView, float x, float y, boolean defaultValueForNull) {
        if (mView == null || !contains(mView, x, y)) {
            return defaultValueForNull;
        }
        return ViewCompat.canScrollVertically(mView, 1);
    }

    public static boolean canViewScrollDown(List<View> mViews, float x, float y, boolean defaultValueForNull) {
        if (mViews == null) {
            return defaultValueForNull;
        }
        View view = contains(mViews, x, y);
        if (view == null) {
            return defaultValueForNull;
        }
        return ViewCompat.canScrollVertically(view, 1);
    }

    @Deprecated
    public static boolean canViewScrollRight(View mView, float x, float y, boolean defaultValueForNull) {
        if (mView == null || !contains(mView, x, y)) {
            return defaultValueForNull;
        }
        return ViewCompat.canScrollHorizontally(mView, -1);
    }

    public static boolean canViewScrollRight(List<View> mViews, float x, float y, boolean defaultValueForNull) {
        if (mViews == null) {
            return defaultValueForNull;
        }
        View view = contains(mViews, x, y);
        if (view == null) {
            return defaultValueForNull;
        }
        return ViewCompat.canScrollHorizontally(view, -1);
    }

    @Deprecated
    public static boolean canViewScrollLeft(View mView, float x, float y, boolean defaultValueForNull) {
        if (mView == null || !contains(mView, x, y)) {
            return defaultValueForNull;
        }
        return ViewCompat.canScrollHorizontally(mView, 1);
    }

    public static boolean canViewScrollLeft(List<View> mViews, float x, float y, boolean defaultValueForNull) {
        if (mViews == null) {
            return defaultValueForNull;
        }
        View view = contains(mViews, x, y);
        if (view == null) {
            return defaultValueForNull;
        }
        return ViewCompat.canScrollHorizontally(view, 1);
    }

    @Deprecated
    public static View findAllScrollViews(ViewGroup mViewGroup) {
        for (int i = 0; i < mViewGroup.getChildCount(); i++) {
            View mView = mViewGroup.getChildAt(i);
            if (mView.getVisibility() != View.VISIBLE) {
                continue;
            }
            if (isScrollableView(mView)) {
                return mView;
            }
            if (mView instanceof ViewGroup) {
                mView = findAllScrollViews((ViewGroup) mView);
                if (mView != null) {
                    return mView;
                }
            }
        }
        return null;
    }


    public static List<View> findAllScrollViews2(ViewGroup mViewGroup) {
        List<View> views = new ArrayList<>();
        for (int i = 0; i < mViewGroup.getChildCount(); i++) {
            View mView = mViewGroup.getChildAt(i);
            if (mView.getVisibility() != View.VISIBLE) {
                continue;
            }
            if (isScrollableView(mView)) {
                views.add(mView);
                continue;
            }
            if (mView instanceof ViewGroup) {
                views.addAll(findAllScrollViews2((ViewGroup) mView));
            }
        }
        return views;
    }

    public static boolean isScrollableView(View mView) {
        return mView instanceof ScrollView
                || mView instanceof HorizontalScrollView
                || mView instanceof NestedScrollView
                || mView instanceof AbsListView
                || mView instanceof RecyclerView
                || mView instanceof ViewPager
                || mView instanceof WebView;
    }

    public static boolean contains(View mView, float x, float y) {
        Rect localRect = new Rect();
        mView.getGlobalVisibleRect(localRect);
        return localRect.contains((int) x, (int) y);
    }

    public static View contains(List<View> mViews, float x, float y) {
        if (mViews == null) {
            return null;
        }
        View view = null;
        for (View mView : mViews) {
            Rect localRect = new Rect();
            mView.getGlobalVisibleRect(localRect);
            if (localRect.contains((int) x, (int) y)) {
                view = mView;
                break;
            }
        }
        return view;
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque
     * Activity.
     * <p>
     * Call this whenever the background of a translucent Activity has changed
     * to become opaque. Doing so will allow the {@link android.view.Surface} of
     * the Activity behind to be released.
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityFromTranslucent(Activity activity) {
        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
            method.setAccessible(true);
            method.invoke(activity);
        } catch (Throwable t) {
        }
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} back from opaque to
     * translucent following a call to
     * {@link #convertActivityFromTranslucent(Activity)} .
     * <p>
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityToTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            convertActivityToTranslucentAfterL(activity);
        } else {
            convertActivityToTranslucentBeforeL(activity);
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms before Android 5.0
     */
    private static void convertActivityToTranslucentBeforeL(Activity activity) {
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method method = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz);
            method.setAccessible(true);
            method.invoke(activity, new Object[]{
                    null
            });
        } catch (Throwable t) {
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms after Android 5.0
     */
    private static void convertActivityToTranslucentAfterL(Activity activity) {
        try {
            Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
            getActivityOptions.setAccessible(true);
            Object options = getActivityOptions.invoke(activity);

            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz, ActivityOptions.class);
            convertToTranslucent.setAccessible(true);
            convertToTranslucent.invoke(activity, null, options);
        } catch (Throwable t) {
        }
    }
}
