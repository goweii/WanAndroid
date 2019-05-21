package per.goweii.swipeback;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by zh on 2018/10/21.
 */

public class SwipeBackLayout extends FrameLayout {

    //左侧边缘阴影最大透明度
    private static final int FULL_ALPHA = 255;

    private final ViewDragHelper mDragHelper;
    private Drawable mShadowLeft;
    private float mScrimOpacity = 1;
    private Rect mTmpRect = new Rect();
    private View mPreviousChild;
    private Activity mTopActivity;//执行滑动时的最顶层Activity
    private WeakReference<Activity> mBackActivityWeakRf;//后面的Activity的弱引用
    private boolean mActivityTranslucent = true;//界面是否是透明的
    private boolean mActivitySwiping = false;//界面是否正在滑动

    private boolean mFinishAnimEnable = true;//Activity关闭底层动画
    private boolean mSwipeBackEnable = true;//是否允许滑动
    private boolean mForceEdgeEnable = true;
    private boolean mOnlyFromEdge = false;
    @SwipeDirection
    private int mSwipeDirection = SwipeDirection.FROM_LEFT;
    private View mDragContentView;
    private List<View> innerScrollViews;

    private int width, height;

    private int mTouchSlop;
    private float swipeBackFactor = 0.5f;
    private float swipeBackFraction;//界面滑动进度
    private int maskAlpha = 180;//底层阴影初始透明度
    private float downX, downY;

    private int leftOffset = 0;
    private int topOffset = 0;
    private float autoFinishedVelocityLimit = 2000f;
    private int touchedEdge = ViewDragHelper.INVALID_POINTER;

    public SwipeBackLayout(@NonNull Context context) {
        this(context, null);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
        mDragHelper.setEdgeTrackingEnabled(mSwipeDirection);
        mTouchSlop = mDragHelper.getTouchSlop();
        setSwipeBackListener(mSwipeBackListenerDefault);

        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeBackLayout);
        setSwipeDirection(a.getInt(R.styleable.SwipeBackLayout_directionMode, mSwipeDirection));
        setSwipeBackFactor(a.getFloat(R.styleable.SwipeBackLayout_swipeBackFactor, swipeBackFactor));
        setMaskAlpha(a.getInteger(R.styleable.SwipeBackLayout_maskAlpha, maskAlpha));
        mOnlyFromEdge = a.getBoolean(R.styleable.SwipeBackLayout_isSwipeFromEdge, mOnlyFromEdge);
        setShadow(R.drawable.shadow_left);
        a.recycle();
    }

    public void attachToActivity(Activity activity) {
        TypedArray a = activity.getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.windowBackground
        });
        int background = a.getResourceId(0, 0);
        a.recycle();
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
        decorChild.setBackgroundResource(background);
        decorView.removeView(decorChild);
        addView(decorChild);
        mDragContentView = decorChild;
        decorView.addView(this);

        mTopActivity = activity;
        Activity backActivity = SwipeBackManager.getInstance().getPenultimateActivity();
        if (backActivity instanceof SwipeBackActivity) {
            mBackActivityWeakRf = new WeakReference<>(backActivity);
            SwipeBackLayout previousSlideLayout = ((SwipeBackActivity) backActivity).getSwipeBackLayout();
            if (previousSlideLayout != null) {
                mPreviousChild = previousSlideLayout.getChildAt(0);
            }
        }
    }

    /**
     * 绑定
     */
    public void bind() {
        if (mFinishAnimEnable && mPreviousChild != null) {
            ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
            long dur = mPreviousChild.getResources().getInteger(android.R.integer.config_shortAnimTime);
            animator.setDuration(dur);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float f = (float) animation.getAnimatedValue();
                    if (mPreviousActivityTransformer == null) {
                        mPreviousActivityTransformer = mPreviousActivityTransformerDefault;
                    }
                    mPreviousActivityTransformer.anim(mPreviousChild, f);
                }
            });
            animator.start();
        }
    }

    /**
     * 上层滑动关闭，底层恢复缩放动画
     */
    public void startFinishAnim() {
        if (mFinishAnimEnable && mPreviousChild != null) {
            ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
            long dur = mPreviousChild.getResources().getInteger(android.R.integer.config_shortAnimTime);
            animator.setDuration(dur);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float f = (float) animation.getAnimatedValue();
                    if (mPreviousActivityTransformer == null) {
                        mPreviousActivityTransformer = mPreviousActivityTransformerDefault;
                    }
                    mPreviousActivityTransformer.anim(mPreviousChild, f);
                }
            });
            animator.start();
        }
    }

    public boolean isActivitySwiping() {
        return mActivitySwiping;
    }

    public boolean isActivityTranslucent() {
        return mActivityTranslucent;
    }

    public void setActivityTranslucent(boolean activityTranslucent) {
        mActivityTranslucent = activityTranslucent;
    }

    public void setFinishAnimEnable(boolean enable) {
        mFinishAnimEnable = enable;
    }

    public boolean isFinishAnimEnable() {
        return mFinishAnimEnable;
    }

    public void setSwipeBackEnable(boolean enable) {
        mSwipeBackEnable = enable;
    }

    public boolean isSwipeBackEnable() {
        return mSwipeBackEnable;
    }

    public void setForceEdgeEnable(boolean enable) {
        mForceEdgeEnable = enable;
    }

    public boolean isForceEdgeEnable() {
        return mForceEdgeEnable;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        try {
            if (!isSwipeBackEnable()) {
                super.onLayout(changed, l, t, r, b);
                return;
            }
            int left = getPaddingLeft() + leftOffset;
            int top = getPaddingTop() + topOffset;
            int right = left + mDragContentView.getMeasuredWidth();
            int bottom = top + mDragContentView.getMeasuredHeight();
            mDragContentView.layout(left, top, right, bottom);
            if (changed) {
                width = getWidth();
                height = getHeight();
            }
            innerScrollViews = SwipeChecker.findAllScrollViews2(this);
        } catch (Exception e) {
            super.onLayout(changed, l, t, r, b);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSwipeBackEnable()) {
            //绘制底层阴影
            canvas.drawARGB(maskAlpha - (int) (maskAlpha * swipeBackFraction), 0, 0, 0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isSwipeBackEnable()) {
            return false;
        }
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getRawX();
                downY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (innerScrollViews != null && SwipeChecker.contains(innerScrollViews, downX, downY) != null) {
                    float distanceX = Math.abs(ev.getRawX() - downX);
                    float distanceY = Math.abs(ev.getRawY() - downY);
                    if (mSwipeDirection == SwipeDirection.FROM_LEFT || mSwipeDirection == SwipeDirection.FROM_RIGHT) {
                        if (distanceY > mTouchSlop && distanceY > distanceX) {
                            return super.onInterceptTouchEvent(ev);
                        }
                    } else if (mSwipeDirection == SwipeDirection.FROM_TOP || mSwipeDirection == SwipeDirection.FROM_BOTTOM) {
                        if (distanceX > mTouchSlop && distanceX > distanceY) {
                            return super.onInterceptTouchEvent(ev);
                        }
                    }
                }
                break;
            default:
                break;
        }
        boolean handled = mDragHelper.shouldInterceptTouchEvent(ev);
        return handled ? handled : super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isSwipeBackEnable()) {
            return false;
        }
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void smoothScrollToX(int finalLeft) {
        if (mDragHelper.settleCapturedViewAt(finalLeft, getPaddingTop())) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void smoothScrollToY(int finalTop) {
        if (mDragHelper.settleCapturedViewAt(getPaddingLeft(), finalTop)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (isSwipeBackEnable()) {
                mActivitySwiping = true;
                if (null != mTopActivity && !mActivityTranslucent) {
                    SwipeChecker.convertActivityToTranslucent(mTopActivity);
                    mActivityTranslucent = true;
                }
                return child == mDragContentView;
            }
            return false;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            leftOffset = getPaddingLeft();
            if (isSwipeEnabled()) {
                if (mSwipeDirection == SwipeDirection.FROM_LEFT) {
                    if (!SwipeChecker.canViewScrollRight(innerScrollViews, downX, downY, false)) {
                        leftOffset = Math.min(Math.max(left, getPaddingLeft()), width);
                    } else {
                        if (mForceEdgeEnable && touchedEdge == ViewDragHelper.EDGE_LEFT) {
                            leftOffset = Math.min(Math.max(left, getPaddingLeft()), width);
                        }
                    }
                } else if (mSwipeDirection == SwipeDirection.FROM_RIGHT) {
                    if (!SwipeChecker.canViewScrollLeft(innerScrollViews, downX, downY, false)) {
                        leftOffset = Math.min(Math.max(left, -width), getPaddingRight());
                    } else {
                        if (mForceEdgeEnable && touchedEdge == ViewDragHelper.EDGE_RIGHT) {
                            leftOffset = Math.min(Math.max(left, -width), getPaddingRight());
                        }
                    }
                }
            }
            return leftOffset;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            topOffset = getPaddingTop();
            if (isSwipeEnabled()) {
                if (mSwipeDirection == SwipeDirection.FROM_TOP) {
                    if (!SwipeChecker.canViewScrollUp(innerScrollViews, downX, downY, false)) {
                        topOffset = Math.min(Math.max(top, getPaddingTop()), height);
                    } else {
                        if (mForceEdgeEnable && touchedEdge == ViewDragHelper.EDGE_TOP) {
                            topOffset = Math.min(Math.max(top, getPaddingTop()), height);
                        }
                    }
                } else if (mSwipeDirection == SwipeDirection.FROM_RIGHT) {
                    if (!SwipeChecker.canViewScrollDown(innerScrollViews, downX, downY, false)) {
                        topOffset = Math.min(Math.max(top, -height), getPaddingBottom());
                    } else {
                        if (mForceEdgeEnable && touchedEdge == ViewDragHelper.EDGE_BOTTOM) {
                            topOffset = Math.min(Math.max(top, -height), getPaddingBottom());
                        }
                    }
                }
            }
            return topOffset;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            left = Math.abs(left);
            top = Math.abs(top);
            switch (mSwipeDirection) {
                case SwipeDirection.FROM_LEFT:
                case SwipeDirection.FROM_RIGHT:
                    swipeBackFraction = 1.0f * left / width;
                    break;
                case SwipeDirection.FROM_TOP:
                case SwipeDirection.FROM_BOTTOM:
                    swipeBackFraction = 1.0f * top / height;
                    break;
                default:
                    break;
            }
            if (mSwipeBackListener != null) {
                mSwipeBackListener.onViewPositionChanged(mDragContentView, swipeBackFraction, swipeBackFactor);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            leftOffset = topOffset = 0;
            if (!isSwipeEnabled()) {
                touchedEdge = ViewDragHelper.INVALID_POINTER;
                return;
            }
            touchedEdge = ViewDragHelper.INVALID_POINTER;

            boolean isBackToEnd = backJudgeBySpeed(xvel, yvel) || swipeBackFraction >= swipeBackFactor;
            if (isBackToEnd) {
                switch (mSwipeDirection) {
                    case SwipeDirection.FROM_LEFT:
                        //滑动关闭
                        smoothScrollToX(width);
                        break;
                    case SwipeDirection.FROM_TOP:
                        smoothScrollToY(height);
                        break;
                    case SwipeDirection.FROM_RIGHT:
                        smoothScrollToX(-width);
                        break;
                    case SwipeDirection.FROM_BOTTOM:
                        smoothScrollToY(-height);
                        break;
                    default:
                        break;
                }
            } else {
                switch (mSwipeDirection) {
                    case SwipeDirection.FROM_LEFT:
                    case SwipeDirection.FROM_RIGHT:
                        //滑动返回
                        smoothScrollToX(getPaddingLeft());
                        break;
                    case SwipeDirection.FROM_BOTTOM:
                    case SwipeDirection.FROM_TOP:
                        smoothScrollToY(getPaddingTop());
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (state == ViewDragHelper.STATE_IDLE) {
                if (mSwipeBackListener != null) {
                    if (swipeBackFraction == 0) {
                        mSwipeBackListener.onViewSwipeFinished(mDragContentView, false);
                    } else if (swipeBackFraction == 1) {
                        mSwipeBackListener.onViewSwipeFinished(mDragContentView, true);
                    }
                }
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return width;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return height;
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
            //边缘Touch状态 开始滑动
            touchedEdge = edgeFlags;
        }
    }

    private boolean isSwipeEnabled() {
        if (mOnlyFromEdge) {
            switch (mSwipeDirection) {
                case SwipeDirection.FROM_LEFT:
                    return touchedEdge == ViewDragHelper.EDGE_LEFT;
                case SwipeDirection.FROM_TOP:
                    return touchedEdge == ViewDragHelper.EDGE_TOP;
                case SwipeDirection.FROM_RIGHT:
                    return touchedEdge == ViewDragHelper.EDGE_RIGHT;
                case SwipeDirection.FROM_BOTTOM:
                    return touchedEdge == ViewDragHelper.EDGE_BOTTOM;
                default:
                    break;
            }
        }
        return true;
    }

    private boolean backJudgeBySpeed(float xvel, float yvel) {
        switch (mSwipeDirection) {
            case SwipeDirection.FROM_LEFT:
                return xvel > autoFinishedVelocityLimit;
            case SwipeDirection.FROM_TOP:
                return yvel > autoFinishedVelocityLimit;
            case SwipeDirection.FROM_RIGHT:
                return xvel < -autoFinishedVelocityLimit;
            case SwipeDirection.FROM_BOTTOM:
                return yvel < -autoFinishedVelocityLimit;
            default:
                break;
        }
        return false;
    }

    public void setSwipeBackFactor(@FloatRange(from = 0.0f, to = 1.0f) float swipeBackFactor) {
        if (swipeBackFactor > 1) {
            swipeBackFactor = 1;
        } else if (swipeBackFactor < 0) {
            swipeBackFactor = 0;
        }
        this.swipeBackFactor = swipeBackFactor;
    }

    public float getSwipeBackFactor() {
        return swipeBackFactor;
    }

    public void setMaskAlpha(@IntRange(from = 0, to = 255) int maskAlpha) {
        if (maskAlpha > 255) {
            maskAlpha = 255;
        } else if (maskAlpha < 0) {
            maskAlpha = 0;
        }
        this.maskAlpha = maskAlpha;
    }

    public int getMaskAlpha() {
        return maskAlpha;
    }

    public void setSwipeDirection(@SwipeDirection int direction) {
        mSwipeDirection = direction;
        mDragHelper.setEdgeTrackingEnabled(direction);
    }

    public int getSwipeDirection() {
        return mSwipeDirection;
    }

    public float getAutoFinishedVelocityLimit() {
        return autoFinishedVelocityLimit;
    }

    public void setAutoFinishedVelocityLimit(float autoFinishedVelocityLimit) {
        this.autoFinishedVelocityLimit = autoFinishedVelocityLimit;
    }

    public boolean isOnlyFromEdge() {
        return mOnlyFromEdge;
    }

    public void setOnlyFromEdge(boolean onlyFromEdge) {
        this.mOnlyFromEdge = onlyFromEdge;
    }

    private OnSwipeBackListener mSwipeBackListener;
    private PreviousActivityTransformer mPreviousActivityTransformer;

    private final PreviousActivityTransformer mPreviousActivityTransformerDefault = new PreviousActivityTransformer() {
        @Override
        public void anim(View view, float fraction) {
            float scale = 0.96f + (1 - 0.96f) * fraction;
            view.setScaleX(scale);
            view.setScaleY(scale);
        }
    };

    public void setPreviousActivityTransformer(PreviousActivityTransformer previousActivityTransformer) {
        mPreviousActivityTransformer = previousActivityTransformer;
    }

    public interface PreviousActivityTransformer {
        void anim(View view, float fraction);
    }

    private final OnSwipeBackListener mSwipeBackListenerDefault = new OnSwipeBackListener() {
        @Override
        public void onViewPositionChanged(View mView, float swipeBackFraction, float swipeBackFactor) {
            invalidate();
            //滑动中改变缩放度
            if (mPreviousChild != null) {
                if (mPreviousActivityTransformer == null) {
                    mPreviousActivityTransformer = mPreviousActivityTransformerDefault;
                }
                mPreviousActivityTransformer.anim(mPreviousChild, swipeBackFraction);
            }
        }

        @Override
        public void onViewSwipeFinished(View mView, boolean isEnd) {
            if (isEnd) {
                //上层界面滑动消失，finish掉该界面
                finish();
            } else {
                //上层界面恢复滑动
                mActivitySwiping = false;
                if (null != mTopActivity && mActivityTranslucent) {
                    SwipeChecker.convertActivityFromTranslucent(mTopActivity);
                    mActivityTranslucent = false;
                }
            }
        }
    };

    public void finish() {
        mFinishAnimEnable = false;
        ((Activity) getContext()).finish();
        //去除默认动画，防止关闭时出现阴影
        ((Activity) getContext()).overridePendingTransition(0, 0);
    }

    public void setSwipeBackListener(OnSwipeBackListener mSwipeBackListener) {
        this.mSwipeBackListener = mSwipeBackListener;
    }

    public interface OnSwipeBackListener {

        void onViewPositionChanged(View mView, float swipeBackFraction, float swipeBackFactor);

        void onViewSwipeFinished(View mView, boolean isEnd);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (!isSwipeBackEnable()) {
            return super.drawChild(canvas, child, drawingTime);
        }
        final boolean drawContent = child == mDragContentView;
        boolean ret = super.drawChild(canvas, child, drawingTime);
        if (mScrimOpacity > 0 && drawContent
                && mDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
            drawShadow(canvas, child);
        }
        return ret;
    }

    /**
     * 设置左侧边缘阴影图片
     */
    public void setShadow(int resId) {
        Drawable shadow = getResources().getDrawable(resId);
        if (mSwipeDirection == SwipeDirection.FROM_LEFT) {
            mShadowLeft = shadow;
        }
        invalidate();
    }

    /**
     * 左侧边缘阴影
     *
     * @param canvas
     * @param child
     */
    private void drawShadow(Canvas canvas, View child) {
        final Rect childRect = mTmpRect;
        child.getHitRect(childRect);
        if (mSwipeDirection == SwipeDirection.FROM_LEFT) {
            mShadowLeft.setBounds(childRect.left - mShadowLeft.getIntrinsicWidth(), childRect.top,
                    childRect.left, childRect.bottom);
            mShadowLeft.setAlpha((int) ((1 - swipeBackFraction) * FULL_ALPHA));
            mShadowLeft.draw(canvas);
        }
    }
}
