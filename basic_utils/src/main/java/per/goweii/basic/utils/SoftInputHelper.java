package per.goweii.basic.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;

/**
 * 监听软键盘的打开和隐藏
 * 打开时滚动布局，可设置仅在某几个EditText获取焦点时开启
 *
 * @author Cuizhen
 * @version v1.0.0
 * @date 2018/3/30-上午9:06
 */
public class SoftInputHelper implements ViewTreeObserver.OnGlobalLayoutListener, ViewTreeObserver.OnGlobalFocusChangeListener {

    private final Window window;
    private final View rootView;

    private long duration = 200;
    private View moveView = null;
    private View bottomView = null;
    private EditText[] focusViews = null;
    private OnSoftInputListener onSoftInputListener = null;

    private boolean moveWithScroll = false;

    private boolean isOpened = false;
    private int moveHeight = 0;
    private boolean isFocusChange = false;

    private Runnable moveRunnable = new Runnable() {
        @Override
        public void run() {
            if (isViewFocus()) {
                Rect rect = getRootViewRect();
                int bottomViewY = getBottomViewY();
                if (bottomViewY > rect.bottom) {
                    int offHeight = bottomViewY - rect.bottom;
                    moveHeight += offHeight;
                    move();
                }
            } else {
                moveHeight = 0;
                move();
            }
        }
    };

    private SoftInputHelper(@NonNull Activity activity) {
        this.window = activity.getWindow();
        this.rootView = window.getDecorView().getRootView();
        ViewTreeObserver observer = rootView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(this);
        observer.addOnGlobalFocusChangeListener(this);
    }

    public static SoftInputHelper with(@NonNull Activity activity) {
        return new SoftInputHelper(activity);
    }

    public SoftInputHelper init(@NonNull View moveView, @NonNull View bottomView, @NonNull EditText... focusViews) {
        this.moveView = moveView;
        this.bottomView = bottomView;
        this.focusViews = focusViews;
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
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
        Rect rect = getRootViewRect();
        boolean isOpen = isSoftOpen(rect.bottom - rect.top, rootView.getHeight());
        if (isOpen) {
            if (!isOpened) {
                isOpened = true;
                if (onSoftInputListener != null) {
                    onSoftInputListener.onOpen();
                }
            }
            if (moveView != null && bottomView != null && focusViews != null) {
                if (isFocusChange) {
                    isFocusChange = false;
                    rootView.removeCallbacks(moveRunnable);
                }
                if (isViewFocus()) {
                    int bottomViewY = getBottomViewY();
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
                } else {
                    moveHeight = 0;
                    move();
                }
            }
        } else {
            if (isOpened) {
                isOpened = false;
                if (onSoftInputListener != null) {
                    onSoftInputListener.onClose();
                }
            }
            if (moveView != null && bottomView != null && focusViews != null) {
                moveHeight = 0;
                move();
            }
        }
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        if (isOpened) {
            if (moveView != null && bottomView != null && focusViews != null) {
                isFocusChange = true;
                rootView.postDelayed(moveRunnable, 100);
            }
        }
    }

    private int getBottomViewY() {
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
     * @param usableHeightNow          当前可被用户使用的高度
     * @param usableHeightSansKeyboard 总高度，及包含软键盘占位的高度
     * @return boolean
     */
    private boolean isSoftOpen(int usableHeightNow, int usableHeightSansKeyboard) {
        int heightDifference = usableHeightSansKeyboard - usableHeightNow;
        if (heightDifference > (usableHeightSansKeyboard / 4)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isViewFocus() {
        boolean focus = false;
        if (focusViews == null || focusViews.length == 0) {
            focus = true;
        } else {
            View focusView = window.getCurrentFocus();
            for (EditText editText : focusViews) {
                if (focusView == editText) {
                    focus = true;
                    break;
                }
            }
        }
        return focus;
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
