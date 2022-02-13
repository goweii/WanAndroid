package per.goweii.basic.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import java.util.HashMap;
import java.util.Map;

/**
 * 监听软键盘的打开和隐藏
 * 打开时滚动布局，可设置仅在某几个EditText获取焦点时开启
 */
public final class SoftInputHelper implements ViewTreeObserver.OnGlobalLayoutListener, ViewTreeObserver.OnGlobalFocusChangeListener {

    private final Window window;
    private final View rootView;

    private long duration = 300;
    private View moveView = null;
    private Map<View, View> focusBottomMap = new HashMap<>(1);
    private OnSoftInputListener onSoftInputListener = null;

    private boolean moveWithScroll = false;

    private boolean isOpened = false;
    private int moveHeight = 0;
    private boolean isFocusChange = false;

    private Runnable moveRunnable = new Runnable() {
        @Override
        public void run() {
            calcToMove();
        }
    };

    public static SoftInputHelper attach(Activity activity) {
        return new SoftInputHelper(activity);
    }

    private SoftInputHelper(Activity activity) {
        this.window = activity.getWindow();
        this.rootView = window.getDecorView().getRootView();
        ViewTreeObserver observer = rootView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(this);
        observer.addOnGlobalFocusChangeListener(this);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public void detach() {
        if (rootView.getViewTreeObserver().isAlive()) {
            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            rootView.getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
        }
    }

    public SoftInputHelper moveBy(View moveView) {
        this.moveView = moveView;
        return this;
    }

    public SoftInputHelper moveWith(View bottomView, View... focusViews) {
        for (View focusView : focusViews) {
            focusBottomMap.put(focusView, bottomView);
        }
        return this;
    }

    public SoftInputHelper listener(OnSoftInputListener onSoftInputListener) {
        this.onSoftInputListener = onSoftInputListener;
        return this;
    }

    public SoftInputHelper duration(long duration) {
        this.duration = duration;
        return this;
    }

    /**
     * 设置moveView移动以ScrollY属性滚动内容
     *
     * @return SoftInputHelper
     */
    public SoftInputHelper moveWithScroll() {
        this.moveWithScroll = true;
        return this;
    }

    /**
     * 设置moveView移动以TranslationY属性移动位置
     *
     * @return SoftInputHelper
     */
    public SoftInputHelper moveWithTranslation() {
        this.moveWithScroll = false;
        return this;
    }

    @Override
    public void onGlobalLayout() {
        boolean isOpen = isSoftOpen();
        if (isOpen) {
            if (!isOpened) {
                isOpened = true;
                if (onSoftInputListener != null) {
                    onSoftInputListener.onOpen();
                }
            }
            if (moveView != null) {
                if (isFocusChange) {
                    isFocusChange = false;
                    rootView.removeCallbacks(moveRunnable);
                }
                calcToMove();
            }
        } else {
            if (isOpened) {
                isOpened = false;
                if (onSoftInputListener != null) {
                    onSoftInputListener.onClose();
                }
            }
            if (moveView != null) {
                moveHeight = 0;
                move();
            }
        }
    }

    private void calcToMove() {
        View focusView = isViewFocus();
        if (focusView != null) {
            View bottomView = focusBottomMap.get(focusView);
            if (bottomView != null) {
                Rect rect = getRootViewRect();
                int bottomViewY = getBottomViewY(bottomView);
                if (bottomViewY > rect.bottom) {
                    int offHeight = bottomViewY - rect.bottom;
                    moveHeight += offHeight;
                    move();
                } else if (bottomViewY < rect.bottom) {
                    int offHeight = -(bottomViewY - rect.bottom);
                    if (moveHeight > 0) {
                        if (moveHeight >= offHeight) {
                            moveHeight -= offHeight;
                        } else {
                            moveHeight = 0;
                        }
                        move();
                    }
                }
            }
        } else {
            moveHeight = 0;
            move();
        }
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        if (isOpened) {
            if (moveView != null) {
                isFocusChange = true;
                rootView.postDelayed(moveRunnable, 100);
            }
        }
    }

    private int getBottomViewY(View bottomView) {
        int[] bottomLocation = new int[2];
        bottomView.getLocationOnScreen(bottomLocation);
        return bottomLocation[1] + bottomView.getHeight();
    }

    private Rect getRootViewRect() {
        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);
        return rect;
    }

    private void move() {
        if (moveWithScroll) {
            scrollTo(moveHeight);
        } else {
            translationTo(-moveHeight);
        }
    }

    private void translationTo(int to) {
        float translationY = moveView.getTranslationY();
        if (translationY == to) {
            return;
        }
        ObjectAnimator anim = ObjectAnimator.ofFloat(moveView, "translationY", translationY, to);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(duration);
        anim.start();
    }

    private void scrollTo(int to) {
        int scrollY = moveView.getScrollY();
        if (scrollY == to) {
            return;
        }
        ObjectAnimator anim = ObjectAnimator.ofInt(moveView, "scrollY", scrollY, to);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(duration);
        anim.start();
    }

    /**
     * 判断软键盘打开状态的阈值
     * 此处以用户可用高度变化值大于1/4总高度时作为判断依据。
     *
     * @return boolean
     */
    private boolean isSoftOpen() {
        Rect rect = getRootViewRect();
        int usableHeightNow = rect.bottom - rect.top;
        int usableHeightSansKeyboard = rootView.getHeight();
        int heightDifference = usableHeightSansKeyboard - usableHeightNow;
        if (heightDifference > (usableHeightSansKeyboard / 4)) {
            return true;
        } else {
            return false;
        }
    }

    private View isViewFocus() {
        View focusView = window.getCurrentFocus();
        for (View view : focusBottomMap.keySet()) {
            if (focusView == view) {
                return view;
            }
        }
        return null;
    }

    public interface OnSoftInputListener {
        /**
         * 软键盘由关闭变为打开时调用
         */
        void onOpen();

        /**
         * 软键盘由打开变为关闭时调用
         */
        void onClose();
    }
}
