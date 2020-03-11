package per.goweii.wanandroid.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.view.ScrollingView
import com.github.chrisbanes.photoview.PhotoView

/**
 * @author CuiZhen
 * @date 2020/3/7
 */
class ImagePreviewView : PhotoView, ScrollingView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attr: AttributeSet?) : super(context, attr)
    constructor(context: Context?, attr: AttributeSet?, defStyle: Int) : super(context, attr, defStyle)

    init {
        setOnViewTapListener { view, x, y ->
            onImagePreviewListener?.onTap()
        }
        setOnLongClickListener {
            onImagePreviewListener?.onLongClick()
            return@setOnLongClickListener true
        }
    }

    var onImagePreviewListener: OnImagePreviewListener? = null

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount > 1) {
            onImagePreviewListener?.onTouching2()
        } else {
            onImagePreviewListener?.onTouching1()
        }
        return super.dispatchTouchEvent(event)
    }

    private fun isScaled() = scale > 1F

    override fun computeHorizontalScrollRange(): Int = if (isScaled()) width * 3 else 0

    override fun computeHorizontalScrollOffset(): Int = if (isScaled()) width * 1 else 0

    override fun computeHorizontalScrollExtent(): Int = if (isScaled()) width * 1 else 0

    override fun computeVerticalScrollRange(): Int = if (isScaled()) height * 3 else 0

    override fun computeVerticalScrollOffset(): Int = if (isScaled()) height * 1 else 0

    override fun computeVerticalScrollExtent(): Int = if (isScaled()) height * 1 else 0

    override fun canScrollHorizontally(direction: Int): Boolean {
        return isScaled()
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return isScaled()
    }

    interface OnImagePreviewListener {
        fun onTap()
        fun onTouching1()
        fun onTouching2()
        fun onLongClick()
    }
}