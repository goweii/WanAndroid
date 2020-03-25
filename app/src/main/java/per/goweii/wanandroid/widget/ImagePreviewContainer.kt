package per.goweii.wanandroid.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.ScrollingView

/**
 * @author CuiZhen
 * @date 2020/3/7
 */
class ImagePreviewContainer : FrameLayout, ScrollingView {

    private var imagePreviewView: ImagePreviewView? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet?) : super(context, attr)
    constructor(context: Context, attr: AttributeSet?, defStyle: Int) : super(context, attr, defStyle)

    override fun onFinishInflate() {
        super.onFinishInflate()
        for (i in 0 until childCount) {
            val v = getChildAt(i)
            if (v is ImagePreviewView) {
                imagePreviewView = v
                break
            }
        }
    }

    private fun isScaled() = imagePreviewView?.isScaled() ?: false

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
        fun onTouchingUp()
        fun onLongClick()
    }
}