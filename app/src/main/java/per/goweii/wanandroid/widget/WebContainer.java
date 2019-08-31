package per.goweii.wanandroid.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * @author CuiZhen
 * @date 2019/8/31
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WebContainer extends FrameLayout {

    private final float mTouchSlop;
    private final long mTapTimeout;
    private long mDelay = 1000L;

    private long mDownTime = 0L;
    private float mDownX = 0;
    private float mDownY = 0;
    private long mLastTouchTime = 0L;

    private OnDoubleClickListener mOnDoubleClickListener = null;

    public WebContainer(@NonNull Context context) {
        this(context, null);
    }

    public WebContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.getTouchSlop();
        mTapTimeout = ViewConfiguration.getTapTimeout();
    }

    public void setOnDoubleClickListener(OnDoubleClickListener onDoubleClickListener) {
        mOnDoubleClickListener = onDoubleClickListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            default:
                break;
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                mDownTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();
                long currTime = System.currentTimeMillis();
                boolean isClick = getDistance(mDownX, mDownY, upX, upY) < mTouchSlop &&
                        currTime - mDownTime < mTapTimeout;
                if (isClick) {
                    if (currTime - mLastTouchTime < mDelay) {
                        if (mOnDoubleClickListener != null) {
                            mOnDoubleClickListener.onDoubleClick();
                        }
                    }
                    mLastTouchTime = currTime;
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(Math.abs(x2 - x1), 2) + Math.pow(Math.abs(y2 - y1), 2));
    }

    public interface OnDoubleClickListener {
        void onDoubleClick();
    }
}
