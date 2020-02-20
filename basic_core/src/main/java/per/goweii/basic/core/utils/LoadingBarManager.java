package per.goweii.basic.core.utils;

import android.view.View;
import android.view.ViewGroup;

import per.goweii.actionbarex.ActionBarEx;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class LoadingBarManager implements Runnable {

    private static final long MIN_SHOW_TIME = 500L;

    private boolean mHasTryToFindActionBarEx = false;
    private ActionBarEx mActionBarEx = null;
    private View mRootView = null;

    private long mShowTime = 0L;

    private int count = 0;

    private LoadingBarManager(View rootView) {
        mRootView = rootView;
    }

    public static LoadingBarManager attach(View rootView) {
        return new LoadingBarManager(rootView);
    }

    public void detach() {
        if (mActionBarEx != null) {
            mActionBarEx.removeCallbacks(this);
        }
        mRootView = null;
        mActionBarEx = null;
    }

    @Override
    public void run() {
        if (mActionBarEx != null) {
            mActionBarEx.getForegroundLayer().setVisibility(View.GONE);
        }
    }

    public void show() {
        tryToFindActionBarEx();
        if (mActionBarEx != null) {
            count = count < 0 ? 0 : count;
            if (count == 0) {
                mActionBarEx.removeCallbacks(this);
                mShowTime = System.currentTimeMillis();
                mActionBarEx.getForegroundLayer().setVisibility(View.VISIBLE);
            }
            count++;
        }
    }

    public void dismiss() {
        tryToFindActionBarEx();
        if (mActionBarEx != null) {
            count--;
            count = count < 0 ? 0 : count;
            if (count == 0) {
                long dismissTime = System.currentTimeMillis();
                long delay = MIN_SHOW_TIME - (dismissTime - mShowTime);
                delay = delay < 0 ? 0 : delay;
                mActionBarEx.postDelayed(this, delay);
            }
        }
    }

    public void clear() {
        tryToFindActionBarEx();
        if (mActionBarEx != null) {
            mActionBarEx.getForegroundLayer().setVisibility(View.GONE);
            count = 0;
        }
    }

    private void tryToFindActionBarEx() {
        if (!mHasTryToFindActionBarEx) {
            mHasTryToFindActionBarEx = true;
            mActionBarEx = findActionBarEx(mRootView);
        }
    }

    private ActionBarEx findActionBarEx(View view) {
        if (view instanceof ActionBarEx) {
            return (ActionBarEx) view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                ActionBarEx actionBarEx = findActionBarEx(child);
                if (actionBarEx != null) {
                    return actionBarEx;
                }
            }
        }
        return null;
    }
}
