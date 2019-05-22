package per.goweii.swipeback;

import android.app.Activity;
import android.app.Application;
import android.view.View;

public class SwipeBack {

    private Activity mActivity;
    private SwipeBackLayout mSwipeBackLayout;

    public static void init(Application application) {
        SwipeBackManager.init(application);
    }

    private SwipeBack(Activity activity) {
        mActivity = activity;
        mSwipeBackLayout = new SwipeBackLayout(mActivity);
    }

    public static SwipeBack inject(Activity activity) {
        return new SwipeBack(activity);
    }

    public void onPostCreate() {
        mSwipeBackLayout.attachTo(mActivity);
    }

    public void onDestroy() {
        mActivity = null;
        mSwipeBackLayout = null;
    }

    public void onEnterAnimationComplete() {
        if (!mSwipeBackLayout.isTakeOverActivityEnterExitAnim()) {
            if (!mSwipeBackLayout.isActivitySwiping()) {
                mSwipeBackLayout.setActivityTranslucent(false);
            }
//            if (mSwipeBackLayout.getPreviousChildView() != null) {
//                mSwipeBackLayout.setActivityTranslucent(true);
//            }
        }
    }

    public boolean finish() {
        if (mSwipeBackLayout.isTakeOverActivityEnterExitAnim()) {
            mSwipeBackLayout.startExitAnim();
            return false;
        } else {
            if (mSwipeBackLayout.isActivitySwiping()) {
                return false;
            } else {
//                if (mSwipeBackLayout.isBackSuccess()) {
//                    return true;
//                } else {
//                    mSwipeBackLayout.setActivityTranslucent(false);
//                }
                return true;
            }
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

    public void setTakeOverActivityEnterExitAnim(boolean enable) {
        mSwipeBackLayout.setTakeOverActivityEnterExitAnim(enable);
    }

    public boolean isFinishAnimEnable() {
        return mSwipeBackLayout.isTakeOverActivityEnterExitAnim();
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
