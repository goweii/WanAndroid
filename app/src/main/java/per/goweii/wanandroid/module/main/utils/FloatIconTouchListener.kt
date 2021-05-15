package per.goweii.wanandroid.module.main.utils

import android.annotation.SuppressLint
import android.graphics.PointF
import android.graphics.Rect
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.abs

/**
 * @author CuiZhen
 * @date 2020/4/19
 */
@SuppressLint("ClickableViewAccessibility")
class FloatIconTouchListener(
        private val icons: List<Icon>,
        private val onFloatTouchedListener: OnFloatTouchedListener
) : View.OnTouchListener, Runnable {

    data class Icon(val view: View, var touched: Boolean = false)

    private lateinit var icon: Icon

    private val longPressTimeout = ViewConfiguration.getLongPressTimeout()
    private val touchSlop by lazy { ViewConfiguration.get(icon.view.context).scaledTouchSlop }

    private var touchTime = 0L
    private var touchMoved = false
    private val touchPoint = PointF()
    private var currTouchIcon: Icon? = null
    private var moveOutside = false

    private val location = intArrayOf(0, 0)
    private val rect = Rect()

    override fun onTouch(v: View, e: MotionEvent): Boolean {
        if (!this::icon.isInitialized) icon = Icon(v)
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                icon.touched = false
                icons.forEach { it.touched = false }
                touchPoint.x = e.rawX
                touchPoint.y = e.rawY
                touchTime = System.currentTimeMillis()
                touchMoved = false
                moveOutside = false
                currTouchIcon = null
                icon.view.postDelayed(this, longPressTimeout.toLong())
            }
            MotionEvent.ACTION_MOVE -> {
                if (abs(e.rawX - touchPoint.x) > touchSlop || abs(e.rawY - touchPoint.y) > touchSlop) {
                    touchMoved = true
                    icon.view.removeCallbacks(this)
                }
                rect.set(0, 0, icon.view.width, icon.view.height)
                icon.touched = rect.contains(e.x.toInt(), e.y.toInt())
                icons.forEach {
                    it.view.getLocationOnScreen(location)
                    rect.set(location[0], location[1], location[0] + it.view.width, location[1] + it.view.height)
                    if (rect.contains(e.rawX.toInt(), e.rawY.toInt())) {
                        if (!it.touched) {
                            it.touched = true
                        }
                    } else {
                        it.touched = false
                    }
                }
                var touchedCount = 0
                if (icon.touched) touchedCount++
                icons.forEach {
                    if (it.touched) touchedCount++
                }
                if (touchedCount != 1) {
                    if (currTouchIcon != null) {
                        currTouchIcon = null
                        onFloatTouchedListener.onTouched(null)
                    }
                } else {
                    if (icon.touched) {
                        if (moveOutside && currTouchIcon != icon) {
                            currTouchIcon = icon
                            icon.view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onFloatTouchedListener.onTouched(icon.view)
                        }
                    } else {
                        moveOutside = true
                        var touchedIcon: Icon? = null
                        icons.forEach {
                            if (it.touched) {
                                touchedIcon = it
                                return@forEach
                            }
                        }
                        if (currTouchIcon != touchedIcon) {
                            currTouchIcon = touchedIcon
                            touchedIcon!!.view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onFloatTouchedListener.onTouched(touchedIcon!!.view)
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                icon.view.removeCallbacks(this)
                if (!touchMoved) {
                    val upTime = System.currentTimeMillis()
                    if (upTime - touchTime < longPressTimeout) {
                        icon.view.performClick()
                    }
                } else {
                    currTouchIcon?.let {
                        if (it == icon) {
                            it.view.performClick()
                        } else {
                            it.view.performClick()
                        }
                    }
                }
            }
        }
        return true
    }

    override fun run() {
        icon.view.performLongClick()
    }

    interface OnFloatTouchedListener {
        fun onTouched(v: View?)
    }
}