package per.goweii.swipeback;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

public class SwipeBack {

    private Activity mActivity;
    private SwipeBackLayout mSwipeBackLayout;

    public static void init(Application application) {
        SwipeBackManager.init(application);
    }

    private SwipeBack(Activity activity) {
        mActivity = activity;
    }

    public static SwipeBack inject(Activity activity) {
        return new SwipeBack(activity);
    }

    public void onCreate() {
        mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mActivity.getWindow().getDecorView().setBackgroundDrawable(null);
        mSwipeBackLayout = new SwipeBackLayout(mActivity);
    }

    public void onPostCreate() {
        mSwipeBackLayout.attachToActivity(mActivity);
        mSwipeBackLayout.bind();
    }

    public void onEnterAnimationComplete() {
        if (!getSwipeBackLayout().isActivitySwiping()) {
            mSwipeBackLayout.convertActivityFromTranslucent();
        }
    }

    public void finish() {
        if (mSwipeBackLayout != null) {
            mSwipeBackLayout.startFinishAnim();
        }
    }

    public View findViewById(int id) {
        if (mSwipeBackLayout != null) {
            return mSwipeBackLayout.findViewById(id);
        }
        return null;
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    public void setFinishAnimEnable(boolean enable) {
        mSwipeBackLayout.setFinishAnimEnable(enable);
    }

    public boolean isFinishAnimEnable() {
        return mSwipeBackLayout.isFinishAnimEnable();
    }

    public void setSwipeBackEnable(boolean enable) {
        mSwipeBackLayout.setSwipeBackEnable(enable);
    }

    public boolean isSwipeBackEnable() {
        return mSwipeBackLayout.isSwipeBackEnable();
    }

    public void setForceEdgeEnable(boolean enable) {
        mSwipeBackLayout.setForceEdgeEnable(enable);
    }

    public boolean isForceEdgeEnable() {
        return mSwipeBackLayout.isForceEdgeEnable();
    }

    public void setSwipeDirection(@SwipeDirection int direction) {
        mSwipeBackLayout.setSwipeDirection(direction);
    }

    @SwipeDirection
    public int getSwipeDirection() {
        return mSwipeBackLayout.getSwipeDirection();
    }
}
