package per.goweii.wanandroid.widget.draglayout

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.*
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.Scroller
import per.goweii.statusbarcompat.StatusBarCompat
import per.goweii.wanandroid.R
import kotlin.math.abs

class DragLayout : FrameLayout, NestedScrollingParent2 {

    private val _dismissDuration = 300
    private val _dismissVelocity = 2000F
    private val _dismissFraction = 0.5F

    private val mDragHelper: ViewDragHelper = ViewDragHelper.create(this, DragCallback())
    private val mNestedHelper: NestedScrollingParentHelper = NestedScrollingParentHelper(this)
    private val mScroller: Scroller = Scroller(context, DecelerateInterpolator())

    private var mInnerScrollViews: List<View>? = null
    private var mHandleTouchEvent = false
    private var mDownX: Float = 0.toFloat()
    private var mDownY: Float = 0.toFloat()
    private var mLeft: Int = 0
    private var mRight: Int = 0
    private var mTop: Int = 0
    private var mBottom: Int = 0

    private var usingNested = false

    private var mDragFraction = 0f

    private var onDragStart: (() -> Unit)? = null
    private var onDragging: ((f: Float) -> Unit)? = null
    private var onDragEnd: (() -> Unit)? = null

    private var isEnable: Boolean = true

    private var opened = false
    private var openMargin: Int = StatusBarCompat.getHeight(context)
    private var closeMargin: Int = context.resources.getDimension(R.dimen.bottom_bar_height).toInt()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun onDragStart(listener: () -> Unit) {
        onDragStart = listener
    }

    fun onDragging(listener: (f: Float) -> Unit) {
        onDragging = listener
    }

    fun onDragEnd(listener: () -> Unit) {
        onDragEnd = listener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        assert(childCount > 0)
        dragView = getChildAt(childCount - 1)
        dragView.isClickable = true
        mInnerScrollViews = DragCompat.findAllScrollViews(dragView)
        val ws = MeasureSpec.getSize(widthMeasureSpec)
        val hs = MeasureSpec.getSize(heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        dragView.measure(
                MeasureSpec.makeMeasureSpec(ws, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(hs - openMargin, MeasureSpec.EXACTLY)
        )
    }

    private lateinit var dragView: View

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mLeft = dragView.left
        mRight = dragView.right
        mTop = dragView.top
        mBottom = dragView.bottom
        if (opened) {
            open(0)
        } else {
            close(0)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isEnable) {
            mHandleTouchEvent = false
            return super.onInterceptTouchEvent(ev)
        }
        when (ev.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mDragHelper.abort()
                mScroller.abortAnimation()
                usingNested = false
                mDownX = ev.rawX
                mDownY = ev.rawY
            }
        }
        if (usingNested) {
            mHandleTouchEvent = false
            return super.onInterceptTouchEvent(ev)
        }
        val shouldIntercept = mDragHelper.shouldInterceptTouchEvent(ev)
        mHandleTouchEvent = shouldIntercept
        when (ev.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                if (!mHandleTouchEvent) {
                    if (!DragCompat.canViewScrollUp(mInnerScrollViews, mDownX, mDownY, false)) {
                        mHandleTouchEvent = true
                    }
                }
            }
        }
        return shouldIntercept
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!isEnable) {
            return super.onTouchEvent(ev)
        }
        if (usingNested) {
            return super.onTouchEvent(ev)
        }
        mDragHelper.processTouchEvent(ev)
        return mHandleTouchEvent
    }

    override fun computeScroll() {
        if (!isEnable) {
            return
        }
        if (usingNested) {
            if (mScroller.computeScrollOffset()) {
                scrollTo(mScroller.currX, mScroller.currY)
                invalidate()
            }
        } else {
            if (mDragHelper.continueSettling(true)) {
                invalidate()
            }
        }
    }

    fun toggle(duration: Int = 300) {
        if (opened) {
            close(duration)
        } else {
            open(duration)
        }
    }

    fun open(duration: Int = 300) {
        opened = true
        if (duration > 0) {
            mScroller.startScroll(-getDragX().toInt(), -getDragY().toInt(),
                    -getDragX().toInt(), -(getMinDragY() - getDragY()).toInt(), duration)
            invalidate()
        } else {
            setDragY(getMinDragY())
        }
    }

    fun close(duration: Int = 300) {
        opened = false
        if (duration > 0) {
            mScroller.startScroll(-getDragX().toInt(), -getDragY().toInt(),
                    -getDragX().toInt(), -(getMaxDragY() - getDragY()).toInt(), duration)
            invalidate()
        } else {
            setDragY(getMaxDragY())
        }
    }

    private fun getDragX(): Float {
        return dragView.left.toFloat() - mLeft.toFloat()
    }

    private fun getDragY(): Float {
        return dragView.top.toFloat() - mTop.toFloat()
    }

    private fun getMinDragY() = 0F

    private fun getMaxDragY() = (dragView.height - closeMargin).toFloat()

    private fun getMinDragX() = 0F

    private fun getMaxDragX() = (dragView.width - closeMargin).toFloat()

    private fun setDragX(dragX: Float) {
        setDrag(dragX, getDragY())
    }

    private fun setDragY(dragY: Float) {
        setDrag(getDragX(), dragY)
    }

    private fun setDrag(dragX: Float, dragY: Float) {
        val x = dragX.range(getMinDragY(), getMaxDragY()).toInt()
        val y = dragY.range(getMinDragY(), getMaxDragY()).toInt()
        dragView.left = mLeft + x
        dragView.right = mRight + x
        dragView.top = mTop + y
        dragView.bottom = mBottom + y
        onDragChanged()
        invalidate()
    }

    private fun Float.range(min: Float, max: Float): Float {
        return when {
            this < min -> min
            this > max -> max
            else -> this
        }
    }

    override fun scrollBy(x: Int, y: Int) {
        setDrag(getDragX() - x, getDragY() - y)
    }

    override fun scrollTo(x: Int, y: Int) {
        setDrag(-x.toFloat(), -y.toFloat())
    }

    private fun onDragChanged() {
        mDragFraction = abs(getDragY()) / abs(getMaxDragY() - getMinDragY())
        if (mDragFraction < 0) {
            mDragFraction = 0f
        } else if (mDragFraction > 1) {
            mDragFraction = 1f
        }
        onDragging()
        if (mDragFraction == 1F) onDragEnd()
    }

    private fun onDragStart() {
        onDragStart?.invoke()
    }

    private fun onDragging() {
        onDragging?.invoke(mDragFraction)
    }

    private fun onDragEnd() {
        onDragEnd?.invoke()
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        usingNested = if (target is NestedScrollingChild) {
            target.canScrollVertically(-1) || target.canScrollVertically(1)
        } else {
            false
        }
        return usingNested
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        nestedScrollStopped = false
        mNestedHelper.onNestedScrollAccepted(child, target, axes, type)
        if (type == ViewCompat.TYPE_TOUCH) {
            mScroller.abortAnimation()
            velocity = 0F
            onDragStart()
        }
    }

    private var velocity: Float = 0F

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return if (nestedScrollStopped) {
            false
        } else {
            super.onNestedFling(target, velocityX, velocityY, consumed)
        }
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        velocity = velocityY
        return if (nestedScrollStopped) {
            false
        } else {
            return super.onNestedPreFling(target, velocityX, velocityY)
        }
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        target as ScrollingView
        val scrollY: Int = -getDragY().toInt()
        consumed[0] = 0
        consumed[1] = if (dy > 0) {
            if (scrollY < 0) {
                if (scrollY + dy > 0) {
                    -scrollY
                } else {
                    dy
                }
            } else {
                0
            }
        } else if (dy < 0) {
            if (scrollY < 0) {
                dy
            } else {
                if (target.canScrollVertically(-1)) {
                    0
                } else {
                    if (type == ViewCompat.TYPE_NON_TOUCH) {
                        if (scrollY + dy > 0) {
                            -scrollY
                        } else {
                            0
                        }
                    } else {
                        dy
                    }
                }
            }
        } else {
            0
        }
        scrollBy(consumed[0], consumed[1])
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int/*, consumed: IntArray*/) {
        val scrollY: Int = -getDragY().toInt()
        val consumedx = 0
        val consumedy = if (scrollY + dyUnconsumed > 0) {
            -scrollY
        } else {
            if (type == ViewCompat.TYPE_NON_TOUCH) {
                0
            } else {
                dyUnconsumed
            }
        }
        scrollBy(consumedx, consumedy)
    }

    private var nestedScrollStopped = false

    override fun onStopNestedScroll(target: View, type: Int) {
        mNestedHelper.onStopNestedScroll(target, type)
        if (type == ViewCompat.TYPE_TOUCH) {
            if (!target.canScrollVertically(-1)) {
                nestedScrollStopped = true
                val openOrClose = when {
                    velocity > _dismissVelocity -> true
                    velocity < -_dismissVelocity -> false
                    else -> mDragFraction < _dismissFraction
                }
                val f = abs(velocity) / _dismissVelocity
                val duration: Int = if (f <= 1) {
                    _dismissDuration
                } else {
                    val d = (_dismissDuration / f).toInt()
                    if (d < 100) 100 else d
                }
                if (openOrClose) {
                    open(duration)
                } else {
                    close(duration)
                }
            }
        }
    }

    override fun getNestedScrollAxes(): Int {
        return mNestedHelper.nestedScrollAxes
    }

    private inner class DragCallback : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            Log.d("DragLayout", "tryCaptureView")
            if (!isEnable) return false
            if (usingNested) return false
            return child == dragView && !DragCompat.canViewScrollUp(mInnerScrollViews, mDownX, mDownY, false)
        }

        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            super.onViewCaptured(capturedChild, activePointerId)
            Log.d("DragLayout", "onViewCaptured")
            mDragFraction = 0f
            onDragStart()
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            Log.d("DragLayout", "getViewHorizontalDragRange")
            return 0
        }

        override fun getViewVerticalDragRange(child: View): Int {
            Log.d("DragLayout", "getViewVerticalDragRange")
            return height - mTop - closeMargin
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            Log.d("DragLayout", "clampViewPositionHorizontal")
            return mLeft
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            Log.d("DragLayout", "clampViewPositionVertical")
            val pv = if (top > height - closeMargin) {
                height - closeMargin
            } else {
                if (top < mTop) {
                    mTop
                } else {
                    top
                }
            }
            Log.d("DragLayout", "clampViewPositionVertical->$pv")
            return pv
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
            Log.d("DragLayout", "onViewPositionChanged->dx=$dx,dy=$dy")
            val yoff = abs(top - mTop).toFloat()
            val ymax = getViewVerticalDragRange(changedView).toFloat()
            mDragFraction = yoff / ymax
            if (mDragFraction < 0) {
                mDragFraction = 0F
            } else if (mDragFraction > 1) {
                mDragFraction = 1F
            }
            Log.d("DragLayout", "onViewPositionChanged->mDragFraction=$mDragFraction")
            onDragging()
            if (mDragFraction == 1F) onDragEnd()
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)
            Log.d("DragLayout", "onViewReleased->yvel=$yvel")
            val openOrClose = when {
                yvel > _dismissVelocity -> false
                yvel < -_dismissVelocity -> true
                else -> mDragFraction < _dismissFraction
            }
            val l = mLeft
            val t = if (openOrClose) mTop else height - closeMargin
            mDragHelper.settleCapturedViewAt(l, t)
            invalidate()
        }
    }
}
