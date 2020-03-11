package per.goweii.wanandroid.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import com.tencent.smtt.sdk.QbSdk;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import per.goweii.basic.utils.ColorUtils;
import per.goweii.heartview.HeartView;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.utils.SettingUtils;

/**
 * @author CuiZhen
 * @date 2019/8/31
 * GitHub: https://github.com/goweii
 */
public class WebContainer extends FrameLayout {

    private final float mTouchSlop;
    private final long mTapTimeout;
    private final long mDoubleTapTimeout;
    private boolean mDarkMaskEnable;

    private int mMaskColor = Color.TRANSPARENT;

    private long mDownTime = 0L;
    private float mDownX = 0;
    private float mDownY = 0;
    private float mLastDownX = 0;
    private float mLastDownY = 0;
    private long mLastTouchTime = 0L;

    private OnDoubleClickListener mOnDoubleClickListener = null;
    private OnTouchDownListener mOnTouchDownListener = null;

    private List<Animator> mHeartAnimators = new LinkedList<>();

    private boolean doubleClicked = false;
    private Runnable doubleClickTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            doubleClicked = false;
        }
    };

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
        mDoubleTapTimeout = ViewConfiguration.getDoubleTapTimeout();

        mDarkMaskEnable = SettingUtils.getInstance().isDarkTheme() && !QbSdk.canLoadX5(context);
        if (mDarkMaskEnable) {
            mMaskColor = ColorUtils.alphaColor(ContextCompat.getColor(getContext(), R.color.background), 0.4F);
        }
    }

    public void setDarkMaskEnable(boolean enable) {
        mDarkMaskEnable = enable;
    }

    public void setOnDoubleClickListener(OnDoubleClickListener onDoubleClickListener) {
        mOnDoubleClickListener = onDoubleClickListener;
    }

    public void setOnTouchDownListener(OnTouchDownListener onTouchDownListener) {
        mOnTouchDownListener = onTouchDownListener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDownX = getWidth() / 2;
        mDownY = getHeight() / 2;
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
                onTouchDown();
                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();
                long currTime = System.currentTimeMillis();
                boolean inTouchSlop = getDistance(mDownX, mDownY, upX, upY) < mTouchSlop;
                boolean inTapTimeout = currTime - mDownTime < mTapTimeout;
                boolean isClick = inTouchSlop && inTapTimeout;
                if (isClick) {
                    if (currTime - mLastTouchTime < mDoubleTapTimeout) {
                        if (getDistance(mDownX, mDownY, mLastDownX, mLastDownY) < mTouchSlop * 5) {
                            if (!doubleClicked) {
                                doubleClicked = true;
                                onDoubleClicked(upX, upY);
                            }
                        }
                    }
                    mLastDownX = mDownX;
                    mLastDownY = mDownY;
                    mLastTouchTime = currTime;
                }
                if (doubleClicked && inTouchSlop) {
                    onDoubleClicking(upX, upY);
                }
                removeCallbacks(doubleClickTimeoutRunnable);
                postDelayed(doubleClickTimeoutRunnable, mDoubleTapTimeout * 3);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mDarkMaskEnable) {
            canvas.drawColor(mMaskColor);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        for (Animator animator : mHeartAnimators) {
            animator.cancel();
        }
        mHeartAnimators.clear();
        super.onDetachedFromWindow();
    }

    private double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(Math.abs(x2 - x1), 2) + Math.pow(Math.abs(y2 - y1), 2));
    }

    private void onDoubleClicked(float x, float y) {
        if (mOnDoubleClickListener != null) {
            mOnDoubleClickListener.onDoubleClick(x, y);
        }
    }

    private void onDoubleClicking(float x, float y) {
        showHeartAnim(x, y);
    }

    private void onTouchDown() {
        if (mOnTouchDownListener != null) {
            mOnTouchDownListener.onTouchDown();
        }
    }

    public void showHeartAnim(float x, float y) {
        final View heartView = createHeartView();
        heartView.setVisibility(View.INVISIBLE);
        heartView.post(new Runnable() {
            @Override
            public void run() {
                heartView.setPivotX(heartView.getWidth() / 2F);
                heartView.setPivotY(heartView.getHeight());
                heartView.setX(x - heartView.getWidth() / 2F);
                heartView.setY(y - heartView.getHeight());
                Animator animator = createHeartAnim(heartView);
                mHeartAnimators.add(animator);
                animator.start();
            }
        });
        addView(heartView);
    }

    private final Random mRandom = new Random();

    public Animator createHeartAnim(final View heartView) {
        final float rotation;
        final float random = mRandom.nextFloat();
        if (random < 0.3F) {
            rotation = 15F;
        } else if (random > 0.7F) {
            rotation = -15F;
        } else {
            rotation = 0F;
        }
        heartView.setRotation(rotation);
        ObjectAnimator alphaIn = ObjectAnimator.ofFloat(heartView, "alpha", 0F, 1F);
        alphaIn.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator scaleXIn = ObjectAnimator.ofFloat(heartView, "scaleX", 0.3F, 1F);
        scaleXIn.setInterpolator(new OvershootInterpolator());
        ObjectAnimator scaleYIn = ObjectAnimator.ofFloat(heartView, "scaleY", 0.3F, 1F);
        scaleYIn.setInterpolator(new OvershootInterpolator());
        AnimatorSet setIn = new AnimatorSet();
        setIn.playTogether(alphaIn, scaleXIn, scaleYIn);
        setIn.setDuration(300);
        ObjectAnimator alphaOut = ObjectAnimator.ofFloat(heartView, "alpha", 1F, 0F);
        alphaOut.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator scaleXOut = ObjectAnimator.ofFloat(heartView, "scaleX", 1F, 1.5F);
        scaleXOut.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator scaleYOut = ObjectAnimator.ofFloat(heartView, "scaleY", 1F, 1.5F);
        scaleYOut.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator transYOut = ObjectAnimator.ofFloat(heartView, "translationY",
                heartView.getTranslationY(), heartView.getTranslationY() - heartView.getHeight() * 1.5F);
        transYOut.setInterpolator(new AccelerateInterpolator());
        AnimatorSet setOut = new AnimatorSet();
        setOut.playTogether(alphaOut, scaleXOut, scaleYOut, transYOut);
        setOut.setDuration(500);
        setOut.setStartDelay(200);
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(setIn, setOut);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                heartView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(heartView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        return set;
    }

    private View createHeartView() {
        HeartView heartView = new HeartView(getContext());
        heartView.setCenter(-0.5f, -0.5f);
        heartView.setRadiusPercent(0.6F);
        heartView.setColor(ContextCompat.getColor(getContext(), R.color.heart_center));
        heartView.setEdgeColor(ContextCompat.getColor(getContext(), R.color.accent));
        heartView.setStrokeWidthDp(0);
        int size = (int) (getWidth() * 0.27F);
        heartView.setLayoutParams(new LayoutParams(size, size));
        return heartView;
    }

    public interface OnDoubleClickListener {
        void onDoubleClick(float x, float y);
    }

    public interface OnTouchDownListener {
        void onTouchDown();
    }
}
